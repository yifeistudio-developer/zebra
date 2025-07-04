package com.yifeistudio.com.yifeistudio.zebra

import org.slf4j.LoggerFactory
import java.util.UUID

class InMemoryServiceDiscovery: ServiceDiscovery {

    private val log = LoggerFactory.getLogger(InMemoryServiceDiscovery::class.java)

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
        log.debug("Registered new worker: {}", workerIdentifier)
    }

    /**
     * 注销
     */
    fun deregisterWorker(workerIdentifier: String) {
        activeWorkers.remove(workerIdentifier)
        log.debug("Removed offline worker: {}", workerIdentifier)
    }


}