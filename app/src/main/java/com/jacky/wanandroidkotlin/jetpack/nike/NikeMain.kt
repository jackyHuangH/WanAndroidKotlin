package com.jacky.wanandroidkotlin.jetpack.nike

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.provider.Telephony
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import com.jacky.support.permission.requestSelfPermissions
import com.jacky.support.router.Router
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseActivity
import com.jacky.wanandroidkotlin.databinding.ActivityNikeMainBinding


/**
 * @author:Hzj
 * @date  :2020/8/28
 * desc  ：
 * record：
 */
class NikeMainActivity : BaseActivity<ActivityNikeMainBinding>() {
    //电话状态的广播
    private val PHONE_STATE_ACTION = "android.intent.action.PHONE_STATE"
    private val mSb by lazy { StringBuilder() }

    override fun getLayoutId(): Int = R.layout.activity_nike_main

    override fun initWidget() {
        initPermissions()
//        initBroadcastReceiver()
//        initPhoneStateListener()
//        registerSmsListener()

        //测试服务中监听
        startService(Intent(this, PhoneSmsService::class.java))
    }

    private fun initPermissions() {
        //申请存储权限
        requestSelfPermissions(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_SMS,
            callback = { granted, never ->
                if (!granted) {
                    showMessage("请授予权限")
                }
            })
    }


