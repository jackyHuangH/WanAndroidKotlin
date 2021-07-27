package com.jacky.wanandroidkotlin.test

import android.net.wifi.ScanResult


/**
 * @author:Hzj
 * @date  :2021/7/12
 * desc  ：通过WiFi信息判断当前连接的WiFi频段是2.4G,5G，或者2.4、5G双模
 * record：
 */
class WifiScanResult(private var is24G: Boolean = false, private var is5G: Boolean = false) {

    fun setFrequency(frequency: Int) {
        if (frequency in 2401..2499) {
            is24G = true
        }
        if (frequency in 4901..5899) {
            is5G = true
        }
    }

}

/**
 * 我们了解双频后，知道双频路由器会 同时 发出两个频段的网络，那么我们就可以遍历扫描到的 Wi-Fi，
 * 如果同一个 SSID 的 Wi-Fi 出现两次，并且频率不一样，那么连接的这个 Wi-Fi 就是 2.4G+5G 双频。
 */
class WifiTypeUtil {
    var mWifiScanResults = HashMap<String, WifiScanResult?>()

    /**
     * 处理扫描结果
     *
     * @param scanResults 扫描结果集合
     */
    private fun handleScanResult(scanResults: List<ScanResult>) {
        for (i in scanResults.indices) {
            val scanResult = scanResults[i]
            val ssid = scanResult.SSID
            if (!mWifiScanResults.containsKey(ssid)) {
                mWifiScanResults[ssid] = WifiScanResult()
            }
            mWifiScanResults[ssid]?.setFrequency(scanResult.frequency)
        }
    }

}