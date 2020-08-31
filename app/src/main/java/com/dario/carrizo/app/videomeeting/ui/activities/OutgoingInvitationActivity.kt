package com.dario.carrizo.app.videomeeting.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dario.carrizo.app.videomeeting.R
import com.dario.carrizo.app.videomeeting.models.UserModel
import com.dario.carrizo.app.videomeeting.network.ApiClient
import com.dario.carrizo.app.videomeeting.network.ApiService
import com.dario.carrizo.app.videomeeting.utils.Constants
import com.dario.carrizo.app.videomeeting.utils.preferences
import com.dario.carrizo.app.videomeeting.utils.toast
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_incoming_invitation.imageMeetingType
import kotlinx.android.synthetic.main.activity_incoming_invitation.textFirstChar
import kotlinx.android.synthetic.main.activity_incoming_invitation.textUserEmail
import kotlinx.android.synthetic.main.activity_incoming_invitation.textUserName
import kotlinx.android.synthetic.main.activity_outgoing_invitation.*
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL
import java.util.*
import kotlin.Exception

class OutgoingInvitationActivity : AppCompatActivity() {

    private lateinit var user: UserModel

    private var inviterToken: String? = null
    private var meetingType: String? = null
    private var meetingRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_outgoing_invitation)

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                inviterToken = task.result!!.token
                initiateMeeting(meetingType!!, user.fcm_token)
            }
        }


        intent?.extras?.let { bundle ->
            user = bundle.getParcelable<UserModel>("user")!!
            meetingType = bundle.getString("type")
            meetingType?.let {
                if (it == "video") {
                    imageMeetingType.setImageResource(R.drawable.ic_vide)
                }else {
                    imageMeetingType.setImageResource(R.drawable.ic_audio)
                }
            }

            textFirstChar.text = user.first_name.substring(0, 1)
            textUserName.text = String.format("%s %s", user.first_name, user.last_name)
            textUserEmail.text = user.email
        }


        imageStopInvitation.setOnClickListener {
            cancelInvitation(user.fcm_token)
        }

    }

    private fun initiateMeeting(meetingType: String, receiverToken: String) {
        try {
            val tokens = JSONArray()
            tokens.put(receiverToken)

            val body = JSONObject()
            val data = JSONObject()

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION)
            data.put(Constants.REMOTE_MSG_MEETING_TYPE, meetingType)
            data.put(Constants.KEY_FIRST_NAME, preferences.firstName)
            data.put(Constants.KEY_LAST_NAME, preferences.lastName)
            data.put(Constants.KEY_EMAIL, preferences.email)
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken)

            meetingRoom = preferences.userId+"_"+ UUID.randomUUID().toString().substring(0,5)

            data.put(Constants.REMOTE_MSG_MEETING_ROOM, meetingRoom)

            body.put(Constants.REMOTE_MSG_DATA, data)
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens)

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION)
        } catch (e: Exception) {
            e.message!!.toast(this)
            finish()
        }
    }

    private fun sendRemoteMessage(remoteMessageBody: String, type: String) {
        ApiClient.getclient().create(ApiService::class.java).sendRemoteMessage(
            Constants.getRemoteMessageHeaders(), remoteMessageBody
        ).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    if (type == Constants.REMOTE_MSG_INVITATION) {
                        //"Invitation sent successfully".toast(applicationContext)
                    } else if (type == Constants.REMOTE_MSG_INVITATION_RESPONSE) {
                        "Invitation cancelled".toast(applicationContext)
                        finish()
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

    private fun cancelInvitation(receiverToken: String) {
        try {
            val tokens = JSONArray()
            tokens.put(receiverToken)

            val body = JSONObject()
            val data = JSONObject()

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE)
            data.put(
                Constants.REMOTE_MSG_INVITATION_RESPONSE,
                Constants.REMOTE_MSG_INVITATION_CANCELLED
            )

            body.put(Constants.REMOTE_MSG_DATA, data)
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens)

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION_RESPONSE)

        } catch (e: Exception) {
            e.message!!.toast(applicationContext)
            finish()
        }
    }

    private val invitationResponseReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val type = intent!!.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE)
            if(type != null){
                if(type == Constants.REMOTE_MSG_INVITATION_ACCEPTED){
//                    "Invitation Accepted".toast(applicationContext)
                    try{
                        val serverURL = URL("https://meet.jit.si")

                        val builder = JitsiMeetConferenceOptions.Builder()
                        builder.setServerURL(serverURL)
                        builder.setWelcomePageEnabled(false)
                        builder.setRoom(meetingRoom)
                        if(meetingType == "audio"){
                            builder.setVideoMuted(true)
                        }
                        JitsiMeetActivity.launch(this@OutgoingInvitationActivity,builder.build())
                        finish()
                    }catch (e: Exception){
                        e.message!!.toast(applicationContext)
                        finish()
                    }
                }else if(type == Constants.REMOTE_MSG_INVITATION_REJECTED){
                    "Invitation Rejected".toast(applicationContext)
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
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(invitationResponseReceiver)
    }
}