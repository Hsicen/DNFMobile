package com.hsicen.core.arch.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.loadmore.LoadMoreView
import com.hsicen.core.R
import com.hsicen.extensions.extensions.no
import com.hsicen.extensions.extensions.yes
import com.hsicen.extensions.utils.GlobalContext

/**
 * 作者：hsicen  2020/8/2 22:51
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：RecyclerView适配器
 *
 * @property itemLayoutId Int 加载更多布局id.
 * @property loadMoreLayoutId Int 加载更多布局id.
 * @property emptyLayoutId Int 空数据布局id.
 */
class CoreRvAdapter<M : Any>(
    @LayoutRes val itemLayoutId: Int,
    @LayoutRes val loadMoreLayoutId: Int = R.layout.particle_load_more_default,
    @LayoutRes val emptyLayoutId: Int = R.layout.particle_empty_data_default
) : BaseQuickAdapter<M, CoreRvAdapter.DefaultViewHolder>(itemLayoutId) {

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
    var onItemLongClick: ((view: View, position: Int, model: M) -> Unit)? = null
        set(value) {
            field = value
            (field != null).yes {
                super.setOnItemLongClickListener { _, view, position ->
                    field?.invoke(view, position, data[position])
                    return@setOnItemLongClickListener true
                }
            }.no {
                super.setOnItemChildLongClickListener(null)
            }
        }

    /** Item点击. */
    var onItemClick: ((view: View, position: Int, model: M) -> Unit)? = null
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

    /** item长按.  */
    var oItemLongClick: ((view: View, position: Int, model: M) -> Unit)? = null
        set(value) {
            field = value
            (field != null).yes {
                super.setOnItemLongClickListener { _, view, position ->
                    field?.invoke(view, position, data[position])
                    true
                }
            }.no {
                super.setOnItemClickListener(null)
            }
        }

    /** Item子控件点击. */
    var onItemChildClick: ((view: View, position: Int, model: M) -> Unit)? = null
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

    /** 是否开启空界面展示，默认展示. */
    var emptyViewEnable = true

    init {
        // 添加加载更多控件
        super.setLoadMoreView(kLoadMoreView)
    }

    override fun setLoadMoreView(loadingView: LoadMoreView?) {
        //  禁止调用该方法
        throw IllegalAccessException()
    }

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


    override fun convert(holder: DefaultViewHolder, item: M) {
        render?.invoke(item, holder)
    }

    override fun setNewData(data: List<M>?) {
        super.setNewData(data)
        checkEmpty()
    }

    /**
     * 检查当前是否是空数据.
     */
    fun checkEmpty() {
        (emptyViewEnable && data.isEmpty() && emptyView == null).yes {
            super.setEmptyView(
                kEmptyView.createView(
                    GlobalContext.getContext(),
                    recyclerView
                )
            )
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

        /** 空图片view.*/
        val emptyImageView: View?
            get() = view?.findViewById<ImageView>(R.id.iv_empty_image)

        /** 空文字. */
        var emptyText: CharSequence? = null
            set(value) {
                field = value
                view?.findViewById<TextView>(R.id.tv_empty_text)?.text = (value ?: return)
            }

        private var view: View? = null

        /**
         * 创建View.
         * @param context Context
         * @param parent RecyclerView?
         * @return View
         */
        internal fun createView(context: Context, parent: RecyclerView?): View =
            LayoutInflater.from(context).inflate(layoutId, parent, false).also {
                this.view = it
                it.setBackgroundColor(context.resources.getColor(R.color.bg_g8))
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
        var loadEndText: String? = null

        /** 加载失败文字. */
        var errorText: String? = null

        override fun convert(holder: BaseViewHolder?) {
            super.convert(holder)
            holder?.setText(R.id.tv_loading, loadingText)
            loadEndText?.let {
                holder?.setText(R.id.tv_load_more_end, it)
            }
            errorText?.let {
                holder?.setText(R.id.tv_load_more_fail, it)
            }

            (loadMoreStatus == STATUS_LOADING && loadingText.isNotEmpty()).yes {
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
