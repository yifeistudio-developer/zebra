package com.yifeistudio.com.yifeistudio.zebra

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import org.slf4j.LoggerFactory
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource


class InMemoryCoordinator : Coordinator {

    private var pool = mutableMapOf<String, IntArray>()

    private val cache = mutableMapOf<String, Int>()

    private val log = LoggerFactory.getLogger(InMemoryCoordinator::class.java)

    /**
     * 同步控制
     */
    override fun mutex(
        syncKey: String,
        timeout: Duration,
        func: () -> Int
    ): Int {
        return runBlocking {
            val mutex = Mutex()
            val start = TimeSource.Monotonic.markNow()
            while (!mutex.tryLock()) {
                if (start.elapsedNow() > timeout) {
                    throw IllegalStateException("try lock after $timeout failed.")
                }
                delay(1.seconds)
                log.debug("try lock failed. trying to acquire locker for $syncKey again")
            }
            return@runBlocking func()
        }
    }

    /**
     * 获取已分配节点集合
     */
    override fun getAllocatedWorkers(serviceName: String): Set<String> {
        return cache.keys
    }

    /**
     * 回收下线节点的workerId
     */
    override fun recycleAllocateIds(serviceName: String, offlineWorkers: Set<String>) {
        offlineWorkers.forEach {
            val id = cache[it]
            if (id != null) {
                val arr = pool[serviceName]
                if (arr != null) {
                    arr[id - 1] = 0
                }
                cache.remove(it)
                log.debug("recycle worker $it for $serviceName $id")
            }
        }
    }

    /**
     * 获取唯一ID
     */
    override fun acquireWorkerId(serviceName: String, workerIdentifier: String): Int {
        if (pool[serviceName] == null) {
            pool[serviceName] = IntArray(1024)
            log.debug("Initialize pool for app $serviceName 1024")
        }
        val arr = pool[serviceName]
        val size = arr?.size
        for (i in 0 until size!!) {
            if (arr[i] == 0) {
                arr[i] = 1
                val id = i + 1
                cache[workerIdentifier] = id
                log.debug("acquire worker $id for $serviceName $workerIdentifier: $id")
                return id
            }
        }
        throw IllegalStateException("Could not acquire worker $workerIdentifier")
    }

}