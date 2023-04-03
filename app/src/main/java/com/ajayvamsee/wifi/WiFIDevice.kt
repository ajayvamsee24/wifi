package com.ajayvamsee.wifi

/**
 * Created by Ajay Vamsee on 11/3/2022.
 * Time : 15:33 HRS
 */
data class WiFIDevice(
    var ssid: String,
    var capabilities: String,
    var mac: String,
    var level: Int
)
