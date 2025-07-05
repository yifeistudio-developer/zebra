package com.yifeistudio.com.yifeistudio.zebra

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * 分布式workerId分配器
 */
class WorkerIdAllocator(private val coordinator: Coordinator,
                        private val serviceDiscovery: ServiceDiscovery) {

    /**
     * 获取唯一WorkerId
     *
     * 默认同步5秒
     */
    fun acquireWorkerId(syncKey: String): Int {
        return acquireWorkerId(syncKey, syncKey)
    }


    /**
     * 获取唯一WorkerId
     *
     * 默认同步5秒
     */
    fun acquireWorkerId(syncKey: String, serviceName: String): Int {
        val workerIdentifier = serviceDiscovery.getWorkerIdentifier()
        return acquireWorkerId(syncKey, serviceName, workerIdentifier)
    }

    /**
     * 获取唯一WorkerId
     *
     * 默认同步5秒
     */
    fun acquireWorkerId(syncKey: String, serviceName: String, workerIdentifier: String): Int {
        return acquireWorkerId(syncKey, serviceName, workerIdentifier, 5.seconds)
    }


    /**
     * 获取唯一WorkerId
     *
     */
    fun acquireWorkerId(syncKey: String, serviceName: String, workerIdentifier: String, timeout: Duration): Int {
        return coordinator.mutex(syncKey, timeout) {
            val activeWorkers = serviceDiscovery.getActiveWorkers(serviceName)
            val allocatedWorkers = coordinator.getAllocatedWorkers(serviceName)
            val offlineWorkers = allocatedWorkers.filter { !activeWorkers.contains(it) }.toSet()
            coordinator.recycleAllocateIds(serviceName, offlineWorkers)
            return@mutex coordinator.acquireWorkerId(serviceName, workerIdentifier)
        }
    }

}