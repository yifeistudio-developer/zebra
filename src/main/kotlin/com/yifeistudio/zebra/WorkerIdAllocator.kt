package com.yifeistudio.com.yifeistudio.zebra

import java.util.concurrent.TimeUnit

class WorkerIdAllocator(val coordinator: Coordinator,
                        val serviceDiscovery: ServiceDiscovery) {

    /**
     * 获取唯一WorkerId
     *
     * 默认同步10秒
     */
    fun nextId(syncKey: String): Int {
        return nextId(syncKey, appKey = syncKey, 10, TimeUnit.SECONDS)
    }

    /**
     * 获取唯一WorkerId
     *
     * 默认同步10秒
     */
    fun nextId(syncKey: String, appKey: String): Int {
        return nextId(syncKey, appKey, 10, TimeUnit.SECONDS)
    }

    /**
     * 获取唯一WorkerId
     *
     */
    fun nextId(syncKey: String, appKey: String, timeout: Int, timeUnit: TimeUnit): Int {
        return coordinator.mutex(syncKey, timeout, timeUnit) {
            val activeWorkers = serviceDiscovery.getActiveWorkers(appKey)
            coordinator.initializePool(appKey, 10)
            val allocatedWorkers = coordinator.getAllocatedWorkers(appKey)
            val offlineWorkers = allocatedWorkers.filter { !activeWorkers.contains(it) }.toSet()
            coordinator.recycleAllocateIds(appKey, offlineWorkers)
            return@mutex coordinator.acquireWorkerId(syncKey)
        }
    }

}