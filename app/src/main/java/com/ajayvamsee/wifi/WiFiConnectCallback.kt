package com.ajayvamsee.wifi

/**
 * Created by Ajay Vamsee on 11/3/2022.
 * Time : 15:36 HRS
 */
interface WiFiConnectCallback {

    fun onPasswordRequired(device:WiFIDevice)

    fun onDeviceConnected(device: WiFIDevice)

    fun onDeviceConfigured(device: WiFIDevice)

    fun onConfigurationFailed(device: WiFIDevice)
}