package com.yifeistudio.zebra

import com.yifeistudio.com.yifeistudio.zebra.RedisCoordinator
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.StringRedisTemplate
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import kotlin.time.Duration.Companion.seconds

class RedisCoordinatorTest {

    companion object {
        private lateinit var redisContainer: GenericContainer<*>
        private lateinit var redisTemplate: StringRedisTemplate
        private lateinit var coordinator: RedisCoordinator

        @BeforeAll
        @JvmStatic
        fun setup() {
            redisContainer = GenericContainer(DockerImageName.parse("redis:latest")).apply {
                withExposedPorts(6379)
                start()
            }

            val redisHost = redisContainer.host
            val redisPort = redisContainer.getMappedPort(6379)

            // 配置 StringRedisTemplate 连接 Testcontainers Redis
            redisTemplate = createStringRedisTemplate(redisHost, redisPort)

            // 初始化 RedisCoordinator
            coordinator = RedisCoordinator(redisTemplate)
        }

        @AfterAll
        @JvmStatic
        fun teardown() {
            redisContainer.stop()
        }

        private fun createStringRedisTemplate(host: String, port: Int): StringRedisTemplate {
            val factory = org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory(host, port)
            factory.afterPropertiesSet()
            return StringRedisTemplate(factory).apply {
                afterPropertiesSet()
            }
        }
    }


    @Test
    fun testMutexLocking() {
        val key = "test-lock"
        val result = coordinator.mutex(key, 5.seconds) {
            42 // 返回固定值
        }
        assertEquals(42, result)
    }

    @Test
    fun testGetAllocatedWorkersAndRecycle() {
        val serviceName = "test-service"

        // 模拟分配两个worker
        redisTemplate.opsForHash<String, String>().put("zebra:allocated:$serviceName", "1", "worker-1")
        redisTemplate.opsForHash<String, String>().put("zebra:allocated:$serviceName", "2", "worker-2")

        val allocated = coordinator.getAllocatedWorkers(serviceName)
        assertTrue(allocated.contains("worker-1"))
        assertTrue(allocated.contains("worker-2"))

        // 回收 worker-2
        coordinator.recycleAllocateIds(serviceName, setOf("worker-2"))

        val allocatedAfterRecycle = coordinator.getAllocatedWorkers(serviceName)
        assertTrue(allocatedAfterRecycle.contains("worker-1"))
        assertFalse(allocatedAfterRecycle.contains("worker-2"))
    }

    @Test
    fun testAcquireWorkerId() {
        val serviceName = "test-service"
        val workerIdentifier = "worker-unique-id"
        redisTemplate.delete("zebra:allocated:$serviceName")
        val id = coordinator.acquireWorkerId(serviceName, workerIdentifier)
        assertTrue(id in 0..9) // 号码池10个编号
        val allocated = redisTemplate.opsForHash<String, String>().entries("zebra:allocated:$serviceName")
        assertEquals(workerIdentifier, allocated[id.toString()])
    }
}