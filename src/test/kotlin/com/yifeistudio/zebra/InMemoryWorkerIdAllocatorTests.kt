package com.yifeistudio.zebra

import com.yifeistudio.com.yifeistudio.zebra.InMemoryCoordinator
import com.yifeistudio.com.yifeistudio.zebra.InMemoryServiceDiscovery
import com.yifeistudio.com.yifeistudio.zebra.WorkerIdAllocator
import kotlinx.coroutines.*
import kotlin.random.Random
import kotlin.test.Test

class InMemoryWorkerIdAllocatorTests {

    @Test
    fun testInMemory() = runBlocking {
        val jobList = mutableListOf<Job>()
        val coordinator = InMemoryCoordinator()
        val serviceDiscovery = InMemoryServiceDiscovery()
        val workerIdAllocator = WorkerIdAllocator(coordinator = coordinator, serviceDiscovery = serviceDiscovery)
        val serviceName = "zebra"
        repeat(10) { _ ->
            jobList += launch(Dispatchers.Default) {
                repeat(10) {
                    // 随机模拟节点上下线
                    if (Random.nextBoolean()) {
                        val workerIdentifier = serviceDiscovery.getWorkerIdentifier()
                        serviceDiscovery.registerWorker(workerIdentifier)
                        val nextId = workerIdAllocator.acquireWorkerId(serviceName, serviceName, workerIdentifier)
                        println("node registered: $workerIdentifier worker id: $nextId")
                        delay(Random.nextLong(1000, 3000))
                    } else {
                        val activeWorkers = serviceDiscovery.getActiveWorkers(serviceName)
                        if (activeWorkers.isNotEmpty()) {
                            val workerIdentifier = activeWorkers.random()
                            serviceDiscovery.deregisterWorker(workerIdentifier)
                            println("node deregistered: $workerIdentifier")
                        }
                    }
                    // 模拟不稳定节点
                    delay(Random.nextLong(1000, 5000))
                }
            }
        }
        jobList.joinAll()
        println("allocated workers: ${coordinator.getAllocatedWorkers(serviceName)}")
        println("active workers: ${serviceDiscovery.getActiveWorkers(serviceName)}")
    }

}