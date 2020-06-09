package com.tencent.iot.explorer.link.core.link.listener

import java.net.InetAddress

interface SmartConfigListener : IotLinkListener {

    fun connectedToWifi(
        isSuccess: Boolean,
        bssid: String,
        isCancel: Boolean,
        inetAddress: InetAddress
    )

    fun connectFailed()


}