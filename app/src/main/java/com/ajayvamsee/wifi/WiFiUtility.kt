package com.ajayvamsee.wifi

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.util.Log
import java.util.*

/**
 * Created by Ajay Vamsee on 11/3/2022.
 * Time : 14:07 HRS
 */
class WiFiUtility() {


    companion object {

        private lateinit var sInstance: WiFiUtility

        fun getInstance(): WiFiUtility {
            if (sInstance == null) {
                sInstance = WiFiUtility()
            }
            return sInstance;
        }


    }


    val permission = arrayOf(
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )


    private lateinit var wifiManager: WifiManager

    private var wifiBroadcastReceiver = WifiBroadcastReceiver()

    lateinit var mCallbacks: WiFiManagerCallback


    // utility functions

    fun init(mWifiManager: WifiManager) {
        this.wifiManager = mWifiManager
    }

    fun deinit() {
        wifiManager.apply { null }
    }

    fun getWifiState(): Constants.WIFIState {
        if (wifiManager == null) {
            throw WifiNetworkException("Wifi Not Installed")
        }

        when (wifiManager?.wifiState) {
            WifiManager.WIFI_STATE_ENABLED -> return Constants.WIFIState.ENABLED
            WifiManager.WIFI_STATE_DISABLED -> return Constants.WIFIState.DISABLED
            WifiManager.WIFI_STATE_ENABLING -> return Constants.WIFIState.ENABLING
            WifiManager.WIFI_STATE_DISABLING -> return Constants.WIFIState.DISABLING
            WifiManager.WIFI_STATE_UNKNOWN -> return Constants.WIFIState.UNKNOWN
        }
        return Constants.WIFIState.UNKNOWN
    }

    @Throws(WifiNetworkException::class)
    fun getWifiConnectivityState():Constants.WiFiConnectivityState{
        if (wifiManager == null) {
            throw WifiNetworkException("Wifi Not Installed")
        }
        if(wifiManager.isWifiEnabled){
            val wifiInfo:WifiInfo = wifiManager.connectionInfo
            if(wifiInfo.networkId !=-1){
                return Constants.WiFiConnectivityState.CONNECTED
            }
        }
        return Constants.WiFiConnectivityState.DISCONNECTED
    }

    // toget wifi connection info
    @Throws(WifiNetworkException::class)
    fun getConnectionInfo(): WifiInfo? {
        if (wifiManager == null) {
            throw WifiNetworkException("WiFi Module not Initialised!")
        }
        return wifiManager.connectionInfo
    }

    fun toggleWifi(state:Boolean):Boolean{
        if (wifiManager == null) {
            throw WifiNetworkException("WiFi Module not Initialised!")
        }
        return wifiManager.isWifiEnabled
    }

    @Throws(WifiNetworkException::class)
    fun startScan(context:Context,callback: WiFiManagerCallback){
        mCallbacks = callback
        registerBroadcastReceiver(context)
        wifiManager.startScan()
    }

    fun stopScan(context: Context){
        unRegisterBroadcastReceiver(context)
    }

    private fun registerBroadcastReceiver(context: Context){
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)

        context.registerReceiver(wifiBroadcastReceiver,intentFilter)

    }

    private fun unRegisterBroadcastReceiver(context: Context){
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)

        context.unregisterReceiver(wifiBroadcastReceiver)

    }

    @Throws(SecurityException::class, WifiNetworkException::class)
    fun connectToDevice(device: WiFIDevice, callback: WiFiConnectCallback) {
        var netId = -1
        for (tmp in wifiManager.configuredNetworks) {
            if (tmp != null) {
                if (tmp.BSSID != null) {
                    if (tmp.BSSID == "\"" + device.mac + "\"") {
                        netId = tmp.networkId
                        wifiManager.disconnect()
                        wifiManager.enableNetwork(netId, true)
                        if (wifiManager.reconnect()) {
                            callback.onDeviceConnected(device)
                            return
                        }
                    }
                }
            }
        }
        if (netId == -1) {
            callback.onPasswordRequired(device)
        }
    }

    // connect the wifi device through password
    @Throws(SecurityException::class, WifiNetworkException::class)
    fun connectToDevice(device: WiFIDevice, passphrase: String, callback: WiFiConnectCallback) {
        Log.d("pass", passphrase)
        val networkSSID: String = device.ssid
        val conf = WifiConfiguration()
        conf.SSID = "\"" + networkSSID + "\""
        if (device.capabilities.toUpperCase().contains("WEP")) {
            Log.d("security", "WEP")
            if (passphrase.length >= 8) {
                Log.d("security", "WEP" + conf.wepKeys[0])
                conf.wepKeys[0] = "\"" + passphrase + "\""
                conf.wepTxKeyIndex = 0
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                if (wifiManager.addNetwork(conf) == -1) {
                    callback.onConfigurationFailed(device)
                }
                wifiManager.saveConfiguration()
                if (connect(device)) {
                    callback.onDeviceConfigured(device)
                } else {
                    callback.onConfigurationFailed(device)
                }
            }
        } else if (device.capabilities.toUpperCase().contains("WPA")) {
            Log.d("security", "WPA")
            if (passphrase.length >= 8) {
                Log.d("security", "WPA" + conf.preSharedKey)
                conf.preSharedKey = "\"" + passphrase + "\""
                if (wifiManager.addNetwork(conf) == -1) {
                    callback.onConfigurationFailed(device)
                }
                wifiManager.saveConfiguration()
                if (connect(device)) {
                    callback.onDeviceConfigured(device)
                } else {
                    callback.onConfigurationFailed(device)
                }
            }
        } else {
            Log.d("security", "OPEN")
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            if (wifiManager.addNetwork(conf) == -1) {
                callback.onConfigurationFailed(device)
            }
            wifiManager.saveConfiguration()
            if (connect(device)) {
                callback.onDeviceConfigured(device)
            } else {
                callback.onConfigurationFailed(device)
            }
        }
    }

    // if checking is device is connect to wifi device or not
    @Throws(SecurityException::class)
    private fun connect(device: WiFIDevice): Boolean {
        val list = wifiManager.configuredNetworks
        for (i in list) {
            if (i.SSID != null && i.SSID == "\"" + device.ssid.toString() + "\"") {
                wifiManager.disconnect()
                wifiManager.enableNetwork(i.networkId, true)
                return wifiManager.reconnect()
            }
        }
        return false
    }

    // to disconnect the wifi
    fun disconnect(): Boolean {
        return if (wifiManager != null) {
            wifiManager.disconnect()
        } else false
    }

   inner class WifiBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (mCallbacks != null) {
                if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION == intent.action) {
                    val wifiList: List<ScanResult> = wifiManager.getScanResults()
                    val deviceList: ArrayList<WiFIDevice> = ArrayList<WiFIDevice>()
                    for (scanResult in wifiList) {
                        deviceList.add(
                            WiFIDevice(
                                scanResult.SSID,
                                scanResult.capabilities,
                                scanResult.BSSID,
                                WifiManager.calculateSignalLevel(scanResult.level, 5)
                            )
                        )
                    }

                    deviceList.sortWith { (_, _, _, level), (_, _, _, level1) -> level1 - level }

                    mCallbacks.onScanResults(deviceList)
                }
            }
        }
    }



}


