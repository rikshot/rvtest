package com.bumptech.glide.load.engine.executor

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch

class CoroutineExecutorService(private val scope: CoroutineScope) : ExecutorService {
    override fun shutdown() {}

    override fun shutdownNow(): List<Runnable?>? = null

    override fun isShutdown(): Boolean = false

    override fun isTerminated(): Boolean = false

    override fun awaitTermination(timeout: Long, unit: TimeUnit?): Boolean = false

    @RequiresApi(Build.VERSION_CODES.N)
    override fun <T : Any?> submit(task: Callable<T?>?): Future<T?>? = scope.launch {
        task?.call()
    }.asCompletableFuture() as Future<T?>?

    @RequiresApi(Build.VERSION_CODES.N)
    override fun <T : Any?> submit(task: Runnable?, result: T?): Future<T?>? = scope.launch {
        task?.run()
        result
    }.asCompletableFuture() as Future<T?>?

    override fun submit(task: Runnable?): Future<*>? = scope.launch { task?.run() }.asCompletableFuture()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun <T : Any?> invokeAll(tasks: Collection<Callable<T?>?>?): List<Future<T?>?>? {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun <T : Any?> invokeAll(
        tasks: Collection<Callable<T?>?>?,
        timeout: Long,
        unit: TimeUnit?
    ): List<Future<T?>?>? {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> invokeAny(tasks: Collection<Callable<T?>?>?): T? {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> invokeAny(tasks: Collection<Callable<T?>?>?, timeout: Long, unit: TimeUnit?): T? {
        TODO("Not yet implemented")
    }

    override fun execute(command: Runnable?) {
        scope.launch { command?.run() }
    }
}

@SuppressLint("VisibleForTests")
fun createGlideCoroutineExecutor(scope: CoroutineScope) = GlideExecutor(CoroutineExecutorService(scope))
