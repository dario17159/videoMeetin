package com.dario.carrizo.app.videomeeting.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.dario.carrizo.app.videomeeting.R
import com.dario.carrizo.app.videomeeting.utils.Constants
import com.dario.carrizo.app.videomeeting.utils.hideKeyboard
import com.dario.carrizo.app.videomeeting.utils.preferences
import com.dario.carrizo.app.videomeeting.utils.toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        if(preferences.isSignedIn){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }


        textSingUp.setOnClickListener {
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
        }

        buttonSignIn.setOnClickListener {
            if(validateInputs()){
                signIn()
            }
        }
    }

    private fun signIn() {
        buttonSignIn.visibility = View.INVISIBLE
        signInProgressbar.visibility = View.VISIBLE
        hideKeyboard()

        FirebaseFirestore.getInstance()
            .collection(Constants.KEY_COLLECTION_USERS)
            .whereEqualTo(Constants.KEY_EMAIL, inputEmail.text.toString().trim())
            .whereEqualTo(Constants.KEY_PASSWORD, inputPassword.text.toString().trim())
            .get()
            .addOnCompleteListener {task ->
                if(task.isSuccessful && task.result != null && task.result!!.documents.size > 0){
                    val documentSnapshot = task.result!!.documents[0]
                    preferences.isSignedIn = true
                    preferences.userId = documentSnapshot.id
                    preferences.firstName = documentSnapshot.getString(Constants.KEY_FIRST_NAME)!!
                    preferences.lastName = documentSnapshot.getString(Constants.KEY_LAST_NAME)!!
                    preferences.email = documentSnapshot.getString(Constants.KEY_EMAIL)!!
                    val intent = Intent(applicationContext,MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }else {
                    signInProgressbar.visibility = View.INVISIBLE
                    buttonSignIn.visibility = View.VISIBLE
                    "Unable to sign in".toast(this)
                }
            }
    }

    private fun validateInputs(): Boolean {
        if(inputEmail.text.isEmpty()){
            "Enter email".toast(this)
            return false
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail.text.toString().trim()).matches()){
            "Enter a valid email".toast(this)
            return false
        }

        if(inputPassword.text.isEmpty()){
            "Enter password".toast(this)
            return false
        }

        return true
    }
}