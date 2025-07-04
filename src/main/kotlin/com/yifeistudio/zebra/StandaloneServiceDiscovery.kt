package com.yifeistudio.com.yifeistudio.zebra

class StandaloneServiceDiscovery: ServiceDiscovery {

    private val activeWorkers = mutableSetOf<String>()

    /**
     * 获取当前活跃节点
     */
    override fun getActiveWorkers(key: String): Set<String> {
        return activeWorkers
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