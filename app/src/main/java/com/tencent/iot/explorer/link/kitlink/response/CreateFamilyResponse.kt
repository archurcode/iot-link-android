package com.tencent.iot.explorer.link.kitlink.response

/**
 * 新增家庭响应实体
 */
class CreateFamilyResponse {

    var Data = FamilyID()

    inner class FamilyID {
        var FamilyId = ""
    }
}