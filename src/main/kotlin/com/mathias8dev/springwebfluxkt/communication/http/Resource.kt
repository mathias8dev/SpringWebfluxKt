package com.mathias8dev.springwebfluxkt.communication.http


sealed class Resource<T> {

    class Idle<T> : Resource<T>()
    class Loading<T> : Resource<T>()
    class Error<T>(
        val message: String? = null,
        val cause: Throwable? = null
    ) : Resource<T>()

    class Success<T>(
        val data: T
    ) : Resource<T>()
}


fun <T> Resource<T>.isLoading(): Boolean = this is Resource.Loading
fun <T> Resource<T>.isSuccess(): Boolean = this is Resource.Success
fun <T> Resource<T>.isError(): Boolean = this is Resource.Error
fun <T> Resource<T>.isIdle(): Boolean = this is Resource.Idle

inline fun <T> Resource<T>.onIdle(callback: () -> Unit) {
    if (this is Resource.Idle<T>) {
        callback()
    }
}

inline fun <T> Resource<T>.onNotIdle(callback: () -> Unit) {
    if (this is Resource.Idle<T>) {
        callback()
    }
}

inline fun <T> Resource<T>.onLoading(callback: () -> Unit) {
    if (this is Resource.Loading<T>) {
        callback()
    }
}

inline fun <T> Resource<T>.onNotLoading(callback: () -> Unit) {
    if (this !is Resource.Loading<T>) {
        callback()
    }
}

inline fun <T> Resource<T>.onError(callback: (message: String?, cause: Throwable?) -> Unit) {
    if (this is Resource.Error<T>) {
        callback(this.message, this.cause)
    }
}

inline fun <T> Resource<T>.onNotError(callback: () -> Unit) {
    if (this !is Resource.Error<T>) {
        callback()
    }
}


inline fun <T> Resource<T>.onSuccess(callback: (data: T) -> Unit) {
    if (this is Resource.Success<T>) {
        callback(data)
    }
}

inline fun <T> Resource<T>.onNotSuccess(callback: () -> Unit) {
    if (this !is Resource.Success<T>) {
        callback()
    }
}
