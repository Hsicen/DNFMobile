package com.hsicen.core.arch

import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.hsicen.extensions.extensions.hideKeyboard
import com.hsicen.extensions.extensions.hideKeyboardWithDelay
import com.hsicen.extensions.extensions.yes


/**
 * 作者：hsicen  2020/8/10 20:58
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：核心Activity
 */
abstract class CoreActivity(
    @LayoutRes private val layoutResID: Int
) : AppCompatActivity() {

    /** 点击外部关闭软件盘. */
    var touchOutSideCloseKeyboard = false

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResID)

        initVariable()
        initView()
        initData()
    }

    /**
     * 初始化Intent数据
     */
    protected open fun initVariable() {

    }

    /**
     * 初始化View
     */
    protected open fun initView() {

    }

    /**
     * 初始化数据
     */
    protected open fun initData() {

    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val result = super.dispatchTouchEvent(ev)
        (!result && touchOutSideCloseKeyboard && ev?.action == MotionEvent.ACTION_DOWN).yes {
            val view = currentFocus
            (isHideKeyboard(view ?: return@yes, ev ?: return@yes)).yes {
                view.hideKeyboardWithDelay()
                view.clearFocus()
            }
        }
        return result
    }

    override fun onDestroy() {
        hideKeyboard()
        super.onDestroy()
    }

    /**
     * 判断是否需要隐藏软键盘.
     * @param v View
     * @param ev MotionEvent
     * @return Boolean
     */
    private fun isHideKeyboard(v: View, ev: MotionEvent): Boolean {
        if (v is EditText) {
            val location = IntArray(2)
            v.getLocationInWindow(location)
            val left = location[0]
            val top = location[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return !(ev.x > left && ev.x < right && ev.y > top && ev.y < bottom)
        }
        return false
    }

    /**
     * LiveData 扩展.
     * @receiver LiveData<T>
     * @param block (data: T) -> Unit
     */
    infix fun <T> LiveData<T>?.observe(block: (data: T) -> Unit) =
        this?.observe(this@CoreActivity, Observer { block.invoke(it) })
}

/**
 * 绑定toolbar以及返回按钮
 * @receiver AppCompatActivity
 * @param toolbar Toolbar?
 */
fun AppCompatActivity.setToolbarWithBack(toolbar: Toolbar?) {
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    toolbar?.setNavigationOnClickListener {
        onBackPressed()
    }
}
