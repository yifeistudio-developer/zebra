package com.yifeistudio.zebra
import com.yifeistudio.com.yifeistudio.zebra.K8sServiceDiscovery
import io.fabric8.kubernetes.api.model.Pod
import io.fabric8.kubernetes.api.model.PodList
import io.fabric8.kubernetes.api.model.PodStatus
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.dsl.FilterWatchListDeletable
import io.fabric8.kubernetes.client.dsl.MixedOperation
import io.fabric8.kubernetes.client.dsl.PodResource
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import io.fabric8.kubernetes.client.dsl.*


class K8sServiceDiscoveryTests {

    @Test
    fun `getActiveWorkers should return running pod IPs`() {
        // 1. mock Pod 和 PodStatus
        val pod1 = mockk<Pod>()
        val pod2 = mockk<Pod>()
        val status1 = mockk<PodStatus>()
        val status2 = mockk<PodStatus>()

        every { pod1.status } returns status1
        every { status1.phase } returns "Running"
        every { status1.podIP } returns "10.0.0.1"

        every { pod2.status } returns status2
        every { status2.phase } returns "Pending"
        every { status2.podIP } returns "10.0.0.2"

        val podList = PodList()
        podList.items = listOf(pod1, pod2)

        // 2. mock K8s 客户端调用链
        val client = mockk<KubernetesClient>()
        val podOps = mockk<MixedOperation<Pod, PodList, PodResource>>()
        val nsOps = mockk<NonNamespaceOperation<Pod, PodList, PodResource>>()
        val filteredOps = mockk<FilterWatchListDeletable<Pod, PodList, PodResource>>()

        every { client.pods() } returns podOps
        every { podOps.inNamespace("default") } returns nsOps
        every { nsOps.withLabelSelector("app=zebra") } returns filteredOps
        every { filteredOps.list() } returns podList

        // 3. 调用测试目标
        val discovery = K8sServiceDiscovery(client)
        val result = discovery.getActiveWorkers("zebra")

        // 4. 断言只返回 Running 且有 IP 的 Pod
        assertEquals(setOf("10.0.0.1", "10.0.0.2"), result)
    }

    @Test
    fun `getWorkerIdentifier should return hostname`() {
        val client = mockk<KubernetesClient>(relaxed = true)
        val service = K8sServiceDiscovery(client)
        val id = service.getWorkerIdentifier()
        assertTrue(id.isNotBlank())
    }
}