package com.yifeistudio.com.yifeistudio.zebra

import com.alibaba.nacos.api.NacosFactory
import com.alibaba.nacos.api.naming.NamingService
import com.alibaba.nacos.api.naming.pojo.Instance
import org.slf4j.LoggerFactory
import java.net.InetAddress
import java.util.Properties

class NacosServiceDiscovery(
    serverAddr: String,
    port: Int,
    namespace: String = "public"
) : ServiceDiscovery {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val namingService: NamingService
    private val currentInstanceId: String

    init {
        val properties = Properties()
        properties.setProperty("serverAddr", serverAddr)
        properties.setProperty("namespace", namespace)
        namingService = NacosFactory.createNamingService(properties)
        val ip = InetAddress.getLocalHost().hostAddress
        currentInstanceId = "$ip:$port"
    }

    override fun getWorkerIdentifier(): String {
        return currentInstanceId
    }

    override fun getActiveWorkers(serviceName: String): Set<String> {
        return try {
            val instances: List<Instance> = namingService.getAllInstances(serviceName)
            val activeIds = instances.filter { it.isHealthy && it.isEnabled }
                .map { it.instanceId } // default ip:port
                .toSet()
            logger.debug("[NacosServiceDiscovery] Active instances for '{}': {}", serviceName, activeIds)
            activeIds
        } catch (ex: Exception) {
            logger.warn("[NacosServiceDiscovery] Failed to get instances for $serviceName", ex)
            emptySet()
        }
    }
}

