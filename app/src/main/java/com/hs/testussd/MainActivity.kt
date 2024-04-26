@file:Suppress("UNCHECKED_CAST")

package com.hs.testussd

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.hs.testussd.sim.CallBySlot
import com.hs.testussd.sim.Carrier
import com.hs.testussd.sim.Operator

class MainActivity : AppCompatActivity() {
    private val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
    private lateinit var rootTm: TelephonyManager
    private var carrier1: Carrier? = null
    private var carrier2: Carrier? = null
    private var carrier3: Carrier? = null

    private lateinit var etUssd: TextInputEditText
    private lateinit var etPhoneNumber: TextInputEditText
    private lateinit var tvSimCards: AppCompatTextView
    private lateinit var tvUssdResponse: AppCompatTextView
    private lateinit var tvUssdError: AppCompatTextView
    private var simCardsRes: String = ""

    @SuppressLint("MissingInflatedId", "NewApi", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvSimCards = findViewById(R.id.tv_sim_cards)
        tvUssdResponse = findViewById(R.id.tv_response_ussd)
        tvUssdError = findViewById(R.id.tv_response_error)
        etUssd = findViewById(R.id.et_ussd)

        rootTm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        checkAndRequestPermissions()
        val callPhone = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
        if (callPhone == PackageManager.PERMISSION_GRANTED) {
            getSimCardCarrierInfo(this)
        } else {
            Log.d("mlog", "Already: granted")
        }

        findViewById<Button>(R.id.btn_all_sim_info).setOnClickListener {

            if (carrier1 != null) {
                sendUssdRequestRecursively(
                    sendTm = rootTm, currentCarrier = carrier1!!, nextCarrier = carrier2
                )
            } else {
                Toast.makeText(this, "Please get", Toast.LENGTH_SHORT).show()
                Log.d("mlog", "Carrier 1 is null")
            }

        }
        findViewById<Button>(R.id.btn_ussd_sim1).setOnClickListener {
            val ussd = etUssd.text.toString()
            if (carrier1 != null) {
                if (ussd.isNotEmpty()) {
                    sendUssdRequestByCarrier(
                        sendTm = rootTm, currentCarrier = carrier1!!, ussd = ussd
                    )
                } else {
                    Toast.makeText(this, "Ussd is empty", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "SIM Card slot 1 is empty", Toast.LENGTH_SHORT).show()
            }
        }

        // send ussd into Sim 2
        findViewById<Button>(R.id.btn_ussd_sim2).setOnClickListener {
            val ussd = etUssd.text.toString()
            if (carrier2 != null) {
                if (ussd.isNotEmpty()) {
                    sendUssdRequestByCarrier(
                        sendTm = rootTm, currentCarrier = carrier2!!, ussd = ussd
                    )
                } else {
                    Toast.makeText(this, "SIM Card slot 2 is empty", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "SIM Card slot 2 is empty", Toast.LENGTH_SHORT).show()
            }
        }

//make call
        findViewById<Button>(R.id.btnCall).setOnClickListener {
            CallBySlot(this).call("*102%23", 0)
        }

    }


    @SuppressLint("MissingPermission", "NewApi")
    fun getSimCardCarrierInfo(ctx: Context?) {


        if (rootTm.simState == TelephonyManager.SIM_STATE_READY) {
            // Get SubscriptionManager
            val subscriptionManager = SubscriptionManager.from(ctx)

            // Get a list of active SIM cards
            val activeSubscriptions = subscriptionManager.activeSubscriptionInfoList
            if (activeSubscriptions != null) {


                if (activeSubscriptions.size == 3) {
                    carrier1 = Carrier(
                        id = 1,
                        operator = activeSubscriptions[0].carrierName.toString(),
                        carrierId = activeSubscriptions[0].carrierId,
                        ussd = getUssdCodeFromCarrierName(
                            activeSubscriptions[0].carrierName.toString().lowercase()
                        ),
                        subscriptionId = activeSubscriptions[0].subscriptionId
                    )
                    carrier2 = Carrier(
                        id = 2,
                        operator = activeSubscriptions[1].carrierName.toString(),
                        carrierId = activeSubscriptions[1].carrierId,
                        ussd = getUssdCodeFromCarrierName(
                            activeSubscriptions[1].carrierName.toString().lowercase()
                        ),
                        subscriptionId = activeSubscriptions[1].subscriptionId
                    )
                    carrier3 = Carrier(
                        id = 3,
                        operator = activeSubscriptions[2].carrierName.toString(),
                        carrierId = activeSubscriptions[2].carrierId,
                        ussd = getUssdCodeFromCarrierName(
                            activeSubscriptions[2].carrierName.toString().lowercase()
                        ),
                        subscriptionId = activeSubscriptions[2].subscriptionId
                    )
                }

                if (activeSubscriptions.size == 2) {
                    carrier1 = Carrier(
                        id = 1,
                        operator = activeSubscriptions[0].carrierName.toString(),
                        carrierId = activeSubscriptions[0].carrierId,
                        ussd = getUssdCodeFromCarrierName(
                            activeSubscriptions[0].carrierName.toString().lowercase()
                        ),
                        subscriptionId = activeSubscriptions[0].subscriptionId
                    )
                    carrier2 = Carrier(
                        id = 2,
                        operator = activeSubscriptions[1].carrierName.toString(),
                        carrierId = activeSubscriptions[1].carrierId,
                        ussd = getUssdCodeFromCarrierName(
                            activeSubscriptions[1].carrierName.toString().lowercase()
                        ),
                        subscriptionId = activeSubscriptions[1].subscriptionId
                    )
                }

                if (activeSubscriptions.size == 1) {
                    carrier1 = Carrier(
                        id = 1,
                        operator = activeSubscriptions[0].carrierName.toString(),
                        carrierId = activeSubscriptions[0].carrierId,
                        ussd = getUssdCodeFromCarrierName(
                            activeSubscriptions[0].carrierName.toString().lowercase()
                        ),
                        subscriptionId = activeSubscriptions[0].subscriptionId
                    )
                }

            }
        }

    }


    @SuppressLint("MissingPermission", "NewApi")
    fun sendUssdRequestByCarrier(
        sendTm: TelephonyManager,
        currentCarrier: Carrier,
        ussd: String,
    ) {


        val manager = sendTm.createForSubscriptionId(currentCarrier.subscriptionId)

        Log.d("mlog", "Ussd: $ussd")
        manager.sendUssdRequest(
            ussd, object : TelephonyManager.UssdResponseCallback() {
                @SuppressLint("HardwareIds")
                override fun onReceiveUssdResponse(
                    telephonyManager: TelephonyManager, request: String, response: CharSequence
                ) {

                    super.onReceiveUssdResponse(telephonyManager, request, response)
                    tvUssdResponse.text = response

                }

                @SuppressLint("HardwareIds")
                override fun onReceiveUssdResponseFailed(
                    telephonyManager: TelephonyManager, request: String, failureCode: Int
                ) {
                    super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode)
                    Log.d("mlog", "Error code: request $request  failureCode $failureCode")
                    Toast.makeText(
                        this@MainActivity, "USSD CODE FAILED $failureCode", Toast.LENGTH_SHORT
                    ).show()
                }
            }, Handler(Looper.getMainLooper())
        )

    }


    @SuppressLint("MissingPermission", "NewApi", "SetTextI18n")
    fun sendUssdRequestRecursively(
        sendTm: TelephonyManager, currentCarrier: Carrier, nextCarrier: Carrier? = null
    ) {


        val manager = sendTm.createForSubscriptionId(currentCarrier.subscriptionId)

        Log.d(
            this.javaClass.getName(),
            "mlog ussd calling ${currentCarrier.ussd} Operator ${currentCarrier.operator}  Carrier:= ${currentCarrier.carrierId} Manager: ${manager.simCarrierId}\n --------------------------------------- "
        )

        if (currentCarrier.ussd.contains("[0-9]".toRegex())) {
            manager.sendUssdRequest(
                currentCarrier.ussd, object : TelephonyManager.UssdResponseCallback() {
                    @SuppressLint("HardwareIds")
                    override fun onReceiveUssdResponse(
                        telephonyManager: TelephonyManager, request: String, response: CharSequence
                    ) {

                        super.onReceiveUssdResponse(telephonyManager, request, response)
                        Log.d("mlog", " My number:=$response   req:=$request")
                        simCardsRes =
                            "$simCardsRes Carrier_${currentCarrier.id} Operator:=${currentCarrier.operator} \n Number:=$response\n********************************"
                        tvSimCards.text = simCardsRes
                        if (nextCarrier != null) {
                            sendUssdRequestRecursively(
                                sendTm = rootTm,
                                currentCarrier = nextCarrier,
                                nextCarrier = carrier3
                            )
                        } else {
                            Log.d("mlog", "Next carrier is null")
                            simCardsRes =
                                "$simCardsRes Last Operator is null \n********************************"
                        }
                    }

                    @SuppressLint("SetTextI18n", "HardwareIds")
                    override fun onReceiveUssdResponseFailed(
                        telephonyManager: TelephonyManager, request: String, failureCode: Int
                    ) {
                        super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode)
                        Log.d(this.javaClass.getName(), "mlog  error $failureCode")
                        tvUssdError.text =
                            "${currentCarrier.operator} ${currentCarrier.ussd}   Error $failureCode"
                    }
                }, Handler(Looper.getMainLooper())
            )
        } else {
            tvUssdError.text = "Carrier_${currentCarrier.id} Operator:=${currentCarrier.operator}"
        }


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

            Operator.UzMobile.operatorName.lowercase() -> {
                return "*100*4#"
            }

            Operator.Uztelecom.operatorName.lowercase() -> {
                return "*100*4#"
            }

            else -> {
                return "No service"
            }
        }
    }



    @SuppressLint("SuspiciousIndentation")
    private fun checkAndRequestPermissions(): Boolean {

        val listPermissionsNeeded: MutableList<String> = ArrayList<String>()
        listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE)
        listPermissionsNeeded.add(Manifest.permission.READ_CALL_LOG)
        listPermissionsNeeded.add(Manifest.permission.CALL_PHONE)

        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                (listPermissionsNeeded.toTypedArray() as Array<String?>),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }
}