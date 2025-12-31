```
package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 用户端订单分页查询
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult pageQuery4User(int page, int pageSize, Integer status);

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    OrderVO details(Long id);

    /**
     * 用户取消订单
     * @param id
     */
    void userCancelById(Long id) throws Exception;

    /**
     * 再来一单
     * @param id
     */
    void repetition(Long id);

    /**
     * 条件搜索订单
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 各个状态的订单数量统计
     * @return
     */
    OrderStatisticsVO statistics();

    /**
     * 接单
     *
     * @param ordersConfirmDTO
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒单
     *
     * @param ordersRejectionDTO
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception;

    /**
     * 商家取消订单
     *
     * @param ordersCancelDTO
     */
    void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception;

    /**
     * 派送订单
     *
     * @param id
     */
    void delivery(Long id);

    /**
     * 完成订单
     *
     * @param id
     */
    void complete(Long id);

    /**
     * 客户催单
     * @param id
     */
    void reminder(Long id);
}

```

这段 OrderServiceImpl 非常典型，正好把你前面学的 常量 / enum / BaseContext / 自定义异常 都串起来了。我帮你用「整体结构 + 关键点 + 可以优化的地方」给你梳理一遍，方便你以后在项目或面试里讲。

⸻

1️⃣ 整体在干嘛？（一句话）

# OrderServiceImpl = 订单全生命周期的业务服务类，包含：
# 下单 → 支付 → 支付成功回调 → 订单查询 → 取消 / 拒单 / 派送 / 完成 → 催单 → 订单统计。

# 配合的东西：
```
- MessageConstant：提示文案
- OrderBusinessException / AddressBookBusinessException / ShoppingCartBusinessException：业务异常
- BaseContext：获取当前登录用户 ID
- WeChatPayUtil：调用微信支付 / 退款
- HttpClientUtil + 百度地图 API：检查配送范围
- WebSocketServer：给后台管理端推送「来单提醒 / 催单」消息
- PageHelper：分页查询
```
⸻

2️⃣ 核心方法速览（按功能块）

# 🛒 2.1 用户下单：submitOrder

关键逻辑：

## 1.	校验地址簿是否存在
```
AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
if (addressBook == null) {
    throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
}
```

## 2.	查询当前用户购物车
```
Long userId = BaseContext.getCurrentId();
...
if (shoppingCartList == null || shoppingCartList.size() == 0) {
    throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
}
```

## 3.	生成订单主表 Orders，设置：
- orderTime
- payStatus = Orders.UN_PAID
- status = Orders.PENDING_PAYMENT
- number = System.currentTimeMillis()（订单号）
- 收货人、电话、地址、userId 等

## 4.	把购物车里的每一条转成订单明细 OrderDetail，批量插入

## 5.	清空购物车
	
## 6.	返回 OrderSubmitVO（订单号 + 时间 + 金额）

👉 这里你已经用到了：
- BaseContext（当前用户）
- 多个自定义业务异常类
- BeanUtils.copyProperties 简化对象转换

⸻

# 💳 2.2 支付相关：payment + paySuccess

payment(OrdersPaymentDTO)
- 从 BaseContext 拿当前用户 id，从 userMapper 拿 openid
- 调微信支付工具生成预支付订单：
```
JSONObject jsonObject = weChatPayUtil.pay(
    ordersPaymentDTO.getOrderNumber(),
    new BigDecimal(0.01),
    "苍穹外卖订单",
    user.getOpenid()
);
```

- 如果返回 "ORDERPAID"，抛 OrderBusinessException("该订单已支付")
- 把返回 JSON 转成 OrderPaymentVO，并设置 packageStr

```paySuccess(String outTradeNo)```
- 根据订单号 + 当前用户 id 查询订单
- 更新订单状态：
```
Orders orders = Orders.builder()
    .id(ordersDB.getId())
    .status(Orders.TO_BE_CONFIRMED)
    .payStatus(Orders.PAID)
    .checkoutTime(LocalDateTime.now())
    .build();
```

- 通过 WebSocket 推送「来单提醒」

```map.put("type", 1); // 1来单提醒```



⸻

# 📑 2.3 查询与分页：pageQuery4User、details
- pageQuery4User：用户端分页查自己订单
- 用 PageHelper.startPage
- 构造 OrdersPageQueryDTO（带 userId、status）
- 查询 orderMapper.pageQuery
- 把每个订单的 OrderDetail 查出来，组装成 OrderVO
- details(Long id)：查某一个订单 + 明细，封装到 OrderVO

⸻

# ❌ 2.4 取消 / 再来一单 / 条件搜索 等

## userCancelById(Long id)
- 校验订单是否存在：不存在 → OrderBusinessException(ORDER_NOT_FOUND)
- 校验状态是否可取消：status > 2 → 抛 ORDER_STATUS_ERROR
- 如果是 TO_BE_CONFIRMED 且已支付 → 调用 refund
- 更新订单状态为 CANCELLED，记录原因和时间

