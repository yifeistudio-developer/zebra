package com.yifeistudio.com.yifeistudio.zebra

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


class WorkerIdAllocator(val coordinator: Coordinator,
                        val serviceDiscovery: ServiceDiscovery) {

    /**
     * 获取唯一WorkerId
     *
     * 默认同步10秒
     */
    fun nextId(syncKey: String): Int {
        return nextId(syncKey, syncKey)
    }


    /**
     * 获取唯一WorkerId
     *
     * 默认同步10秒
     */
    fun nextId(syncKey: String, appKey: String): Int {
        val workerIdentifier = serviceDiscovery.getWorkerIdentifier()
        return nextId(syncKey, appKey, workerIdentifier)
    }

    fun nextId(syncKey: String, appKey: String, workerIdentifier: String): Int {
        return nextId(syncKey, appKey, workerIdentifier, 5.seconds)
    }


    /**
     * 获取唯一WorkerId
     *
     */
    fun nextId(syncKey: String, appKey: String, workerIdentifier: String, timeout: Duration): Int {
        return coordinator.mutex(syncKey, timeout) {
            val activeWorkers = serviceDiscovery.getActiveWorkers(appKey)
            coordinator.initializePool(appKey, 1024)
            val allocatedWorkers = coordinator.getAllocatedWorkers(appKey)
            val offlineWorkers = allocatedWorkers.filter { !activeWorkers.contains(it) }.toSet()
            coordinator.recycleAllocateIds(appKey, offlineWorkers)
            return@mutex coordinator.acquireWorkerId(appKey, workerIdentifier)
        }
    }

}