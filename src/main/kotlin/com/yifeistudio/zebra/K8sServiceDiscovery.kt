package com.yifeistudio.com.yifeistudio.zebra

import io.fabric8.kubernetes.client.KubernetesClient
import org.slf4j.LoggerFactory

class K8sServiceDiscovery(
    private val client: KubernetesClient
) : ServiceDiscovery {

    private val log = LoggerFactory.getLogger(javaClass)

    private val namespace: String = System.getenv("POD_NAMESPACE") ?: "default"
    private val podName: String = System.getenv("POD_NAME") ?: java.net.InetAddress.getLocalHost().hostName

    override fun getWorkerIdentifier(): String {
        return podName
    }

    override fun getActiveWorkers(serviceName: String): Set<String> {
        return try {
            client.pods()
                .inNamespace(namespace)
                .withLabelSelector(serviceName)
                .list()
                .items
                .mapNotNull { it.status?.podIP }
                .toSet()
        } catch (e: Exception) {
            log.error(e.message, e)
            emptySet()
        }
    }
}
