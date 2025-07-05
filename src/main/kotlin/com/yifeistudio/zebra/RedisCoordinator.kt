package com.yifeistudio.com.yifeistudio.zebra

import org.springframework.data.redis.core.StringRedisTemplate
import kotlin.time.Duration
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.script.DefaultRedisScript
import kotlin.time.toJavaDuration

class RedisCoordinator(
    private val redisTemplate: StringRedisTemplate,
    private val poolSize: Int = 1024) : Coordinator {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun mutex(syncKey: String, timeout: Duration, func: () -> Int): Int {
        val lockKey = "zebra:mutex:$syncKey"
        val lockValue = Thread.currentThread().name + System.currentTimeMillis()
        val acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, timeout.toJavaDuration())
        if (acquired != true) throw IllegalStateException("Could not acquire lock for $syncKey")
        return try {
            func()
        } finally {
            val script = DefaultRedisScript(
                """
                if redis.call("get", KEYS[1]) == ARGV[1] then
                    return redis.call("del", KEYS[1])
                else
                    return 0
                end
                """.trimIndent(),
                Long::class.java
            )
            redisTemplate.execute(script, listOf(lockKey), lockValue)
        }
    }

    override fun getAllocatedWorkers(serviceName: String): Set<String> {
        val key = "zebra:allocated:$serviceName"
        return redisTemplate.opsForHash<String, String>().entries(key).keys.toSet()
    }

    override fun recycleAllocateIds(serviceName: String, offlineWorkers: Set<String>) {
        val key = "zebra:allocated:$serviceName"
        if (offlineWorkers.isNotEmpty()) {
            redisTemplate.opsForHash<String, String>().delete(key, *offlineWorkers.toTypedArray())
            logger.info("[RedisCoordinator] Recycled workers: $offlineWorkers")
        }
    }

    override fun acquireWorkerId(serviceName: String, workerIdentifier: String): Int {
        val key = "zebra:allocated:$serviceName"
        val map = redisTemplate.opsForHash<String, String>().entries(key)
        val existing = map.entries.find { it.value == workerIdentifier }?.key?.toIntOrNull()
        if (existing != null) return existing

        val used = map.keys.mapNotNull { it.toIntOrNull() }.toSet()
        for (i in 0 until poolSize) {
            if (i !in used) {
                redisTemplate.opsForHash<String, String>().put(key, i.toString(), workerIdentifier)
                logger.info("[RedisCoordinator] Assigned workerId=$i to $workerIdentifier")
                return i
            }
        }
        throw IllegalStateException("No available workerId for service $serviceName")
    }
}
