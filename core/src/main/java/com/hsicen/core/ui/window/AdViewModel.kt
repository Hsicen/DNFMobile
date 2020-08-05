package com.hsicen.core.ui.window

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.alibaba.android.arouter.launcher.ARouter
import com.free.extensions.clickThrottle
import com.free.extensions.dp2px
import com.lxt.app.account.data.AccountDataService
import com.lxt.app.account.data.LoginState
import com.lxt.app.account.data.isLoggedIn
import com.lxt.app.ad.api.AdService
import com.lxt.app.ad.model.AdResult
import com.lxt.app.ad.model.ContractAdResult
import com.lxt.app.ad.model.NormalAdResult
import com.lxt.app.ad.model.TabBubble
import com.lxt.app.ad.other.AdSpCache
import com.lxt.app.ad.other.ContractRequest
import com.lxt.app.api.BuriedPointService
import com.lxt.app.api.navBuriedPoint
import com.lxt.app.growth.data.ExpCode
import com.lxt.app.growth.data.GrowthDataService
import com.lxt.app.vehicle.data.VehicleDataService
import com.lxt.app.web.api.KWebs
import com.lxt.app.xyt.data.model.Coupon
import com.lxt.core.GlobalConfigs
import com.lxt.core.arch.CoreViewModel
import com.lxt.core.arouter.ARouters
import com.lxt.core.arouter.ARouters.Web.ELECTRONIC_GUARANTEE
import com.lxt.core.arouter.getAService
import com.lxt.core.arouter.navigation
import com.lxt.core.coreComponent
import com.lxt.core.data.onFailure
import com.lxt.core.data.onSuccess
import com.lxt.core.data.safeApiCall
import com.lxt.core.ui.kad.KAdDialog
import com.lxt.core.ui.kalert.KAlertDialog
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *  广告弹窗逻辑处理(优先级6)
 *  紧急弹窗>自定义弹窗>定向优惠券弹窗>普通弹窗广告>欣悦途弹窗广告
 * Date: 2019/04/13.
 * Time: 10:02.
 */
open class AdViewModel @Inject constructor(private val service: AdService) : CoreViewModel() {

    /***当前车辆*/
    private val currentVehicle by lazy {
        ARouter.getInstance().navigation(VehicleDataService::class.java)?.curVehicle
    }
    private var mData = listOf<ContractAdResult>()
    private var mEmergencyData: AdResult? = null
    private var mNormalData: NormalAdResult? = null
    private var mContractBack: (() -> Unit)? = null
    var mCouponData: Coupon? = null

    private var mBubbleData: TabBubble? = null
    private var mBubbles: HashMap<String, View>? = HashMap()
    private var mBubbleIds: HashMap<String, String>? = HashMap()

    private var mViewGroup: ViewGroup? = null
    private val loginState
        get() = getAService<AccountDataService>()?.loginState

    /***  标记服务协议已读.*/
    private fun changeServiceContractData(windowIds: IntArray) = launch {
        safeApiCall { service.changeServiceContractData(ContractRequest(windowIds)) }
    }

    /***  获取服务协议信息*/
    fun getContractData(responseBack: (() -> Unit)? = null) =
        launch {
            val loginState =
                getAService<AccountDataService>()?.loginState?.value ?: LoginState.LOGOUT
            if (!loginState.isLoggedIn) {
                responseBack?.invoke()
                mData = listOf()
                return@launch
            }

            safeApiCall { service.getServiceContractData() }
                .onSuccess {
                    mData = it.list
                    responseBack?.invoke()
                }.onFailure {
                    mData = listOf()
                    responseBack?.invoke()
                }
        }

    private fun showContractServiceDialog(
        listData: List<ContractAdResult>,
        context: Context,
        index: Int
    ) {
        val ids = IntArray(listData.size)
        listData.forEachIndexed { pos, adResult ->
            ids[pos] = adResult.id
        }

        if (listData.size > index) {
            var position = index
            val itemData = listData[index]

            KAlertDialog.show(context) {
                title = itemData.title
                message = itemData.content
                negativeText = "暂时不看"
                positiveText = "立即查看"
                isCancelable = false
                isCanceledOnTouchOutside = false
            }.onNegativeButtonClick { dialog ->
                dialog.dismiss()
                showContractServiceDialog(listData, context, ++position)
                changeServiceContractData(intArrayOf(itemData.id))
                mData = mData.filter {
                    return@filter it.id != itemData.id
                }
            }.onPositiveButtonClick {
                changeServiceContractData(ids)
                it.dismiss()
                mData = listOf()
                ELECTRONIC_GUARANTEE.navigation {
                    withLong(KWebs.EXTRA_VEHICLE_ID, itemData.vehicleId)
                }
                mContractBack?.invoke()
            }
        } else mContractBack?.invoke()
    }

