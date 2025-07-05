package com.yifeistudio.zebra

import com.alibaba.nacos.api.naming.NamingService
import com.alibaba.nacos.api.naming.pojo.Instance
import com.yifeistudio.com.yifeistudio.zebra.NacosServiceDiscovery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NacosServiceDiscoveryTests {

    private lateinit var namingServiceMock: NamingService

    private lateinit var serviceDiscovery: NacosServiceDiscovery

    @BeforeEach
    fun setup() {
        namingServiceMock = mockk()
        serviceDiscovery = object : NacosServiceDiscovery("localhost", 8080, namingServiceMock) {}
    }

    @Test
    fun `getActiveWorkers returns healthy enabled instanceIds`() {
        val instance1 = Instance()
        instance1.instanceId = "192.168.1.10:8080"
        instance1.isHealthy = true
        instance1.isEnabled = true

        val instance2 = Instance()
        instance2.instanceId = "192.168.1.11:8080"
        instance2.isHealthy = false
        instance2.isEnabled = true

        every { namingServiceMock.getAllInstances("my-app") } returns listOf(instance1, instance2)
        val result = serviceDiscovery.getActiveWorkers("my-app")
        assertEquals(setOf("192.168.1.10:8080", "192.168.1.11:8080"), result)
        verify(exactly = 1) { namingServiceMock.getAllInstances("my-app") }
    }

    @Test
    fun `getWorkerIdentifier returns ip and port`() {
        val id = serviceDiscovery.getWorkerIdentifier()
        assertTrue(id.contains(":"))
        println("Worker Identifier: $id")
    }

}