package com.jacky.wanandroidkotlin.model.api

import androidx.annotation.Nullable
import com.jacky.wanandroidkotlin.BuildConfig
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.model.api.ApiManger.Companion.SUCCESS_CODE
import com.jacky.wanandroidkotlin.model.entity.WanResponse
import com.jacky.wanandroidkotlin.model.local.ContextModel
import com.zenchn.support.utils.NetworkUtils
import retrofit2.HttpException
import java.io.Closeable
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * @author:Hzj
 * @date  :2020/5/13
 * desc  ：
 * record：
 */
internal class ApiException(val msg: String?) : RuntimeException(msg) {
    override val message: String
        get() = "ApiException message:$msg"
}

/**
 * 常用响应码
 */
internal object ResponseCode {

    const val CODE_200 = 200//请求成功

    const val CODE_400 = 400//错误请求
    const val CODE_401 = 401//未授权
    const val CODE_403 = 403//请求禁止
    const val CODE_404 = 404//未找到页面
    const val CODE_405 = 405//方法禁用
    const val CODE_408 = 408//请求超时

    const val CODE_500 = 500//服务器内部错误
    const val CODE_502 = 502//错误网关
    const val CODE_503 = 503//服务不可用
}


class ApiManger {
    companion object {
        const val SUCCESS_CODE = 0//请求成功返回码
        const val ERROR_CODE = -1//请求失败返回码
    }
}

/**
 * 消息
 */
internal object Message {

    val DEFAULT_EXCEPTION
        get() = "系统升级维护中，请稍后再试"
    val NOT_FOUND_EXCEPTION
        get() = "找不到访问资源，请稍后再试"
    val AUTH_REFUSE_EXCEPTION
        get() = "授权失败，请重新登录"
    val CONNECT_EXCEPTION
        get() = "连接服务器失败，请稍后再试"
    val SOCKET_TIMEOUT_EXCEPTION
        get() = "连接服务器超时，请稍后再试"
    val NETWORK_NOT_AVAILABLE
        get() = "网络错误，请检查您的网络"
}

/**
 * 请求结果是否成功
 */
fun WanResponse<*>?.isApiSuccess(): Boolean =
    this?.let { it.errorCode == SUCCESS_CODE } ?: false

/**
 * 提取普通接口异常中包含的错误信息
 */
fun Throwable.dispatch(
    @Nullable defaultErrorMsg: String = Message.DEFAULT_EXCEPTION,
    msgResult: (String) -> Unit,
    apiRefused: () -> Unit
) {
    if (NetworkUtils.isNetworkAvailable(ContextModel.getApplicationContext())) {
        when (this) {
            is ConnectException -> msgResult.invoke(Message.CONNECT_EXCEPTION)
            is SocketTimeoutException -> msgResult.invoke(Message.SOCKET_TIMEOUT_EXCEPTION)
            is ApiException -> msgResult.invoke(msg ?: defaultErrorMsg)
            is HttpException -> {
                when (code()) {
                    ResponseCode.CODE_401 -> apiRefused.invoke()
                    ResponseCode.CODE_404 -> msgResult.invoke(Message.NOT_FOUND_EXCEPTION)
                    else -> {
                        msgResult.invoke(
                            safelyRun(
                                catch = { if (BuildConfig.DEBUG) message() },
                                runnable = {
                                    response()?.errorBody()?.toString() ?: defaultErrorMsg
                                }
                            ) ?: defaultErrorMsg
                        )
                    }
                }
            }
            else -> msgResult.invoke(defaultErrorMsg)
        }
    } else {
        msgResult.invoke(Message.NETWORK_NOT_AVAILABLE)
    }
}

inline fun <S, R> S.safelyRun(
    noinline catch: ((Exception) -> Unit)? = null,
    noinline finally: (() -> Unit)? = null,
    crossinline runnable: (S) -> R?
): R? = try {
    runnable.invoke(this)
} catch (e: Exception) {
    catch?.invoke(e)
    null
} finally {
    finally?.invoke()
    if (this is Closeable) {
        safelyClose()
    }
}

fun Closeable.safelyClose() {
    try {
        this.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}