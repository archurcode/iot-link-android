package com.tencent.iot.explorer.link.core.link

import com.tencent.iot.explorer.link.core.link.service.DeviceService

/**
 * 配网
 */
class IoTLink private constructor() {

    private var service: DeviceService? = null

    companion object {
        val instance: IoTLink by lazy { IoTLink() }
    }

    fun start(service: DeviceService) {
        service.start()
    }

    fun stop() {
        service?.stop()
        service = null
    }

}