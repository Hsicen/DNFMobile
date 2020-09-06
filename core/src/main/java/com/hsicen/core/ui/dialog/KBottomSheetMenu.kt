package com.hsicen.core.ui.dialog

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hsicen.core.R
import com.hsicen.core.arch.adapter.CoreRvAdapter
import com.hsicen.extensions.extensions.clickThrottle
import com.hsicen.extensions.extensions.color
import com.hsicen.extensions.extensions.dp2px
import com.hsicen.extensions.extensions.no
import kotlinx.android.synthetic.main.dialog_bottom_sheet_menu.*
import kotlinx.android.synthetic.main.item_bottom_sheet_menu.view.*


/**
 * 作者：hsicen  2020/9/6 16:51
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：底部弹窗
 *
 * 用法
 * <p>
 *      KBottomSheetMenu<String>.with(context)
 *          .config{
 *              menus = listof("1", "2")
 *              onMenuItemClick = { position: Int, item: String ->
 *
 *              }
 *          }
 *          .show()
 * </p>
 */
class KBottomSheetMenu<T : Any> private constructor(
  context: Context,
  private val builder: Builder<T>
) : BottomSheetDialog(context) {

  /**
   * 选择状态
   */
  private val selectStatus by lazy {
    MutableLiveData<Boolean>().apply {
      value = false
    }
  }

  /** 适配器. */
  private val adapter by lazy {
    CoreRvAdapter<T>(builder.itemResId).apply {
      render = { model, helper ->
        if (builder.render != null) {
          builder.render?.invoke(model, helper)
        } else {
          helper.getView<TextView>(R.id.tv_menu_title)?.text = model.toString()
        }
      }

      onItemClick = { _, position, item ->
        builder.onMenuItemClick?.invoke(position, item)
        selectStatus.value = true
        dismiss()
      }
    }
  }

  override fun dismiss() {
    (selectStatus.value).no {
      builder.cancelCallBack?.invoke(this)
    }
    super.dismiss()
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.dialog_bottom_sheet_menu)
    window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    selectStatus.value = false
    setCancelable(builder.isCancelable)

    tv_title.text = builder.title
    tv_title.isVisible = builder.title.isNotEmpty()
    // 取消按钮
    include_action.tv_menu_title.text = context.getString(R.string.action_cancel)
    include_action.clickThrottle {
      dismiss()
    }

    rv_menu.adapter = adapter
    rv_menu.addItemDecoration(object : RecyclerView.ItemDecoration() {

      private val divider1 = context.dp2px(1)
      private val divider2 = context.dp2px(16)
      private val dividerDrawable = ColorDrawable(context.color(R.color.divider_line5))

      override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val itemCount = parent.adapter?.itemCount ?: 0
        (0 until parent.childCount)
          .map { parent.getChildAt(it) }
          .forEach { child ->
            val pos = parent.getChildAdapterPosition(child)
            if (pos < itemCount - 1) {
              dividerDrawable.setBounds(0, child.bottom, parent.right, child.bottom + divider1)
            } else {
              dividerDrawable.setBounds(0, child.bottom, parent.right, child.bottom + divider2)
            }
            dividerDrawable.draw(c)
            if (pos == 0 && builder.title.isNotBlank()) {
              dividerDrawable.setBounds(0, child.top + divider1, parent.right, child.top)
              dividerDrawable.draw(c)
            }
          }
      }

      override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: return
        if (position < itemCount - 1) {
          outRect.set(0, if (position == 0 && builder.title.isNotBlank()) divider1 else 0, 0, divider1)
        } else {
          outRect.set(0, 0, 0, divider2)
        }
      }
    })
    adapter.setNewData(builder.menus)
  }

  /**
   * 构建器.
   * @param T : Any
   * @property ctx Context
   * @property menus List<T> 菜单列表.
   * @property title: CharSequence 标题..
   * @property itemResId Int 可自定义样式.
   * @property onMenuItemClick Function2<[@kotlin.ParameterName] Int, [@kotlin.ParameterName] T, Unit>? 菜单点击.
   * @property render Function2<[@kotlin.ParameterName] T, [@kotlin.ParameterName] DefaultViewHolder, Unit>? 自定义渲染.
   * @property isCancelable Boolean
   * @constructor
   */
  class Builder<T : Any> internal constructor(
    private val ctx: Context,
    var menus: List<T> = listOf(),
    var title: CharSequence = "",
    @LayoutRes val itemResId: Int = R.layout.item_bottom_sheet_menu,
    var onMenuItemClick: ((position: Int, model: T) -> Unit)? = null,
    var render: ((model: T, helper: CoreRvAdapter.DefaultViewHolder) -> Unit)? = null,
    var isCancelable: Boolean = true,
    var cancelCallBack: ((dialog: BottomSheetDialog) -> Unit)? = null
  )

  companion object {
    /**
     * 显示.
     * @param ctx Context
     * @param builder Builder<T>.() -> Unit
     * @return KBottomSheetMenu<T>
     */
    fun <T : Any> show(ctx: Context, builder: Builder<T>.() -> Unit): KBottomSheetMenu<T> =
      KBottomSheetMenu(ctx, Builder<T>(ctx).apply(builder))
        .also {
          it.show()
        }
  }
}