package com.yifeistudio.com.yifeistudio.zebra


interface ServiceDiscovery {

    /**
     * 获取当前活跃节点
     */
    fun getActiveWorkers(key: String): Set<String>

}