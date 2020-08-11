package com.hsicen.core.arch


import androidx.lifecycle.*
import com.hsicen.core.GlobalConfigs
import com.hsicen.core.arch.CorePaginationViewModel.Companion.START_PAGE_NUMBER
import com.hsicen.core.arch.adapter.CoreRvAdapter
import com.hsicen.core.data.Response
import com.hsicen.core.data.Result
import com.hsicen.core.data.onFailure
import com.hsicen.core.data.onSuccess
import com.hsicen.core.exceptions.handleForNetwork
import com.hsicen.extensions.extensions.no
import com.hsicen.extensions.extensions.yes
import kotlinx.coroutines.launch

/**
 * 作者：hsicen  2020/8/10 22:14
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：支持分页ViewModel
 * @param handle  数据保存
 * @param pageSize Int 每页数据条数.
 */
abstract class CorePaginationViewModel<T>(
    private val handle: SavedStateHandle,
    protected val pageSize: Int = GlobalConfigs.DEFAULT_PAGE_SIZE
) : CoreViewModel() {

    /** 当前页数. */
    protected var pageNumber = START_PAGE_NUMBER

    /** 分页数据状态. */
    protected val _paginationDataState by lazy { MutableLiveData<PaginationState<T>>() }
    val paginationDataState: LiveData<PaginationState<T>>
        get() = _paginationDataState

    /**
     * 获取分页数据.
     * @param isLoadMore Boolean 是否是加载更多.
     */
    fun getPaginationData(isLoadMore: Boolean = false) = launch {
        // 防止重复加载
        if (_paginationDataState.value?.state is LoadState.Loading) return@launch

        val localPageNumber = if (isLoadMore) (pageNumber + 1) else START_PAGE_NUMBER
        _paginationDataState.value = PaginationState(localPageNumber, LoadState.Loading)

        // 先从缓存中读取
        paginationActionFromCache(pageSize, localPageNumber)?.onSuccess {
            // 更新页数
            pageNumber = localPageNumber
            _paginationDataState.value = onPaginationDataArrived(pageNumber, it, true)
        }
            ?.onFailure {
                _paginationDataState.value = onPaginationDataError(localPageNumber, it, true)
            }

        paginationAction(pageSize, localPageNumber)
            .onSuccess {
                // 更新页数
                pageNumber = localPageNumber
                _paginationDataState.value = onPaginationDataArrived(pageNumber, it, false)
            }
            .onFailure {
                _paginationDataState.value = onPaginationDataError(localPageNumber, it, false)
            }
    }

    /**
     * 分页数据加载行为，需要自行具体实现.
     * @param pageSize Int
     * @param pageNumber Int
     * @return Result<Response.Pagination<T>>
     */
    abstract suspend fun paginationAction(
        pageSize: Int,
        pageNumber: Int
    ): Result<Response.Pagination<T>>

    /**
     * 分页数据加载行为，来自缓存操作，默认不处理.
     * @param pageSize Int
     * @param pageNumber Int
     * @return Result<Response.Pagination<T>>?
     */
    open suspend fun paginationActionFromCache(
        pageSize: Int,
        pageNumber: Int
    ): Result<Response.Pagination<T>>? = null

    /**
     * 分页数据到达.
     * @param pageNumber Int
     * @param data Response.Pagination<T>
     * @return PaginationState<T>
     */
    @Deprecated("", ReplaceWith("onPaginationDataArrived(pageNumber, data, false)"))
    open fun onPaginationDataArrived(
        pageNumber: Int,
        data: Response.Pagination<T>?
    ): PaginationState<T> = onPaginationDataArrived(pageNumber, data, false)

    open fun onPaginationDataArrived(
        pageNumber: Int,
        data: Response.Pagination<T>?,
        fromCache: Boolean = false
    ): PaginationState<T> =
        PaginationState(
            pageNumber,
            LoadState.Loaded(),
            data?.data ?: arrayListOf(),
            -1,
            if (data?.data.isNullOrEmpty()) {
                false
            } else {
                data?.data?.size == pageSize
            },
            fromCache = fromCache
        )

    /**
     * 分页数据加载失败.
     * @param pageNumber Int
     * @param exception Throwable
     * @return PaginationState<T>
     */
    @Deprecated("", ReplaceWith("onPaginationDataError(pageNumber, exception, false)"))
    open fun onPaginationDataError(pageNumber: Int, exception: Throwable): PaginationState<T> =
        onPaginationDataError(pageNumber, exception, false)

    open fun onPaginationDataError(
        pageNumber: Int,
        exception: Throwable,
        fromCache: Boolean = false
    ): PaginationState<T> =
        PaginationState(pageNumber, LoadState.LoadError(exception), fromCache = fromCache)

    /**
     * 分页状态.
     * @param T
     * @property pageNumber Int 当前页码.
     * @property state LoadState 加载状态.
     * @property data List<T> 数据.
     * @property insertPosition Int 数据插入位置, -1表示末尾 或者 设置新数据.
     * @property hasMore Boolean 是否还有更多.
     * @property extras Any 额外携带的参数.
     * @property fromCache Boolean 结果是否来自缓存.
     * @constructor
     */
    data class PaginationState<T>(
        val pageNumber: Int,
        val state: LoadState,
        val data: List<T> = listOf(),
        val insertPosition: Int = -1,
        val hasMore: Boolean = false,
        val extras: Any = Any(),
        val fromCache: Boolean = false
    )

    companion object {
        /** 开始页码数. */
        const val START_PAGE_NUMBER = 1
    }
}

