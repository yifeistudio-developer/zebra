package com.yifeistudio.com.yifeistudio.zebra

import io.fabric8.kubernetes.client.KubernetesClientBuilder
import org.slf4j.LoggerFactory

class K8sServiceDiscovery : ServiceDiscovery {

    private val log = LoggerFactory.getLogger(javaClass)

    private val client = KubernetesClientBuilder().build()
    private val namespace: String = System.getenv("POD_NAMESPACE") ?: "default"
    private val podName: String = System.getenv("POD_NAME") ?: java.net.InetAddress.getLocalHost().hostName

    override fun getWorkerIdentifier(): String {
        return podName
    }

    override fun getActiveWorkers(serviceName: String): Set<String> {
        val endpoints = client.endpoints()
            .inNamespace(namespace)
            .withName(serviceName)
            .get() ?: return emptySet()
        val podNames = endpoints.subsets.orEmpty().flatMap { subset ->
            subset.addresses.orEmpty().mapNotNull { addr ->
                addr.targetRef?.name
            }
        }.toSet()
        log.debug("[K8sServiceDiscovery] Active pods for service '{}': {}", serviceName, podNames)
        return podNames
    }
}
