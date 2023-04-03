package com.ajayvamsee.wifi

/**
 * Created by Ajay Vamsee on 11/3/2022.
 * Time : 16:01 HRS
 */
class Constants {

    public enum class WIFIState{
        ENABLED,
        ENABLING,
        DISABLED,
        DISABLING,
        UNKNOWN
    }

    enum class WiFiConnectivityState {
        CONNECTED,
        DISCONNECTED
    }
}