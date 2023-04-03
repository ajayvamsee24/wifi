package com.ajayvamsee.wifi

/**
 * Created by Ajay Vamsee on 11/3/2022.
 * Time : 15:30 HRS
 */
public interface WiFiManagerCallback {

    fun onScanResults(devices: ArrayList<WiFIDevice>)

    fun signalStrength(signal:Int)
}