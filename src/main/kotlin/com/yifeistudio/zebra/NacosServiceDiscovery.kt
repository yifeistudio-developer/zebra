package com.yifeistudio.com.yifeistudio.zebra

import com.alibaba.nacos.api.naming.NamingService
import com.alibaba.nacos.api.naming.pojo.Instance
import org.slf4j.LoggerFactory

open class NacosServiceDiscovery(
    val ip: String = "localhost",
    val port: Int,
    val namingService: NamingService
) : ServiceDiscovery {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun getWorkerIdentifier(): String {
        return "$ip:$port"
    }

    override fun getActiveWorkers(serviceName: String): Set<String> {
        return try {
            val instances: List<Instance> = namingService.getAllInstances(serviceName)
            val activeIds = instances.map { it.instanceId }.toSet()
            logger.debug("[NacosServiceDiscovery] Active instances for '{}': {}", serviceName, activeIds)
            activeIds
        } catch (ex: Exception) {
            logger.warn("[NacosServiceDiscovery] Failed to get instances for $serviceName", ex)
            emptySet()
        }
    }
}

