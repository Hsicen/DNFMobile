@file:Suppress("UNCHECKED_CAST", "RedundantVisibilityModifier")

package com.hsicen.core.data

import com.hsicen.core.exceptions.KException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * 作者：hsicen  2020/8/10 22:40
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：加载结果包装类
 */
@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
sealed class Result<out T : Any>(internal val value: T?) {

    data class Success<out T : Any>(val data: T) : Result<T>(data)
    data class Error(val exception: Throwable) : Result<Nothing>(null)

    override fun toString(): String =
        when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }

    fun getOrNull(): T? = value
}

/**
 * 是成功的结果回调.
 * @receiver Result<T>
 * @param action (value: T) -> Unit
 * @return Result<T>
 */

@OptIn(ExperimentalContracts::class)
inline fun <T : Any> Result<T>.onSuccess(action: (value: T) -> Unit): Result<T> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }
    return try {
        if (this is Result.Success) action(this.data)
        this
    } catch (e: Exception) {
        e.printStackTrace()
        Result.Error(
            KException(
                "onSuccess",
                if (e is ClassCastException) KException.Code.TYPE_CONVERSION_EXCEPTION else -1,
                cause = e
            )
        )
    }
}

/**
 * 是失败的结果回调.
 * @receiver Result<T>
 * @param action (exception: Throwable) -> Unit
 * @return Result<T>
 */
@OptIn(ExperimentalContracts::class)
inline fun <T : Any> Result<T>.onFailure(action: (exception: Throwable) -> Unit): Result<T> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }
    if (this is Result.Error) action(this.exception)
    return this
}

/**
 * 封装api请求，并进行异常捕捉处理.
 * @param call suspend () -> Response<T>
 * @return Result<T>
 */
suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>): Result<T> =
    try {
        Result.Success(call.invoke().unwrap())
    } catch (e: Throwable) {
        Result.Error(e)
    }

/**
 * 捕获代码块异常，返回Result结构.
 * @param block suspend () -> T
 * @return Result<T>
 */
suspend fun <T : Any> safeCall(block: suspend () -> T): Result<T> =
    try {
        Result.Success(block.invoke())
    } catch (e: Throwable) {
        Result.Error(e)
    }

/**
 *  封装api请求，并进行异常捕捉处理.
 *  并且返回未解包的数据，包含了错误码；用于需要错误码的地方.
 *  @param call
 *  @return Result<Response<T>>
 */
suspend fun <T : Any> safeApiCallWithCode(call: suspend () -> Response<T>): Result<Response<T>> =
    try {
        Result.Success(call.invoke())
    } catch (e: Throwable) {
        Result.Error(e)
    }
