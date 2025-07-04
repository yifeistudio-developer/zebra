package com.yifeistudio.com.yifeistudio.zebra

class K8sServiceDiscovery : ServiceDiscovery {
    /**
     * 获取当前活跃节点
     */
    override fun getActiveWorkers(appKey: String): Set<String> {
        TODO("Not yet implemented")
    }

    /**
     * 获取当前节点识别码
     */
    override fun getWorkerIdentifier(): String {
        TODO("Not yet implemented")
    }


}