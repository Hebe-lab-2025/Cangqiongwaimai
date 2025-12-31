很好，这里你问的 prefix 是关键点 👍
我用最简单、对照例子的方式讲清楚它👇

⸻

✅ prefix 是什么？

在这里：

```@ConfigurationProperties(prefix = "sky.jwt")```

👉 prefix = 配置文件中的前缀

就是告诉 Spring：

- 到 application.yml 里
- 找以 sky.jwt 开头的配置
- 然后自动绑定到这个类的属性上

⸻

🧩 配置 → Java 字段 绑定示例
```
application.yml

sky:
  jwt:
    admin-secret-key: abc123
    admin-ttl: 7200000
```
Java 类
```
private String adminSecretKey;
private long adminTtl;
```
自动绑定关系
```
yml	java
sky.jwt.admin-secret-key	adminSecretKey
sky.jwt.admin-ttl	adminTtl
```
👉 横线命名 → 自动转成驼峰命名

⸻

🔔 记住一句话

prefix 决定了 Spring 去 yml 里的“哪一块配置”绑定到类上

⸻

🧠 如果 prefix 改成这样：

```@ConfigurationProperties(prefix = "abc.def")```

那就必须写：
```
abc:
  def:
    xxx: yyy
```
否则绑定不到，值为 null

⸻

🎯 面试一句话回答

- prefix specifies the root path of configuration properties in application.yml 
that should be bound to the fields of the class annotated 
with @ConfigurationProperties.

⸻

如果你愿意，我还能继续帮你：
- 🌈 画一张 prefix 绑定流程图
- 🧪 出 5 道 Spring 配置绑定面试题
- 🛠 带你手写 properties + yml 练习
- 🚨 讲清楚 @Value vs @ConfigurationProperties 区别
