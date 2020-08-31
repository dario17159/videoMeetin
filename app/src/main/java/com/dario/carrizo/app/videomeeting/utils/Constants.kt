package com.dario.carrizo.app.videomeeting.utils

/**
 * @author Dario Carrizo on 29/8/2020
 **/
class Constants {
    companion object {
        const val KEY_COLLECTION_USERS = "users"
        const val KEY_FIRST_NAME = "first_name"
        const val KEY_LAST_NAME = "last_name"
        const val KEY_EMAIL = "email"
        const val KEY_PASSWORD = "password"

        const val KEY_USER_ID = "user_id"
        const val KEY_FCM_TOKEN = "fcm_token"

        private const val REMOTE_MSG_AUTHORIZATION = "Authorization"
        private const val REMOTE_MSG_CONTENT_TYPE = "Content-Type"

        const val REMOTE_MSG_TYPE = "type"
        const val REMOTE_MSG_INVITATION = "invitation"
        const val REMOTE_MSG_MEETING_TYPE = "meetingType"
        const val REMOTE_MSG_INVITER_TOKEN = "inviterToken"
        const val REMOTE_MSG_DATA = "data"
        const val REMOTE_MSG_REGISTRATION_IDS = "registration_ids"

        const val REMOTE_MSG_INVITATION_RESPONSE = "invitationResponse"

        const val REMOTE_MSG_INVITATION_ACCEPTED = "accepted"
        const val REMOTE_MSG_INVITATION_REJECTED = "rejected"
        const val REMOTE_MSG_INVITATION_CANCELLED = "cancelled"

        const val REMOTE_MSG_MEETING_ROOM = "meetingRoom"

        fun getRemoteMessageHeaders(): HashMap<String, String> {
            val headers = hashMapOf<String, String>()
            headers[REMOTE_MSG_AUTHORIZATION] =
                "key=AAAAzEnuZ9g:APA91bE1-3oyjjU4RfJjISt_VBhBPursHpPXzBk5a1RRLnTem6Ux5gozS_IUtYakW7eSABWig72dZpREYf9PcUaFy8fI3uXucZ9EEaHFpSRuqxpQuHijqaV7-zb60eeQ6KAwIMpfB_0j"
            headers[REMOTE_MSG_CONTENT_TYPE] = "application/json"
            return headers
        }
    }
}