    /*** 获取紧急弹窗*/
    fun getEmergencyAd(dataCallback: (() -> Unit)? = null) =
        launch {
            safeApiCall { service.getEmergencyAdData() }
                .onSuccess {
                    mEmergencyData = it
                    dataCallback?.invoke()
                }.onFailure {
                    mEmergencyData = null
                    dataCallback?.invoke()
                }
        }

    /*** 获取定向优惠券*/
    fun getDirectCoupon(dataCallback: (() -> Unit)? = null) =
        launch {
            dataCallback?.invoke()
        }

    /*** 获取普通广告*/
    fun getNormalAd(dataCallback: (() -> Unit)? = null) =
        launch {
            val loginState =
                getAService<AccountDataService>()?.loginState?.value ?: LoginState.LOGOUT
            if (!loginState.isLoggedIn) {
                dataCallback?.invoke()
                mNormalData = null
                return@launch
            }

            safeApiCall { service.getAdData(currentVehicle?.value?.id?.toString()) }
                .onSuccess {
                    mNormalData = it
                    dataCallback?.invoke()
                }
                .onFailure {
                    mNormalData = null
                    dataCallback?.invoke()
                }
        }

    /*** 显示电子保函弹窗封装*/
    fun showContract(context: Context?, nextCallback: (() -> Unit)? = null) {
        context ?: return
        mContractBack = nextCallback

        if (mData.isNullOrEmpty()) {
            nextCallback?.invoke()
            return
        }

        showContractServiceDialog(mData, context, 0)
    }

    /*** 显示普通广告*/
    fun showNormalAd(context: Context?, nextCallback: (() -> Unit)?) {
        context ?: return
        if (mNormalData == null || mNormalData!!.list.isNullOrEmpty()) {
            nextCallback?.invoke()
            return
        }

        val dataService = ARouter.getInstance().navigation(AccountDataService::class.java)
        val userId = dataService?.userId
        val loginState = dataService?.loginState?.value ?: LoginState.LOGOUT
        val imei = context.coreComponent().kimei().imei

        mNormalData!!.list!!.forEachIndexed { index, normalAd ->
            if (normalAd == null) {
                if (index == mNormalData!!.list!!.size - 1) nextCallback?.invoke()
                return@forEachIndexed
            }

            val cacheValue =
                if (loginState.isLoggedIn) "${userId}_${normalAd.id}" else "${imei}_${normalAd.id}"
            val cacheIds = AdSpCache.cacheIdSet
            if (cacheIds.contains(cacheValue)) {
                if (index + 1 == mNormalData?.list?.size ?: 1) nextCallback?.invoke()
                return@forEachIndexed
            }

            if (GlobalConfigs.SHOW_NORMAL_ALERT) {
                nextCallback?.invoke()
                return
            }

            showNormalAlert(normalAd, context, nextCallback)
            AdSpCache.cacheIdSet.add(cacheValue)
            GlobalConfigs.SHOW_NORMAL_ALERT = true
            return
        }
    }

    /*** 展示普通广告弹窗*/
    private fun showNormalAlert(
        normalAd: NormalAdResult.NormalAd,
        context: Context,
        nextCallback: (() -> Unit)?
    ) {
        KAdDialog.show(context) {
            title = normalAd.title ?: ""
            imgUrl = normalAd.coverPhoto ?: ""
            isEmergency = false
            isCancelable = true
        }.onImgClick { dialog ->
            navBuriedPoint(
                module = BuriedPointService.MODULE_INDEX,
                page = BuriedPointService.PAGE_INDEX,
                event = BuriedPointService.EVENT_POPUP_AD_CLICK,
                properties = HashMap<String, String>().apply {
                    put(BuriedPointService.TARGET_POPUP_AD_ID, "${normalAd.id}")
                }
            )

            if (normalAd.needLogin == true) {
                //需要登录
                if (!(loginState?.value ?: LoginState.LOGOUT).isLoggedIn) {
                    //未登录，跳转登录
                    ARouters.Account.LOGIN.navigation()
                    return@onImgClick
                }
            }
            if (normalAd.target != null) {
                normalAd.target?.jumpToNext(normalAd.title ?: "")
                dialog.dismiss()
            }
            ARouter.getInstance().navigation(GrowthDataService::class.java)
                ?.reportExpTask(ExpCode.VIEW_VUS_ACTIVITY)
        }.onDismiss {
            nextCallback?.invoke()
        }

        navBuriedPoint(
            module = BuriedPointService.MODULE_INDEX,
            page = BuriedPointService.PAGE_INDEX,
            event = BuriedPointService.EVENT_POPUP_AD_SHOW,
            properties = HashMap<String, String>().apply {
                put(BuriedPointService.TARGET_POPUP_AD_ID, "${normalAd.id}")
            }
        )
    }

