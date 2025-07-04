package com.yifeistudio.zebra

import com.yifeistudio.com.yifeistudio.zebra.InMemoryCoordinator
import com.yifeistudio.com.yifeistudio.zebra.InMemoryServiceDiscovery
import com.yifeistudio.com.yifeistudio.zebra.WorkerIdAllocator
import kotlinx.coroutines.*
import kotlin.random.Random
import kotlin.test.Test

class WorkerIdAllocatorTests {

    @Test
    fun testStandaloneNextId() {
        val coordinator = InMemoryCoordinator()
        val serviceDiscovery = InMemoryServiceDiscovery()
        val workerIdAllocator = WorkerIdAllocator(coordinator = coordinator, serviceDiscovery = serviceDiscovery)
        // mock service register and deregister
        while (true) {
            val worker = "zebra-${Random.nextInt()})"
            serviceDiscovery.registerWorker(worker)

        }
    }


    @Test
    fun test() = runBlocking {
        val jobList = mutableListOf<Job>()
        val coordinator = InMemoryCoordinator()
        val serviceDiscovery = InMemoryServiceDiscovery()
        val workerIdAllocator = WorkerIdAllocator(coordinator = coordinator, serviceDiscovery = serviceDiscovery)
        val appKey = "zebra"
        repeat(1) { _ ->
            jobList += launch(Dispatchers.Default) {
                repeat(10) {
                    // 随机模拟节点上下线
                    if (Random.nextBoolean()) {
                        val workerIdentifier = serviceDiscovery.getWorkerIdentifier()
                        serviceDiscovery.registerWorker(workerIdentifier)
                        val nextId = workerIdAllocator.nextId(appKey, appKey, workerIdentifier)
                        println("node registered: $workerIdentifier worker id: $nextId")
                        delay(Random.nextLong(1000, 3000))
                    } else {
                        val activeWorkers = serviceDiscovery.getActiveWorkers(appKey)
                        if (activeWorkers.isNotEmpty()) {
                            val workerIdentifier = activeWorkers.random()
                            serviceDiscovery.deregisterWorker(workerIdentifier)
                            println("node deregistered: $workerIdentifier")
                        }
                    }
                    delay(Random.nextLong(1000, 5000)) // 模拟不稳定节点
                }
            }
        }
        jobList.joinAll()
        println("allocated workerIds: ${coordinator.getAllocatedWorkers(appKey)}")
        println("active workers: ${serviceDiscovery.getActiveWorkers(appKey)}")
    }

}