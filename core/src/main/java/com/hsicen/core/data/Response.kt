package com.hsicen.core.data

import com.google.gson.annotations.SerializedName
import com.hsicen.core.R
import com.hsicen.extensions.utils.GlobalContext


/**
 * 作者：hsicen  2020/8/10 22:19
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：api请求结果
 *
 * @param T 泛型实体类
 * @property code Int 服务端请求状态码.
 * @property data T 服务端请求数据实体.
 * @property msg String? 服务端请求信息.
 */
open class Response<T>(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val data: T,
    @SerializedName("msg")
    val msg: String? = null
) {

    /**
     * 是否成功.
     * 根据服务端code判断.
     */
    val isSuccess: Boolean
        get() = code == ServerCodes.SUCCESS

    /**
     * 列表数据.
     * @param T
     * @constructor
     */
    class List<T>(
        code: Int,
        data: ListData<T>,
        msg: String?
    ) : Response<ListData<T>>(code, data, msg)

    class ListData<T>(
        @SerializedName("list")
        val list: kotlin.collections.List<T>
    )

    /**
     * 分页列表请求类型.
     * @param T
     * @constructor
     */
    class PaginationList<T>(
        code: Int,
        data: Pagination<T>,
        msg: String?
    ) : Response<Pagination<T>>(code, data, msg)

    /**
     * 分页实体.
     * @param T
     * @property data List<T> 数据集合.
     * @constructor
     */
    data class Pagination<T>(
        @SerializedName("result", alternate = ["list"])
        val data: kotlin.collections.List<T>
    )

    /**
     * 服务端返回结果异常.
     * @property response Response<*>
     * @constructor
     */
    class Exception(val response: Response<*>) : RuntimeException(
        if (response.msg.isNullOrEmpty()) {
            GlobalContext.getContext().getString(
                R.string.error_not_network
            )
        } else {
            response.msg
        }
    )

    /**
     * 拆箱数据 获得T.
     * @return T
     */
    fun unwrap(): T {
        if (!isSuccess) {
            // 不成功 直接抛出异常
            throw Exception(this)
        }
        return data
    }
}
