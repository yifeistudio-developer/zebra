package com.yifeistudio.com.yifeistudio.zebra


interface ServiceDiscovery {

    /**
     * 获取当前活跃节点
     */
    fun getActiveWorkers(appKey: String): Set<String>

    /**
     * 获取当前节点识别码
     */
    fun getWorkerIdentifier(): String

}