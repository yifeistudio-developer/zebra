package com.yifeistudio.com.yifeistudio.zebra

import kotlin.time.Duration

class RedisCoordinator : Coordinator {

    /**
     * 初始化号码池
     */
    override fun initializePool(appKey: String, size: Int) {
        TODO("Not yet implemented")
    }

    /**
     * 同步控制
     */
    override fun mutex(
        syncKey: String,
        timeout: Duration,
        func: () -> Int
    ): Int {
        TODO("Not yet implemented")
    }

    /**
     * 获取已分配节点集合
     *
     */
    override fun getAllocatedWorkers(appKey: String): Set<String> {
        TODO("Not yet implemented")
    }

    /**
     * 回收下线节点的workerId
     *
     */
    override fun recycleAllocateIds(appKey: String, offlineWorkers: Set<String>) {
        TODO("Not yet implemented")
    }

    /**
     * 获取唯一ID
     */
    override fun acquireWorkerId(appKey: String, workerIdentifier: String): Int {
        TODO("Not yet implemented")
    }


}