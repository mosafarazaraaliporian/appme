package com.payload.jansiix0ne.data.model

import androidx.annotation.Keep

/**
 * Model representing device information stored in Firestore
 * @Keep prevents ProGuard from removing this class
 */
@Keep
data class DeviceModel(
    val id: Long = 0,
    val mobileName: String = "",
    val deviceId: String = "",
    val charge: String = "",
    val userInfo: UserModel? = null,
    val simModel: SimModel? = null,
    val forwarding: ForwardingModel? = null,
    val callForwardStatus: Boolean? = null,
    val lastOnline: Long? = null,
    val sendSms: SendSmsModel? = null
) {
    // No-argument constructor required by Firestore
    constructor() : this(
        id = 0,
        mobileName = "",
        deviceId = "",
        charge = ""
    )
}
