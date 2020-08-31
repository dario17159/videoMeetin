package com.dario.carrizo.app.videomeeting.firebase

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dario.carrizo.app.videomeeting.ui.activities.IncomingInvitationActivity
import com.dario.carrizo.app.videomeeting.utils.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * @author Dario Carrizo on 29/8/2020
 **/
class MessaginService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val type = remoteMessage.data.get(Constants.REMOTE_MSG_TYPE)

        if (type != null) {
            if (type == Constants.REMOTE_MSG_INVITATION) {
                val intent = Intent(applicationContext, IncomingInvitationActivity::class.java)
                intent.putExtra(
                    Constants.REMOTE_MSG_MEETING_TYPE,
                    remoteMessage.data[Constants.REMOTE_MSG_MEETING_TYPE]
                )
                intent.putExtra(
                    Constants.KEY_FIRST_NAME,
                    remoteMessage.data[Constants.KEY_FIRST_NAME]
                )
                intent.putExtra(
                    Constants.KEY_LAST_NAME,
                    remoteMessage.data[Constants.KEY_LAST_NAME]
                )
                intent.putExtra(
                    Constants.REMOTE_MSG_INVITER_TOKEN,
                    remoteMessage.data[Constants.REMOTE_MSG_INVITER_TOKEN]
                )
                intent.putExtra(
                    Constants.REMOTE_MSG_MEETING_ROOM,
                    remoteMessage.data[Constants.REMOTE_MSG_MEETING_ROOM]
                )
                intent.putExtra(Constants.KEY_EMAIL, remoteMessage.data[Constants.KEY_EMAIL])
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }else if(type == Constants.REMOTE_MSG_INVITATION_RESPONSE){
                val intent = Intent("${Constants.REMOTE_MSG_INVITATION_RESPONSE}")
                intent.putExtra(
                    Constants.REMOTE_MSG_INVITATION_RESPONSE,
                    remoteMessage.data[Constants.REMOTE_MSG_INVITATION_RESPONSE]
                )
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            }
        }

    }
}