    /*** 显示紧急弹窗*/
    fun showEmergency(context: Context?, nextCallback: (() -> Unit)? = null) {
        context ?: return
        if (mEmergencyData == null) {
            nextCallback?.invoke()
            return
        }

        KAdDialog.show(context) {
            title = mEmergencyData?.title ?: ""
            imgUrl = mEmergencyData?.coverPhoto ?: ""
            isEmergency = true
            isCancelable = false
            isCanceledOnTouchOutside = false
        }.onImgClick {
            navBuriedPoint(
                module = BuriedPointService.MODULE_INDEX,
                page = BuriedPointService.PAGE_INDEX,
                event = BuriedPointService.EVENT_POPUP_AD_CLICK,
                properties = HashMap<String, String>().apply {
                    put(BuriedPointService.TARGET_POPUP_AD_ID, "${mEmergencyData?.id}")
                }
            )

            mEmergencyData?.target?.jumpToNext(mEmergencyData?.title)
        }

        navBuriedPoint(
            module = BuriedPointService.MODULE_INDEX,
            page = BuriedPointService.PAGE_INDEX,
            event = BuriedPointService.EVENT_POPUP_AD_SHOW,
            properties = HashMap<String, String>().apply {
                put(BuriedPointService.TARGET_POPUP_AD_ID, "${mEmergencyData?.id}")
            }
        )
    }

    /*** 获取首页底部小气泡弹窗*/
    fun getTabBubble(dataCallback: (() -> Unit)? = null) =
        launch {
            safeApiCall { service.getTabBubble() }
                .onSuccess {
                    mBubbleData = it
                    dataCallback?.invoke()
                }.onFailure {
                    mBubbleData = null
                    dataCallback?.invoke()
                }
        }

    /*** 显示底部Tab气泡*/
    @SuppressLint("InflateParams")
    fun showTabBubble(mAct: Activity?, clickCallback: ((position: Int) -> Unit)? = null) {
        mAct ?: return
        if (mBubbleData == null) return
        if (mViewGroup == null) mViewGroup = mAct.findViewById(android.R.id.content)
        removeAllBubble()

        val dataService = ARouter.getInstance().navigation(AccountDataService::class.java)
        val userId = dataService?.userId
        val loginState = dataService?.loginState?.value ?: LoginState.LOGOUT
        val imei = mAct.coreComponent().kimei().imei

        mBubbleData?.list?.forEach { bubble ->
            val bubblePosition = bubble?.getTab()
            bubblePosition ?: return@forEach

            val cacheValue =
                if (loginState.isLoggedIn) "${userId}_${bubble.id}" else "${imei}_${bubble.id}"
            val cacheIds = AdSpCache.tabBubbleIds
            if (cacheIds.contains(cacheValue)) return@forEach

            val bubbleView =
                LayoutInflater.from(mAct).inflate(R.layout.item_tab_bubble, null)
            val bubbleText = bubbleView.findViewById<AppCompatTextView>(R.id.tv_bubble)
            bubbleText.text = bubble.content ?: ""
            bubbleView.measure(0, 0)

            bubbleText.clickThrottle {
                removeTabBubble("$bubblePosition")
                clickCallback?.invoke(bubblePosition)
            }

            bubbleView.apply {
                mBubbles?.put("$bubblePosition", bubbleView)
                mBubbleIds?.put("$bubblePosition", cacheValue)
                mViewGroup?.addView(bubbleView, bubbleView.measuredWidth, bubbleView.measuredHeight)

                val bubbleParams = (layoutParams as ViewGroup.MarginLayoutParams)
                val perWidth = mViewGroup!!.width / 5
                bubbleParams.leftMargin =
                    (bubblePosition - 1) * perWidth + (perWidth / 2 - bubbleView.measuredWidth / 2)
                bubbleParams.topMargin = mViewGroup!!.height - mAct.dp2px(69)
            }
        }
    }

    /*** 移除所有底部气泡*/
    fun removeAllBubble() {
        runCatching {
            val entries = mBubbles?.entries ?: return@runCatching

            for (item in entries) {
                mViewGroup?.removeView(item.value)
            }
            mBubbles?.clear()
            mBubbleIds?.clear()
        }
    }

    /*** 移除指定位置气泡*/
    fun removeTabBubble(position: String?) {
        position ?: return

        val positionBubble = mBubbles?.get(position)
        positionBubble?.let {
            mViewGroup?.removeView(it)
            mBubbles?.remove(position)
        }

        val positionId = mBubbleIds?.get(position)
        positionId?.let {
            AdSpCache.tabBubbleIds.add(positionId)
            mBubbleIds?.remove(position)
        }
    }

    fun changeStatus(visible: Boolean) {
        runCatching {
            val entries = mBubbles?.entries ?: return@runCatching

            for (item in entries) {
                item.value.visibility = if (visible) View.VISIBLE else View.INVISIBLE
            }
        }
    }
}
