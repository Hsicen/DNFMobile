package com.hsicen.core.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.hsicen.core.R
import com.hsicen.extensions.extensions.addTextChangedListener
import com.hsicen.extensions.extensions.clickThrottle
import com.hsicen.extensions.extensions.showKeyboardWithDelay
import kotlinx.android.synthetic.main.k_search_bar.view.*

/**
 * 作者：hsicen  2020/9/6 16:37
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：自定义搜索条
 */
class KSearchBar @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

  /** 搜索行为. */
  var onSearchAction: ((CharSequence) -> Unit)? = null

  /** 输入内容改变. */
  var onTextChange: ((CharSequence) -> Unit)? = null

  /** 搜索内容. */
  var searchText: CharSequence
    get() = et_search_content.text.toString()
    set(value) {
      et_search_content.setText(value)
    }

  /** 输入框hint. */
  var searchHint: CharSequence
    get() = tv_search_hint.text
    set(value) {
      tv_search_hint.text = value
    }

  var searchHints: CharSequence
    get() = et_search_content.hint.toString()
    set(value) {
      et_search_content.hint = value
    }
  val searchBtn: Button
    get() = btn_search

  init {
    View.inflate(context, R.layout.k_search_bar, this)

    // 监听输入框内容
    et_search_content.addTextChangedListener {
      // 输入内容不为空时 显示清楚按钮
      ib_clear.isVisible = it.isNotBlank()
      btn_search.isEnabled = it.isNotBlank()

      onTextChange?.invoke(it)
    }
    et_search_content.setOnFocusChangeListener { _, hasFocus ->
      tv_search_hint.isInvisible = hasFocus
    }

    // 清楚输入内容
    ib_clear.clickThrottle {
      searchText = ""
    }

    // 搜索
    btn_search.clickThrottle {
      onSearchAction?.invoke(searchText)
    }

    // 自动弹出软键盘
    et_search_content.showKeyboardWithDelay(500)
  }

  /***获取搜索框背景view*/
  fun getSearchBgView(): View {
    return view_bg
  }
}
