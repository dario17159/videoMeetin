package com.dario.carrizo.app.videomeeting.listeners

import com.dario.carrizo.app.videomeeting.models.UserModel

/**
 * @author Dario Carrizo on 30/8/2020
 **/
interface UsersListener {
    fun initiateVideoMeeting(user: UserModel)
    fun initiateAudioMeeting(user: UserModel)
    fun onMultipleUsersAction(isMultipleUsersSelected: Boolean)
}