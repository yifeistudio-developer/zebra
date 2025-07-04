package com.yifeistudio.com.yifeistudio.zebra

import java.util.UUID

class InMemoryServiceDiscovery: ServiceDiscovery {

    private val activeWorkers = mutableSetOf<String>()

    /**
     * 获取当前活跃节点
     */
    override fun getActiveWorkers(appKey: String): Set<String> {
        return activeWorkers
    }

    /**
     * 获取当前节点识别码
     */
    override fun getWorkerIdentifier(): String {
        return UUID.randomUUID().toString()
    }

    /**
     * 注册
     */
    fun registerWorker(workerIdentifier: String) {
        activeWorkers.add(workerIdentifier)
    }

    /**
     * 注销
     */
    fun deregisterWorker(workerIdentifier: String) {
        activeWorkers.remove(workerIdentifier)
    }


}