    //自定义广播接收器
    private val comingCallBR: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, " action = " + intent.action)
            if (intent.action != null
                && intent.action == PHONE_STATE_ACTION
            ) {
                //电话状态
                val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                Log.d(TAG, " phone state = $state")
                // 电话正在响铃，即来电
                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING, ignoreCase = true)) {
                    //正在响铃...
                    //来电号码
                    val comingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                    if (comingNumber == null || comingNumber.isEmpty()) {
                        return
                    }
                    Log.d(TAG, " 来电号码 = $comingNumber")
                    updateLog(" 来电号码 = $comingNumber")
//                    if (comingCallListener != null) {
//                        comingCallListener.onCalling(curTimeToFormatNoYearMills(), comingNumber, null)
//                    }
                } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK, ignoreCase = true)) {
                    //正在通话...
                    Log.d(TAG, " 正在通话...")
                    updateLog(" 正在通话...")
                } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE, ignoreCase = true)) {
                    //电话挂断
                    Log.d(TAG, " 电话挂断...")
                    updateLog(" 电话挂断...")
                }
            }
        }
    }

    //注册监听来电的广播接收者
    fun initBroadcastReceiver(): Boolean {
        val intentFilter = IntentFilter()
        intentFilter.addAction(PHONE_STATE_ACTION)
        registerReceiver(comingCallBR, intentFilter)
        return true
    }

    //注销广播
    fun unregisterReceiver(): Boolean {
        unregisterReceiver(comingCallBR)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
//        unregisterReceiver()
//        cancelPhoneStateListener()
//        unRegisterSmsListener()
    }

    private fun updateLog(msg: String?) {
        mSb.append(msg).append("\n")
        mViewBinding.tvLog.text = mSb.toString()
    }

    //======================================================================================

    // 电话管理者对象
    private var mTelephonyManager: TelephonyManager? = null

    // 电话状态监听者
    private var mPhoneStateListener: PhoneStateListener? = null

    //初始化PhoneStateListener
    fun initPhoneStateListener(): Boolean {
        mTelephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        mPhoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String) {
                when (state) {
                    TelephonyManager.CALL_STATE_IDLE -> {
                        Log.d(TAG, "onCallStateChanged(): 电话挂断...")
                        updateLog("onCallStateChanged(): 电话挂断...")
                    }
                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        Log.d(TAG, "onCallStateChanged(): 正在通话...")
                        updateLog("onCallStateChanged(): 正在通话...")
                    }
                    TelephonyManager.CALL_STATE_RINGING -> {
                        //正在响铃...
                        if (phoneNumber.isNotEmpty()) {
                            Log.d(TAG, "onCallStateChanged(): 来电号码 = $phoneNumber")
                            updateLog("onCallStateChanged(): 来电号码 = $phoneNumber")
                        }
//                        if (comingCallListener != null) {
//                            comingCallListener.onCalling(curTimeToFormatNoYearMills(), phoneNumber, null)
//                        }
                    }
                }
            }
        }
        //开启监听
        mTelephonyManager?.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        return true
    }


    fun cancelPhoneStateListener(): Boolean {
        // 取消来电的电话状态监听服务
        if (mTelephonyManager != null && mPhoneStateListener != null) {
            mTelephonyManager?.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE)
            return true
        }
        return false
    }


    //----------------------sms--------------------------

    private val SMS_URI = "content://sms/"
    private val SMS_INBOX_URI = "content://sms/inbox/"
    private val PROJECTION = arrayOf(
        Telephony.Sms._ID,
        Telephony.Sms.ADDRESS,
        Telephony.Sms.BODY,
        Telephony.Sms.DATE
    )
    private var mReceivedMsgDate: Long = 0

    private val mSmsContentObserver: ContentObserver =
        object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange) //To change body of overridden methods use File | Settings | File Templates.
                Log.d(TAG, "onChange")
                val cursor: Cursor? = contentResolver.query(
                    Uri.parse(SMS_INBOX_URI),
                    PROJECTION, null, null, null
                )
                if (cursor != null && !cursor.isClosed()) {
                    if (cursor.count > 0) {
                        //只读第一条
//                        cursor.moveToFirst()
//                        Log.d(
//                            TAG,
//                            "接收到短信: " + cursor.getString(1)
//                                .toString() + ", 内容: " + cursor.getString(2)
//                        )
//                        updateLog(
//                            "接收到短信: " + cursor.getString(1)
//                                .toString() + ", 内容: " + cursor.getString(2)
//                        )

                        //循环读取所有短信
                        while (cursor.moveToNext()) {
                            val date: Long =
                                cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE))
                            if (mReceivedMsgDate != date) {
                                Log.d(
                                    TAG,
                                    "接收到短信: " + cursor.getString(1)
                                        .toString() + ", 内容: " + cursor.getString(2)
                                )
                                updateLog(
                                    "接收到短信: " + cursor.getString(1)
                                        .toString() + ", 内容: " + cursor.getString(2)
                                )
                                mReceivedMsgDate = date
                            } else {
                                Log.d(TAG, "the same item, ignore it")
                            }
                        }
                    }
                    cursor.close()
                }
            }
        }

    private fun registerSmsListener() {
        getContentResolver().registerContentObserver(Uri.parse(SMS_URI), true, mSmsContentObserver)
    }

    private fun unRegisterSmsListener() {
        getContentResolver().unregisterContentObserver(mSmsContentObserver)
    }

    //----------------------sms--------------------------

    companion object {
        const val TAG = "Nike"

        fun launch(from: Activity) {
            Router
                .newInstance()
                .from(from)
                .to(NikeMainActivity::class.java)
                .launch()
        }
    }
}

/**
 * 监听电话短信的service
 */
class PhoneSmsService : Service() {
    private val TAG = "PhoneSmsService"

    //电话状态的广播
    private val PHONE_STATE_ACTION = "android.intent.action.PHONE_STATE"
    private val mSb by lazy { StringBuilder() }

    //用于在子线程中执行异步操作（这是指短信的查询操作）
    private var handlerThread: HandlerThread? = null
    private var threadHandler: Handler? = null

