package com.yifeistudio.com.yifeistudio.zebra

/**
 * 服务发现组件
 */
interface ServiceDiscovery {

    /**
     * 获取当前活跃节点
     */
    fun getActiveWorkers(serviceName: String): Set<String>

    /**
     * 获取当前节点识别码
     */
    fun getWorkerIdentifier(): String

}
