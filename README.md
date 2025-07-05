Zebra （分布式 Worker-Id 分配器）

---

Zebra 是一个轻量级、高性能的分布式 Worker-Id 分配服务，专为多实例部署环境下的唯一 ID 生成服务（如 Snowflake 算法）设计，确保在多节点场景下分配的 WorkerId 唯一、可回收、无需续约心跳。

---

设计

组件
•	协调器（Coordinator）：管理 WorkerId 分配池、互斥锁机制、离线回收。
•	服务发现（ServiceDiscovery）：提供当前活跃节点集合（如基于 Kubernetes、Redis、etcd 等）。

算法流程
1.	对 serviceName + syncKey 进行互斥加锁（带超时）。
2.	查询当前活跃节点集合 activeWorkers。
3.	获取已分配的 allocatedWorkers。
4.	识别并回收不活跃的 WorkerId：allocated - activeWorkers。
5.	从分配池中挑选未分配 WorkerId，返回。

---

使用说明

快速启动（Spring Boot）

val workerId = zebraWorkerIdAllocator.acquire("my-app", Duration.ofSeconds(1))

注册协调器与服务发现实现

@Configuration
class ZebraConfig {
@Bean
fun workerIdCoordinator(): WorkerIdCoordinator = RedisCoordinator()

    @Bean
    fun serviceDiscovery(): ServiceDiscovery = KubernetesServiceDiscovery()
}


---

特性

- ✅自动回收离线节点分配的 WorkerId
- ✅ 支持锁等待超时退出，无需心跳续约
-	✅ 插拔式协调器和服务发现接口
-	✅ 可嵌入 SDK 或部署为独立服务

---

测试

支持并发模拟节点上下线的单元测试，确保在抖动环境下 WorkerId 分配的稳定性与唯一性。

---

依赖
-	Kotlin
-	kotlinx.coroutines
-	SLF4J + Logback（可换）

---

后续规划
-	Redis 协调器实现
-	Spring Boot Starter 自动装配
-	支持 WorkerId 自动释放接口
-	Prometheus 监控指标集成

---

License

MIT License © 2025 yifeistudio.com