/**
 * 将结果直接订阅到适配器上.
 * 注意：该方法目前不支持非第一页数据缓存逻辑.
 * @receiver LiveData<CorePaginationViewModel.PaginationState<T>>
 * @param owner LifecycleOwner
 * @param adapter CoreRvAdapter<T>
 * @param result ((CorePaginationViewModel.PaginationState<T>.() -> Unit))?
 */
fun <T : Any> LiveData<CorePaginationViewModel.PaginationState<T>>.applyTo(
    owner: LifecycleOwner,
    adapter: CoreRvAdapter<T>,
    result: ((CorePaginationViewModel.PaginationState<T>.() -> Unit))? = null
) {
    this.observe(owner, Observer {
        when (it.state) {
            is LoadState.Loading -> {
                // 不做任何处理
            }
            is LoadState.Loaded -> {
                // 判断是否是主动指定插入位置
                (it.insertPosition == -1).yes {
                    (it.pageNumber != START_PAGE_NUMBER).yes {
                        adapter.addData(it.data)
                    }.no {
                        adapter.setNewData(it.data)
                    }
                }.no {
                    adapter.addData(it.insertPosition, it.data)
                }
                (it.hasMore).yes {
                    adapter.setEnableLoadMore(true)
                    if (it.pageNumber != START_PAGE_NUMBER) {
                        adapter.loadMoreComplete()
                    }
                }.no {
                    if (it.pageNumber != START_PAGE_NUMBER) {
                        adapter.loadMoreEnd()
                    } else {
                        adapter.setEnableLoadMore(false)
                    }
                }

                adapter.checkEmpty()
            }
            is LoadState.LoadError -> {
                if (it.fromCache) return@Observer
                // 只处理加载更多错误逻辑
                (it.pageNumber != START_PAGE_NUMBER).yes {
                    adapter.loadMoreFail()
                }.no {
                    adapter.checkEmpty()
                }
                it.state.exception?.handleForNetwork()
            }
        }
        result?.invoke(it)
    })
}

/*fun <T : Any> CorePaginationViewModel.PaginationState<T>.applyTo(swipeRefreshLayout: KSwipeRefreshLayout) {
    // 如果是缓存读取状态不取消加载状态
    if (this.state != LoadState.Loading && !fromCache) {
        swipeRefreshLayout.isRefreshing = false
    } else if (this.state == LoadState.Loading && this.pageNumber == START_PAGE_NUMBER) {
        swipeRefreshLayout.isRefreshing = true
    }
}*/
