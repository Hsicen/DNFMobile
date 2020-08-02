package com.hsicen.extensions.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView

/**
 * 作者：hsicen  2020/8/2 15:09
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：EditText扩展
 */

/**
 *  内容变化监听, 只处理onTextChanged()回调
 *  @param textChanged 监听回调方法
 */
fun EditText.addTextChangedListener(textChanged: (String) -> Unit): TextWatcher {
    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            textChanged(s.toString())
        }
    }
    addTextChangedListener(watcher)
    return watcher
}

fun TextView.addTextChangedListener(textChanged: (String) -> Unit): TextWatcher {
    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            textChanged(s.toString())
        }
    }
    addTextChangedListener(watcher)
    return watcher
}

/**
 * 设置光标在最后的位置
 */
fun EditText.setLastSelection() {
    if (text.isNullOrEmpty()) {
        return
    }
    setSelection(text.length)
}

/**
 * 设置内容，并且调整光标
 */
fun EditText.setTextWithLastSelection(text: String) {
    setText(text)
    setLastSelection()
}

/**
 *  设置失去焦点，不可点击
 */
fun EditText.setLoseFocus() {
    isFocusable = false
    isEnabled = false
    isFocusableInTouchMode = false
    isCursorVisible = false
}

/**
 *  设置获取焦点
 */
fun EditText.setFocus() {
    isFocusable = true
    isEnabled = true
    isFocusableInTouchMode = true
    isCursorVisible = true
}