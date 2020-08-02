package com.hsicen.extensions.extensions

import androidx.lifecycle.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * 作者：hsicen  2020/8/2 15:15
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：LiveData功能扩展
 */


/**
 * 订阅.
 * @receiver LiveData<T>
 * @param owner LifecycleOwner
 * @param observer (T?) -> Unit
 */
fun <T> LiveData<T>.observe(owner: LifecycleOwner, observer: (T?) -> Unit) {
    observe(owner, Observer { observer.invoke(it) })
}

/**
 * 值更新时调用.
 * @receiver LiveData<T>
 * @param func (T?) -> Unit
 * @return MediatorLiveData<T>
 */
fun <T> LiveData<T>.doOnValue(func: (T?) -> Unit) =
    MediatorLiveData<T>().apply {
        addSource(this@doOnValue) {
            value = it
            func.invoke(it)
        }
    }

/**
 * 值变换.
 * @receiver LiveData<T>
 * @param func (T) -> R
 * @return LiveData<R>
 */
fun <T, R> LiveData<T>.map(func: (T) -> R): LiveData<R> = Transformations.map(this, func)

/**
 * LiveData变换.
 * @receiver LiveData<T>
 * @param func (T) -> LiveData<R>
 * @return LiveData<R>
 */
fun <T, R> LiveData<T>.switchMap(func: (T) -> LiveData<R>): LiveData<R> =
    Transformations.switchMap(this, func)

/**
 * 替换值.
 * @receiver LiveData<T>
 * @param newSource MutableLiveData<T>
 * @return LiveData<T>
 */
fun <T> LiveData<T>.replace(newSource: MutableLiveData<T?>): LiveData<T> =
    MediatorLiveData<T>().apply {
        addSource(newSource) {
            value = it
        }
        addSource(this@replace) {
            newSource.value = it
        }
    }

/**
 * 值过滤.
 * @receiver LiveData<T>
 * @param func (T?) -> Boolean 过滤条件.
 * @return LiveData<T>
 */
fun <T> LiveData<T>.filter(func: (T?) -> Boolean): LiveData<T> =
    MediatorLiveData<T>().apply {
        addSource<T>(this@filter) { if (func(it)) value = it }
    }

/**
 * 获取指定个数值.
 * @receiver LiveData<T>
 * @param count Int
 * @return LiveData<T>
 */
fun <T> LiveData<T>.take(count: Int): LiveData<T> {
    if (count <= 0) return object : LiveData<T>() {}
    var counter = 0
    return takeUntil { ++counter >= count }
}

/**
 * 条件获取.
 * @receiver LiveData<T>
 * @param predicate (T?) -> Boolean
 * @return LiveData<T>
 */
fun <T> LiveData<T>.takeUntil(predicate: (T?) -> Boolean): LiveData<T> =
    MediatorLiveData<T>().apply {
        addSource(this@takeUntil) {
            if (predicate.invoke(it)) {
                removeSource(this@takeUntil)
            }
            value = it
        }
    }

/**
 * 合并结果.
 * @receiver LiveData<T1>
 * @param source LiveData<T2>
 * @param combiner (T1, T2) -> R
 * @return LiveData<R>
 */
fun <T1, T2, R> LiveData<T1>.combine(source: LiveData<T2>, combiner: (T1, T2) -> R): LiveData<R> =
    MediatorLiveData<R>().apply {
        var emits = 0
        val sources = listOf(this@combine, source)
        val size = sources.size
        val emptyVal = Any()
        val values = arrayOfNulls<Any?>(size)

        fun reset() {
            emits = 0
            (0 until size).forEach { values[it] = emptyVal }
        }

        reset()
        (0 until size).forEach {
            val observer = Observer<Any> { t ->
                var combine = emits == size
                if (!combine) {
                    if (values[it] == emptyVal) emits++
                    combine = emits == size
                }
                values[it] = t

                if (combine) {
                    value = combiner.invoke(values[0] as T1, values[1] as T2)
                    reset()
                }
            }
            addSource(sources[it] as LiveData<Any>, observer)
        }
    }

suspend fun <T> LiveData<T>.waitForValueAvailable() = suspendCancellableCoroutine<T> {
    val currentValue = value
    if (currentValue != null) {
        it.resume(currentValue)
    } else {
        val observer = object : Observer<T> {
            override fun onChanged(t: T) {
                if (t != null) {
                    removeObserver(this)
                    it.resume(t)
                }
            }
        }

        it.invokeOnCancellation {
            removeObserver(observer)
        }
        observeForever(observer)
    }
}
