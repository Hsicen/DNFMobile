package com.hsicen.core.arch.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.loadmore.LoadMoreView
import com.hsicen.core.R
import com.hsicen.extensions.extensions.no
import com.hsicen.extensions.extensions.yes


/**
 * 作者：hsicen  2020/8/2 23:30
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：多布局，刷新加载功能适配器
 */
abstract class RcvMultiItemAdapter<M : MultiItemEntity>(
    data: List<M>,
    @LayoutRes
    val loadMoreLayoutId: Int = R.layout.particle_load_more_default,
    @LayoutRes
    val emptyLayoutId: Int = R.layout.particle_empty_data_default
) : BaseMultiItemQuickAdapter<M, RcvMultiItemAdapter.DefaultViewHolder>(data) {
    /** 加载更多时回调. */
    var onLoadMore: (() -> Unit)? = null
        set(value) {
            field = value
            // 设置加载更多监听
            (field != null).yes {
                super.setOnLoadMoreListener({
                    field?.invoke()
                }, null)
            }.no {
                super.setOnLoadMoreListener(null, null)
            }
        }

    /** Item点击. */
    var onItemClick: ((view: View, position: Int, item: M) -> Unit)? = null
        set(value) {
            field = value
            (field != null).yes {
                super.setOnItemClickListener { _, view, position ->
                    field?.invoke(view, position, data[position])
                }
            }.no {
                super.setOnItemClickListener(null)
            }
        }

    /** Item子控件点击. */
    var onItemChildClick: ((view: View, position: Int, item: M) -> Unit)? = null
        set(value) {
            field = value
            (field != null).yes {
                super.setOnItemChildClickListener { _, view, position ->
                    field?.invoke(view, position, data[position])
                }
            }.no {
                super.setOnItemChildClickListener(null)
            }
        }

    /** item渲染. */
    var render: ((model: M, helper: DefaultViewHolder) -> Unit)? = null

    /** 加载更多View. */
    val kLoadMoreView = CoreAdapterLoadMoreView(loadMoreLayoutId)

    /** 空View. */
    val kEmptyView = CoreAdapterEmptyDataView(emptyLayoutId)

    override fun setOnLoadMoreListener(
        requestLoadMoreListener: RequestLoadMoreListener?,
        recyclerView: RecyclerView?
    ) {
        //  禁止调用该方法
        //  super.setOnLoadMoreListener(requestLoadMoreListener, recyclerView)
        throw IllegalAccessException()
    }

    override fun setEmptyView(emptyView: View?) {
        throw IllegalAccessException()
    }

    override fun setEmptyView(layoutResId: Int, viewGroup: ViewGroup?) {
        throw IllegalAccessException()
    }

    override fun setOnItemClickListener(listener: OnItemClickListener?) {
        throw IllegalAccessException()
    }

    override fun setOnItemChildClickListener(listener: OnItemChildClickListener?) {
        throw IllegalAccessException()
    }

    override fun convert(helper: DefaultViewHolder?, item: M) {
        render?.invoke(item, helper ?: return)
    }

    /**
     * 检查当前是否是空数据.
     */
    fun checkEmpty() {
        loadMoreComplete()
        (data.isEmpty()).yes {
            super.setEmptyView(kEmptyView.createView(recyclerView))
        }
    }

    /**
     * 检查当前是否是空数据.
     */
    fun checkEmpty(data: List<M>) {
        loadMoreComplete()
        (data.isEmpty()).yes {
            if (footerLayoutCount == 0) {
                addFooterView(kEmptyView.createView(recyclerView))
            } else {
                kEmptyView.emptyImage = kEmptyView.emptyImage
                kEmptyView.emptyText = kEmptyView.emptyText
            }
        }.no {
            removeAllFooterView()
        }
    }

    /**
     * 默认ViewHolder.
     * @constructor
     */
    class DefaultViewHolder(view: View) : BaseViewHolder(view)

    class CoreAdapterEmptyDataView(
        @LayoutRes private val layoutId: Int
    ) {

        /** 空图片.*/
        @DrawableRes
        var emptyImage: Int? = null
            set(value) {
                field = value
                view?.findViewById<ImageView>(R.id.iv_empty_image)
                    ?.setImageResource(value ?: return)
            }

        /** 空文字. */
        var emptyText: CharSequence? = null
            set(value) {
                field = value
                view?.findViewById<TextView>(R.id.tv_empty_text)?.text = (value ?: return)
            }

        private var view: View? = null

        /**
         * 创建View.
         * @param parent RecyclerView
         * @return View
         */
        internal fun createView(parent: RecyclerView): View =
            LayoutInflater.from(parent.context).inflate(layoutId, parent, false).also {
                this.view = it
                // 进行设置
                this.emptyImage = emptyImage
                this.emptyText = emptyText
            }
    }

    class CoreAdapterLoadMoreView(
        @LayoutRes private val layoutId: Int
    ) : LoadMoreView() {

        /** 加载中文字. */
        var loadingText: String = ""

        /** 加载结束文字. */
        var loadEndText: String = ""

        /** 加载失败文字. */
        var errorText: String = ""

        override fun convert(holder: BaseViewHolder?) {
            super.convert(holder)
            holder?.setText(R.id.tv_loading, loadingText)
            holder?.setText(R.id.tv_load_more_end, errorText)
            holder?.setText(R.id.tv_load_more_fail, loadEndText)
            (loadMoreStatus == STATUS_LOADING).yes {
                holder?.setGone(R.id.tv_loading, true)
            }.no {
                holder?.setGone(R.id.tv_loading, false)
            }
        }

        override fun getLayoutId(): Int = layoutId

        override fun getLoadingViewId(): Int = R.id.pb_load_more_loading

        override fun getLoadEndViewId(): Int = R.id.tv_load_more_end

        override fun getLoadFailViewId(): Int = R.id.tv_load_more_fail
    }
}
