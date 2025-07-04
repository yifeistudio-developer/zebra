package com.yifeistudio.zebra

import com.yifeistudio.com.yifeistudio.zebra.StandaloneCoordinator
import com.yifeistudio.com.yifeistudio.zebra.StandaloneServiceDiscovery
import com.yifeistudio.com.yifeistudio.zebra.WorkerIdAllocator
import kotlin.test.Test

class WorkerIdAllocatorTests {

    @Test
    fun testStandaloneNextId() {
        val coordinator = StandaloneCoordinator()
        val serviceDiscovery = StandaloneServiceDiscovery()
        val workerIdAllocator = WorkerIdAllocator(coordinator = coordinator, serviceDiscovery = serviceDiscovery)
        println(workerIdAllocator.nextId("zebra"))
    }

}