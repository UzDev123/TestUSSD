package com.hs.testussd

import com.hs.testussd.sim.Operator

fun String.getUssdCodeFromCarrierName(): String {
    when (this) {
        Operator.BeelineUz.operatorName.lowercase() -> {
            return "*148#"
        }

        Operator.HumansUz.operatorName.lowercase() -> {
            return "*664579#"
        }

        Operator.MobiUz.operatorName.lowercase() -> {
            return "*150#"
        }

        Operator.Ucell.operatorName.lowercase() -> {
            return "*450#"
        }

        Operator.UzMobile.operatorName.lowercase() -> {
            return "*100*4#"
        }

        Operator.Uztelecom.operatorName.lowercase() -> {
            return "*100*4#"
        }

        Operator.PerfectumMobile.operatorName.lowercase() -> {
            return ""
        }



    }
    return ""
}