## repetition(Long id)
- 按订单 id 查 OrderDetail
- 把 OrderDetail 映射为 ShoppingCart，重新加入购物车

## conditionSearch + getOrderVOList + getOrderDishesStr
- 管理端条件搜索订单并分页
- 把订单菜品拼接为 宫保鸡丁*3;鱼香肉丝*1;

⸻

# 🚚 2.5 后台操作：统计、接单、拒单、派送、完成、催单
```
- statistics()：查询各个状态数量
- confirm()：接单 → 状态改成 CONFIRMED
- rejection()：拒单：
- 校验状态为 TO_BE_CONFIRMED
- 如果已支付，调用退款
- 状态改为 CANCELLED，记录拒单原因 + 取消时间
- cancel()：管理端取消
- 若已支付，同样调用退款
- 状态改为 CANCELLED
- delivery()：派送中：状态从 CONFIRMED → DELIVERY_IN_PROGRESS
- complete()：完成：状态从 DELIVERY_IN_PROGRESS → COMPLETED
- reminder()：催单：
- 校验订单存在
- WebSocket 发送 type=2 催单消息
```
⸻

# 📍 2.6 配送范围：checkOutOfRange(String address)

虽然现在被注释掉，但逻辑很完整。
```
- 调百度 Geocoding API 拿 店铺经纬度
- 再拿 用户地址经纬度
- 调「路线规划 API」拿 distance
- distance > 5000 → 抛 OrderBusinessException("超出配送范围")
```
⸻

# 3️⃣ 你用到的所有“常量 / 异常 / 上下文”点

✅ 常量（MessageConstant / Orders 中的状态）

例：
```
throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
```
优点：
- 不写魔法字符串
- 文案统一管理
- 以后改提示只需要改 MessageConstant

⸻

✅ 枚举 / 常量式状态（Orders 的各种状态字段）

你在这里用的是 实体类里的静态常量：
```
orders.setStatus(Orders.PENDING_PAYMENT);
orders.setStatus(Orders.TO_BE_CONFIRMED);
orders.setStatus(Orders.CANCELLED);
...
```
已经避免了 1、2、3 这种魔法数字，是非常好的实践。
再进阶可以用 enum + 数据库映射。

⸻

✅ BaseContext + ThreadLocal

多处使用：
```
Long userId = BaseContext.getCurrentId();
```

用于：
- 下单
- 查询订单
- 支付
- 再来一单

避免每一层方法都传 userId，用 ThreadLocal 携带当前登录用户信息，这个在现代 Web 项目里非常常见。

⸻

✅ 自定义异常体系
- AddressBookBusinessException
- ShoppingCartBusinessException
- OrderBusinessException

这些都是继承 BaseException，用于：
- 精细区分错误类型
- 让全局异常处理器返回统一结构 {code, msg}
- 避免直接抛 RuntimeException / Exception

⸻

4️⃣ 可以优化/思考的点（你以后可以自己改）

这些属于 “如果这是你真实项目，我会在 CR 提的建议”：
## 1.	金额硬编码 new BigDecimal(0.01)
- 可以改为从真实订单金额取
- 或者用常量 / 配置控制测试模式

## 2.	payStatus == 1 这种魔法数字
- 改成 Orders.PAID、Orders.UN_PAID 等常量

## 3.	Map map = new HashMap();
- 可以加泛型：Map<String, Object> map = new HashMap<>();

## 4.	判空方式：

```shoppingCartList == null || shoppingCartList.size() == 0```

可以用：

```CollectionUtils.isEmpty(shoppingCartList)```


## 5.	OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR)
- 催单那里（reminder），目前订单不存在却返回“状态错误”，可以更精细点：
- 不存在 → 用 ORDER_NOT_FOUND
- 状态错误 → ORDER_STATUS_ERROR

⸻

5️⃣ 你可以怎么在面试里讲这段代码？

可以用一句话结构：
```
- 在订单模块中，我通过 OrderServiceImpl 设计了完整的订单生命周期，
包括下单、支付、退款、状态流转、配送范围校验和 WebSocket 实时推送。

- 其中使用了 ThreadLocal 封装的 BaseContext 管理当前登录用户，
使用常量类统一错误提示文案和订单状态，使用自定义业务异常
（例如 OrderBusinessException、ShoppingCartBusinessException）
配合全局异常处理器实现统一的错误响应。
```
⸻

如果你愿意，下一步我可以帮你：
- 把整个 OrderService 流程画成时序图 / 状态机图（Mermaid）
- 帮你写一份 “我在项目中怎么设计订单模块”的面试答案
- 帮你标记所有魔法值，改成常量 / enum
- 写一个 统一的 GlobalExceptionHandler，结合这些异常类一起用
