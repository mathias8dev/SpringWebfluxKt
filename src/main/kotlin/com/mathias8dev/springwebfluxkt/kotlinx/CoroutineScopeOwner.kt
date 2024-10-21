package com.mathias8dev.springwebfluxkt.kotlinx


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.io.Closeable
import kotlin.coroutines.CoroutineContext


interface CoroutineScopeProvider : Closeable {
    val coroutineScope: CoroutineScope
}

open class CoroutineScopeOwner(
    dispatcher: CoroutineContext = Dispatchers.IO
) : CoroutineScopeProvider {
    private val updatedContext = dispatcher + SupervisorJob()
    private var scope: CloseableCoroutineScope? =
        CloseableCoroutineScope(updatedContext)

    override val coroutineScope: CoroutineScope
        get() {
            val newScope = CloseableCoroutineScope(updatedContext)
            if (scope == null) {
                scope = newScope
            }
            return newScope
        }

    override fun close() {
        scope?.cancel()
        scope = null
    }
}

