package com.yifeistudio.com.yifeistudio.zebra

import kotlin.time.Duration

/**
 * 分布式协调器
 */
interface Coordinator {

    /**
     * 同步控制
     */
    fun mutex(syncKey: String,
              timeout: Duration,
              func: () -> Int): Int

    /**
     * 获取已分配节点集合
     *
     */
    fun getAllocatedWorkers(serviceName: String): Set<String>

    /**
     * 回收下线节点的workerId
     *
     */
    fun recycleAllocateIds(serviceName: String, offlineWorkers: Set<String>)

    /**
     * 获取唯一ID
     *
     */
    fun acquireWorkerId(serviceName: String, workerIdentifier: String): Int

}