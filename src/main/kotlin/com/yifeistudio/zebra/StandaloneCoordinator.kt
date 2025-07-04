package com.yifeistudio.com.yifeistudio.zebra

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.TimeUnit


class StandaloneCoordinator : Coordinator {

    private var pool = mutableMapOf<String, IntArray>()

    private val cache = mutableMapOf<String, Int>()

    /**
     * 初始化号码池
     */
    override fun initializePool(appKey: String, size: Int) {
        val arr = pool[appKey]
        if (arr == null) {
            pool[appKey] = intArrayOf(size)
        }
    }


    /**
     * 同步控制
     */
    override fun mutex(
        syncKey: String,
        timeout: Int,
        timeUnit: TimeUnit,
        func: () -> Int
    ): Int {

        return func()
    }

    /**
     * 获取已分配节点集合
     */
    override fun getAllocatedWorkers(appKey: String): Set<String> {
        return HashSet()
    }

    /**
     * 回收下线节点的workerId
     */
    override fun recycleAllocateIds(appKey: String, offlineWorkers: Set<String>) {
        offlineWorkers.forEach {
            val id = cache[it]
            if (id != null) {
                val arr = pool[appKey]
                arr?.set(id - 1, 0)
            }
        }
    }

    /**
     * 获取唯一ID
     */
    override fun acquireWorkerId(key: String): Int {
        val arr = pool[key]
        val size = arr?.size
        for (i in 0 until size!!) {
            if (arr[i] == 0) {
                arr[i] = 1
                val id = i + 1
                cache[key] = id
                return id
            }
        }
        return -1
    }

}