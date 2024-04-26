package com.hs.testussd.sim

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.telephony.TelephonyManager.UssdResponseCallback
import android.util.Log
import java.util.Date

class CallsReceiver : PhoneCallReceiver() {
    override fun onIncomingCallStarted(ctx: Context?, number: String?, start: Date?) {
        Log.d(this.javaClass.getName(), "mlog onIncomingCallStarted number = $number")
    }

    override fun onOutgoingCallStarted(ctx: Context?, number: String?, start: Date?) {
        Log.d(this.javaClass.getName(), "mlog onOutgoingCallStarted number = $number")
    }

    override fun onIncomingCallEnded(
        ctx: Context?,
        number: String?,
        start: Date?,
        end: Date?
    ) {
        Log.d(this.javaClass.getName(), "mlog onIncomingCallEnded number = $number")
    }

    override fun onOutgoingCallEnded(
        ctx: Context?,
        number: String?,
        start: Date?,
        end: Date?
    ) {
        Log.d(this.javaClass.getName(), "mlog onOutgoingCallEnded number = $number")
        runUssdCode(ctx)
    }

    override fun onMissedCall(ctx: Context?, number: String?, start: Date?) {
        Log.d(this.javaClass.getName(), "onMissedCall number = $number")
    }

    @SuppressLint("MissingPermission", "NewApi")
    fun runUssdCode(ctx: Context?) {
        val manager = ctx!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager



        if (manager.simState == TelephonyManager.SIM_STATE_READY) {
            // Get SubscriptionManager
            val subscriptionManager = SubscriptionManager.from(ctx)

            // Get a list of active SIM cards
            val activeSubscriptions = subscriptionManager.activeSubscriptionInfoList
            if (activeSubscriptions != null) {
                for (subscriptionInfo in activeSubscriptions) {
                    val operatorName = subscriptionInfo.carrierName.toString()

                    val simId = subscriptionInfo.carrierId
                    Log.d(
                        "mlog",
                        "SubscriptionId : ${subscriptionInfo.subscriptionId} Carrier $operatorName"
                    )
                    val ussdCode = getUssdCodeFromCarrierName(operatorName.lowercase())
//                    sendUssdRequest(
//                        telephonyManager = manager,
//                        simCardCarrierId = simId,
//                        ussdCode = ussdCode,
//                        operatorName = operatorName
//                    )


                    Log.d(
                        this.javaClass.getName(),
                        "mlog ussd calling $ussdCode  Operator $operatorName  Carrier $simId "
                    )

                    val manager1 = manager.createForSubscriptionId(simId)
                    manager1.sendUssdRequest(ussdCode, object : UssdResponseCallback() {
                        override fun onReceiveUssdResponse(
                            telephonyManager: TelephonyManager,
                            request: String,
                            response: CharSequence
                        ) {

                            super.onReceiveUssdResponse(telephonyManager, request, response)
                            Log.d(this.javaClass.getName(), "mlog My number:=$response")

                        }

                        override fun onReceiveUssdResponseFailed(
                            telephonyManager: TelephonyManager,
                            request: String,
                            failureCode: Int
                        ) {
                            super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode)
                            Log.d(this.javaClass.getName(), "mlog  error $failureCode")
                        }
                    }, Handler(Looper.getMainLooper()))

                }
            }
        }


    }


    @SuppressLint("MissingPermission", "NewApi")
    fun sendUssdRequest(
        telephonyManager: TelephonyManager,
        simCardCarrierId: Int,
        ussdCode: String,
        operatorName: String,
    ) {

        Log.d(
            this.javaClass.getName(),
            "mlog ussd calling $ussdCode  Operator $operatorName  Carrier $simCardCarrierId "
        )

            val manager = telephonyManager.createForSubscriptionId(simCardCarrierId)
            Log.d("mlog", "Manager: ${manager.simCarrierId}")
            manager.sendUssdRequest(ussdCode, object : UssdResponseCallback() {
                override fun onReceiveUssdResponse(
                    telephonyManager: TelephonyManager,
                    request: String,
                    response: CharSequence
                ) {

                    super.onReceiveUssdResponse(telephonyManager, request, response)
                    Log.d(this.javaClass.getName(), "mlog My number:=$response")

                }

                override fun onReceiveUssdResponseFailed(
                    telephonyManager: TelephonyManager,
                    request: String,
                    failureCode: Int
                ) {
                    super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode)
                    Log.d(this.javaClass.getName(), "mlog  error $failureCode")
                }
            }, Handler(Looper.getMainLooper()))

    }

    private fun getUssdCodeFromCarrierName(operatorName: String): String {
        when (operatorName) {
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

        }
        return ""
    }

}