    override fun onCreate() {
        super.onCreate()
        handlerThread = object : HandlerThread("sms") {
            override fun onLooperPrepared() {
                super.onLooperPrepared()
                threadHandler = Handler(handlerThread!!.looper)
            }
        }
        handlerThread?.start()
        Log.d(TAG, " phone state service onCreate")
        initBroadcastReceiver()
        registerSmsListener()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, " phone state service onDestroy")
        unregisterReceiver()
        unRegisterSmsListener()
    }

    //----------------------sms--------------------------

    //自定义广播接收器
    private val comingCallBR: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, " action = " + intent.action)
            if (intent.action != null
                && intent.action == PHONE_STATE_ACTION
            ) {
                //电话状态
                val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                Log.d(TAG, " phone state = $state")
                // 电话正在响铃，即来电
                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING, ignoreCase = true)) {
                    //正在响铃...
                    //来电号码
                    val comingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                    if (comingNumber == null || comingNumber.isEmpty()) {
                        return
                    }
                    Log.d(TAG, " 来电号码 = $comingNumber")
                    updateLog(" 来电号码 = $comingNumber")
//                    if (comingCallListener != null) {
//                        comingCallListener.onCalling(curTimeToFormatNoYearMills(), comingNumber, null)
//                    }
                } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK, ignoreCase = true)) {
                    //正在通话...
                    Log.d(TAG, " 正在通话...")
                    updateLog(" 正在通话...")
                } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE, ignoreCase = true)) {
                    //电话挂断
                    Log.d(TAG, " 电话挂断...")
                    updateLog(" 电话挂断...")
                }
            }
        }
    }

    //注册监听来电的广播接收者
    fun initBroadcastReceiver(): Boolean {
        val intentFilter = IntentFilter()
        intentFilter.addAction(PHONE_STATE_ACTION)
        registerReceiver(comingCallBR, intentFilter)
        return true
    }

    //注销广播
    fun unregisterReceiver(): Boolean {
        unregisterReceiver(comingCallBR)
        return true
    }


    //----------------------sms--------------------------

    private val SMS_URI = "content://sms/"
    private val SMS_INBOX_URI = "content://sms/inbox/"
    private val PROJECTION = arrayOf(
        Telephony.Sms._ID,
        Telephony.Sms.ADDRESS,
        Telephony.Sms.BODY,
        Telephony.Sms.DATE
    )
    private var mReceivedMsgDate: Long = 0

    private val mSmsContentObserver: ContentObserver =
        object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange) //To change body of overridden methods use File | Settings | File Templates.
                Log.d(TAG, "onChange")
                //子线程查询短信
                threadHandler?.post {
                    val cursor: Cursor? = contentResolver.query(
                        Uri.parse(SMS_INBOX_URI),
                        PROJECTION, null, null, null
                    )
                    if (cursor != null && !cursor.isClosed()) {
                        if (cursor.count > 0) {
                            //只读第一条
//                        cursor.moveToFirst()
//                        Log.d(
//                            TAG,
//                            "接收到短信: " + cursor.getString(1)
//                                .toString() + ", 内容: " + cursor.getString(2)
//                        )
//                        updateLog(
//                            "接收到短信: " + cursor.getString(1)
//                                .toString() + ", 内容: " + cursor.getString(2)
//                        )

                            //循环读取所有短信
                            while (cursor.moveToNext()) {
                                val date: Long =
                                    cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE))
                                if (mReceivedMsgDate != date) {
                                    Log.d(
                                        TAG,
                                        "接收到短信: " + cursor.getString(1)
                                            .toString() + ", 内容: " + cursor.getString(2)
                                    )
                                    updateLog(
                                        "接收到短信: " + cursor.getString(1)
                                            .toString() + ", 内容: " + cursor.getString(2)
                                    )
                                    mReceivedMsgDate = date
                                } else {
                                    Log.d(TAG, "the same item, ignore it")
                                }
                            }
                        }
                        cursor.close()
                    }
                }
            }
        }

    private fun registerSmsListener() {
        getContentResolver().registerContentObserver(Uri.parse(SMS_URI), true, mSmsContentObserver)
    }

    private fun unRegisterSmsListener() {
        getContentResolver().unregisterContentObserver(mSmsContentObserver)
    }

    //----------------------sms--------------------------
    private fun updateLog(msg: String?) {
        mSb.append(msg).append("\n")
        //do something...
    }
}