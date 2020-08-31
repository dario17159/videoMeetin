package com.dario.carrizo.app.videomeeting.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.dario.carrizo.app.videomeeting.R
import com.dario.carrizo.app.videomeeting.listeners.UsersListener
import com.dario.carrizo.app.videomeeting.models.UserModel
import com.dario.carrizo.app.videomeeting.ui.adapters.UserAdapter
import com.dario.carrizo.app.videomeeting.utils.Constants
import com.dario.carrizo.app.videomeeting.utils.preferences
import com.dario.carrizo.app.videomeeting.utils.toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),UsersListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textTitle.text = String.format("%s %s", preferences.firstName, preferences.lastName)

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                sendFCMTokenToDatabase(task.result!!.token)
            }
        }

        textSignOut.setOnClickListener {
            signOut()
        }

        setUprecyclerView()
        getUsers()
    }

    private fun getUsers() {
        usersProgressbar.visibility = View.VISIBLE
        FirebaseFirestore.getInstance()
            .collection(Constants.KEY_COLLECTION_USERS)
            .get()
            .addOnCompleteListener { task ->
                usersProgressbar.visibility = View.GONE
                if (task.isSuccessful && task.result != null) {
                    val userList = mutableListOf<UserModel>()
                    task.result!!.forEach { document ->
                        if (preferences.userId != document.id) {
                            val user = document.toObject(UserModel::class.java)
                            userList.add(user)
                        }
                    }
                    if (userList.size != 0) {
                        userRecyclerView.adapter = UserAdapter(userList,this)
                    } else {
                        textErrorMessage.text = String.format("%s", "No users available")
                        textErrorMessage.visibility = View.VISIBLE
                    }
                } else {
                    textErrorMessage.text = String.format("%s", "No users available")
                    textErrorMessage.visibility = View.VISIBLE
                }
            }
    }

    private fun setUprecyclerView() {
        userRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun sendFCMTokenToDatabase(token: String) {
        FirebaseFirestore.getInstance()
            .collection(Constants.KEY_COLLECTION_USERS)
            .document(preferences.userId)
            .update(Constants.KEY_FCM_TOKEN, token)
            .addOnSuccessListener {
//                "Token update successfully".toast(this)
            }
            .addOnFailureListener {
                "Unable to send token ${it.message}".toast(this)
            }
    }

    private fun signOut() {
        "Signed out...".toast(applicationContext)

        val updateMap = hashMapOf<String, Any>()
        updateMap[Constants.KEY_FCM_TOKEN] = FieldValue.delete()

        FirebaseFirestore.getInstance()
            .collection(Constants.KEY_COLLECTION_USERS)
            .document(preferences.userId)
            .update(updateMap)
            .addOnSuccessListener {
                preferences.clearPreferencees()
                startActivity(Intent(applicationContext, SignInActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                "Unable to sign out".toast(applicationContext)
            }
    }

    override fun initiateVideoMeeting(user: UserModel) {
        if(user.fcm_token.isEmpty()){
            String.format("%s %s is not available for meeting",user.first_name,user.last_name).toast(this)
        }else {
            val intent = Intent(applicationContext,OutgoingInvitationActivity::class.java)
            intent.putExtra("user",user)
            intent.putExtra("type","video")
            startActivity(intent)
        }
    }

    override fun initiateAudioMeeting(user: UserModel) {
        if(user.fcm_token.isEmpty()){
            String.format("%s %s is not available for meeting",user.first_name,user.last_name).toast(this)
        }else {
           val intent = Intent(applicationContext, OutgoingInvitationActivity::class.java)
            intent.putExtra("user",user)
            intent.putExtra("type","audio")
            startActivity(intent)
        }
    }
}