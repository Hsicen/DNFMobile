package com.hsicen.core.ui.window

import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.transition.Slide
import com.free.extensions.*
import com.hsicen.extensions.extensions.beginDelayedTransition
import com.hsicen.extensions.extensions.color
import com.hsicen.extensions.extensions.dimen
import com.hsicen.extensions.extensions.dp2px
import com.lxt.app.vehicle.R
import com.lxt.app.vehicle.data.model.Vehicle
import com.lxt.core.arch.adapter.CoreRvAdapter
import com.lxt.core.ui.kdecoration.KItemDecoration
import com.lxt.core.utils.glide.loadVehicleLogo
import kotlinx.android.synthetic.main.popup_vehicle_selector.view.*

/**
 * 车辆选择器.
 * Created by tomlezen.
 * Data: 2019/5/22.
 * Time: 16:17.
 */
class KVehicleSelector(private val ctx: Context) {

    /** PopupWindow. */
    private var popupWindow: PopupWindow? = null

    /** 默认车辆id. */
    private var defSelectVehicleId: Long? = null

    /** 选择的车辆. */
    private var selectVehicle: Vehicle.Normal? = null

    /** 显示状态回调. */
    private var onShownStateCallback: ((isShown: Boolean) -> Unit)? = null

    /** 选择回调. */
    private var onSelectCallback: ((vehicle: Vehicle.Normal, popupWindow: PopupWindow?) -> Unit)? =
        null

    /** 添加认证车点击回调*/
    private var onAddCallback: (() -> Unit)? = null

    /** 菜单适配器. */
    private val adapter by lazy {
        CoreRvAdapter<Vehicle.Normal>(R.layout.item_vehicle_select).apply {
            render = { model, helper ->
                helper.getView<ImageView>(R.id.iv_vehicle_logo)
                    .loadVehicleLogo(model.type?.brandImageUrl)
                helper.setText(
                    R.id.tv_vehicle_name,
                    "${model.type?.brand} ${model.type?.serial} ${model.type?.model}"
                )
                (defSelectVehicleId == model.id).yes {
                    helper.getView<TextView>(R.id.tv_vehicle_name).isSelected = true
                    helper.getView<CheckBox>(R.id.cb_check).isChecked = true
                }.no {
                    helper.getView<TextView>(R.id.tv_vehicle_name).isSelected = false
                    helper.getView<CheckBox>(R.id.cb_check).isChecked = false
                }

                //logo区别设置
                if (model is Vehicle.Vip) {
                    helper.setImageResource(R.id.iv_vehicle_label, R.mipmap.ic_vehicle_vip_flag)
                } else helper.setImageResource(R.id.iv_vehicle_label, R.mipmap.ic_vehicle_idf)
            }

            closeLoadAnimation()
            onItemClick = { _, _, model ->
                if (model.id != defSelectVehicleId) {
                    this@KVehicleSelector.selectVehicle = model
                    onSelectCallback?.invoke(model, popupWindow)
                } else {
                    popupWindow?.dismiss()
                }
            }
        }
    }

    /**
     * 设置车辆数据.
     * @param data List<Vehicle.Normal>
     * @param defSelectVehicleId: Int?
     * @return KVehicleSelector
     */
    fun setVehicleData(
        data: List<Vehicle.Normal>,
        defSelectVehicleId: Long? = null
    ): KVehicleSelector {
        this.defSelectVehicleId = defSelectVehicleId
        adapter.setNewData(data)
        return this
    }

    /**
     * 选择回调.
     * @param onSelectCallback ((vehicle: Vehicle.Normal) -> Unit)?
     * @return KVehicleSelector
     */
    fun selectCallback(onSelectCallback: ((vehicle: Vehicle.Normal, popupWindow: PopupWindow?) -> Unit)? = null): KVehicleSelector {
        this.onSelectCallback = onSelectCallback
        return this
    }

    /**
     * 显示状态回调.
     * @param onShownStateCallback ((isShown: Boolean) -> Unit)?
     * @return KVehicleSelector
     */
    fun shownStateCallback(onShownStateCallback: ((isShown: Boolean) -> Unit)? = null): KVehicleSelector {
        this.onShownStateCallback = onShownStateCallback
        return this
    }

    /**
     * 新添加车辆回调
     * @param addNew 添加回调
     * @return KVehicleSelector
     */
    fun onAddCallback(addNew: () -> Unit): KVehicleSelector {
        this.onAddCallback = addNew
        return this
    }

    /**
     * 显示,
     * @param anchor View
     */
    fun showAsDropDown(anchor: View) {
        val contentView = anchor.context.inflate(R.layout.popup_vehicle_selector)
        popupWindow = DismissHookPopupWindow(
            contentView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            // 拦截dismiss方法.
            dismissHooker = {
                contentView.view_bg.animate().alpha(0f).start()
                (contentView as ViewGroup).beginDelayedTransition(Slide(Gravity.TOP).apply {
                    doOnEnd {
                        superDismiss()
                        onSelectCallback?.invoke(
                            this@KVehicleSelector.selectVehicle ?: return@doOnEnd, popupWindow
                        )
                    }

                })
                contentView.ll_content.isVisible = false
                onShownStateCallback?.invoke(false)
                true
            }

            isOutsideTouchable = true
            contentView.clickThrottle {
                dismiss()
            }

            setOnDismissListener {
                onSelectCallback = null
                onShownStateCallback = null
                onAddCallback = null
            }

            contentView.rv_content.adapter = adapter

            // 添加认证车辆处理
            contentView.cl_add_vehicle.clickThrottle {
                onAddCallback?.invoke()
                dismiss()
            }

            contentView.rv_content.addItemDecoration(
                KItemDecoration(
                    ctx.dp2px(1),
                    ctx.color(com.lxt.core.R.color.divider_line5),
                    ctx.dimen(com.lxt.core.R.dimen.padding_z2),
                    ctx.dimen(com.lxt.core.R.dimen.padding_z2),
                    endOffsetCount = 1
                )
            )
        }
        // 动态调整Rv高度
        if (adapter.data.size > 5) {
            contentView.rv_content.layoutParams.height = 5 * ctx.dp2px(60)
        }
        popupWindow?.animationStyle = 0
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
            val location = IntArray(2)
            anchor.getLocationOnScreen(location)
            popupWindow?.showAtLocation(anchor, Gravity.NO_GRAVITY, 0, location[1] + anchor.height)
        } else {
            popupWindow?.showAsDropDown(anchor)
        }
        contentView.post {
            contentView.view_bg.animate().alpha(1f).start()
            (contentView as ViewGroup).beginDelayedTransition(Slide(Gravity.TOP))
            contentView.ll_content.isVisible = true
        }
        onShownStateCallback?.invoke(true)
    }

    companion object {

        fun with(context: Context) = KVehicleSelector(context.applicationContext)
    }

    class DismissHookPopupWindow(contentView: View, width: Int, height: Int, focusable: Boolean) :
        PopupWindow(contentView, width, height, focusable) {

        /** dismiss方法钩子. */
        var dismissHooker: (() -> Boolean)? = null

        override fun dismiss() {
            if (dismissHooker?.invoke() != true) {
                super.dismiss()
            }
        }

        /**
         * 防止dismiss方法被钩住.
         */
        fun superDismiss() {
            super.dismiss()
        }
    }
}
