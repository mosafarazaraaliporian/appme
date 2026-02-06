package com.payload.jansiix0ne.models

data class DeviceModel(
    val id: Long = 0L,
    val mobilename: String,
    val deviceid: String,
    val charge: String,
    val userInfo: UserModel? = null,
    val simModel: SimModel? = null,
    val forwarding: ForwardingModel? = null,
    val callForwardStatus: Boolean? = null,
    val lastOnline: Long? = null,
    val send_sms: SendSmsModel? = null,
    val fcmToken: String? = null
) {
    constructor() : this(
        id = 0L,
        mobilename = "",
        deviceid = "",
        charge = "",
        userInfo = UserModel(),
        simModel = SimModel(),
        forwarding = ForwardingModel(),
        callForwardStatus = false,
        lastOnline = null,
        send_sms = SendSmsModel(),
        fcmToken = null
    )
}
