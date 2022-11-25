package com.wy8162.job

import com.wy8162.config.getLogger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object JobManager {
    private val scope = CoroutineScope(SupervisorJob())

    suspend fun startJob(name: String = "default", block: suspend CoroutineScope.() -> Unit): Job {
        return scope.launch() {
            kotlin.runCatching {
                block()
            }
        }
    }

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, exception ->
        getLogger().error("Uncaught exception running job", exception)
    }
}
