package com.yifeistudio.com.yifeistudio.zebra

import java.util.concurrent.TimeUnit


interface Coordinator {

    /**
     * 初始化号码池
     */
    fun initializePool(appKey: String, size: Int)


    /**
     * 同步控制
     */
    fun mutex(syncKey: String,
              timeout: Int,
              timeUnit: TimeUnit,
              func: () -> Int): Int

    /**
     * 获取已分配节点集合
     *
     */
    fun getAllocatedWorkers(appKey: String): Set<String>

    /**
     * 回收下线节点的workerId
     *
     */
    fun recycleAllocateIds(appKey: String, offlineWorkers: Set<String>)

    /**
     * 获取唯一ID
     *
     */
    fun acquireWorkerId(key: String): Int

}