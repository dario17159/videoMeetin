package com.dario.carrizo.app.videomeeting.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dario.carrizo.app.videomeeting.R
import com.dario.carrizo.app.videomeeting.network.ApiClient
import com.dario.carrizo.app.videomeeting.network.ApiService
import com.dario.carrizo.app.videomeeting.utils.Constants
import com.dario.carrizo.app.videomeeting.utils.toast
import kotlinx.android.synthetic.main.activity_incoming_invitation.*
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL

class IncomingInvitationActivity : AppCompatActivity() {

    private var remoteInviterToken: String? = null
    private var meetingType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_invitation)
        intent?.extras?.let { bundle ->
            meetingType = bundle.getString(Constants.REMOTE_MSG_MEETING_TYPE)
            meetingType?.let {
                if (it == "video") {
                    imageMeetingType.setImageResource(R.drawable.ic_vide)
                } else {
                    imageMeetingType.setImageResource(R.drawable.ic_audio)
                }
            }

            val firstName = bundle.getString(Constants.KEY_FIRST_NAME)
            val lastName = bundle.getString(Constants.KEY_LAST_NAME)
            val email = bundle.getString(Constants.KEY_EMAIL)
            remoteInviterToken = bundle.getString(Constants.REMOTE_MSG_INVITER_TOKEN)
            textFirstChar.text = firstName!!.substring(0, 1)
            textUserName.text = String.format("%s %s", firstName, lastName)
            textUserEmail.text = email
        }

        if (remoteInviterToken != null) {
            imageAcceptInvitation.setOnClickListener {
                sendInvitationResponse(
                    Constants.REMOTE_MSG_INVITATION_ACCEPTED,
                    remoteInviterToken!!
                )
            }

            imageRejectInvitation.setOnClickListener {
                sendInvitationResponse(
                    Constants.REMOTE_MSG_INVITATION_REJECTED,
                    remoteInviterToken!!
                )
            }
        }
    }

    private fun sendInvitationResponse(type: String, receiverToken: String) {
        try {
            val tokens = JSONArray()
            tokens.put(receiverToken)

            val body = JSONObject()
            val data = JSONObject()

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE)
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, type)

            body.put(Constants.REMOTE_MSG_DATA, data)
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens)

            sendRemoteMessage(body.toString(), type)

        } catch (e: Exception) {
            e.message!!.toast(applicationContext)
            finish()
        }
    }

    private fun sendRemoteMessage(remoteMessageBody: String, type: String) {
        ApiClient.getclient().create(ApiService::class.java).sendRemoteMessage(
            Constants.getRemoteMessageHeaders(), remoteMessageBody
        ).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    if (type == Constants.REMOTE_MSG_INVITATION_ACCEPTED) {
//                        "Invitation accepted".toast(applicationContext)
                        try {
                            val serverUrl = URL("https://meet.jit.si")
                            val builder = JitsiMeetConferenceOptions.Builder()
                            builder.setServerURL(serverUrl)
                            builder.setWelcomePageEnabled(false)
                            builder.setRoom(intent.getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM))

                            if (meetingType == "audio") {
                                builder.setVideoMuted(true)
                            }

                            JitsiMeetActivity.launch(
                                this@IncomingInvitationActivity,
                                builder.build()
                            )
                            finish()
                        } catch (e: Exception) {
                            e.message!!.toast(applicationContext)
                            finish()
                        }
                    } else {
                        "Invitation rejected".toast(applicationContext)
                    }
                } else {
                    response.message().toast(applicationContext)
                    finish()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                t.message!!.toast(applicationContext)
                finish()
            }
        })
    }

    private val invitationResponseReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val type = intent!!.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE)
            if (type != null) {
                if (type == Constants.REMOTE_MSG_INVITATION_CANCELLED) {
                    "Invitation Canceled".toast(applicationContext)
                    finish()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(
            invitationResponseReceiver,
            IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(applicationContext)
            .unregisterReceiver(invitationResponseReceiver)
    }
}