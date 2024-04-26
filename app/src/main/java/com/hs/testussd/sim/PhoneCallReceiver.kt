package com.hs.testussd.sim

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import java.util.Date

open class PhoneCallReceiver : BroadcastReceiver() {
    private var isInitialized = false
    override fun onReceive(context: Context, intent: Intent) {
        if (!isInitialized) {
            isInitialized = true
            val telephony =
                (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager)
            telephony.listen(object : PhoneStateListener() {
                @Deprecated("Deprecated in Java")
                override fun onCallStateChanged(state: Int, phoneNumber: String) {
                    onCustomCallStateChanged(context, state, phoneNumber)
                }
            }, PhoneStateListener.LISTEN_CALL_STATE)
        }
    }

    //Derived classes should override these to respond to specific events of interest
     open fun onIncomingCallStarted(ctx: Context?, number: String?, start: Date?) {
        Log.d("mlog", "incoming")
    }

    open fun onOutgoingCallStarted(ctx: Context?, number: String?, start: Date?) {
        Log.d("mlog", "incoming")
    }

    open fun onIncomingCallEnded(ctx: Context?, number: String?, start: Date?, end: Date?) {
        Log.d("mlog", "incoming")
    }

    open fun onOutgoingCallEnded(ctx: Context?, number: String?, start: Date?, end: Date?) {
        Log.d("mlog", "incoming")
    }

    open fun onMissedCall(ctx: Context?, number: String?, start: Date?) {
        Log.d("mlog", "incoming")
    }

    //Deals with actual events
    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    private fun onCustomCallStateChanged(context: Context, state: Int, number: String) {
        if (lastState == state) {
            //No change, debounce extras
            return
        }
        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                isIncoming = true
                callStartTime = Date()
                onIncomingCallStarted(context, number, callStartTime)
            }

            TelephonyManager.CALL_STATE_OFFHOOK ->                 //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false
                    callStartTime = Date()
                    onOutgoingCallStarted(context, number, callStartTime)
                }

            TelephonyManager.CALL_STATE_IDLE ->                 //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    onMissedCall(context, number, callStartTime)
                } else if (isIncoming) {
                    onIncomingCallEnded(context, number, callStartTime, Date())
                } else {
                    onOutgoingCallEnded(context, number, callStartTime, Date())
                }
        }
        lastState = state
    }

    companion object {
        //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations
        private var lastState = TelephonyManager.CALL_STATE_IDLE
        private var callStartTime: Date? = null
        private var isIncoming = false
    }
}