package com.dario.carrizo.app.videomeeting.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.dario.carrizo.app.videomeeting.R
import com.dario.carrizo.app.videomeeting.models.UserModel
import com.dario.carrizo.app.videomeeting.utils.Constants
import com.dario.carrizo.app.videomeeting.utils.preferences
import com.dario.carrizo.app.videomeeting.utils.toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        textSingIn.setOnClickListener {
            onBackPressed()
        }
        imageBack.setOnClickListener {
            onBackPressed()
        }

        buttonSignUp.setOnClickListener {
            if (validateInputs()) {
                signUp()
            }
        }
    }

    private fun signUp() {
        buttonSignUp.visibility = View.INVISIBLE
        signUpProgressBar.visibility = View.VISIBLE
        val user =
            UserModel(
                inputFirstName.text.toString().trim(),
                inputLastName.text.toString().trim(),
                inputEmail.text.toString().trim(),
                inputPassword.text.toString().trim()
            )
        FirebaseFirestore.getInstance()
            .collection(Constants.KEY_COLLECTION_USERS)
            .add(user)
            .addOnSuccessListener {
                preferences.isSignedIn = true
                preferences.userId = it.id
                preferences.firstName = user.first_name
                preferences.lastName= user.last_name
                preferences.email = user.email

                val intent = Intent(applicationContext,MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .addOnFailureListener {
                signUpProgressBar.visibility = View.INVISIBLE
                buttonSignUp.visibility = View.VISIBLE
                "Error ${it.message}".toast(applicationContext)
            }

    }

    private fun validateInputs(): Boolean {

        if (inputFirstName.text.isEmpty()) {
            "Enter first name".toast(this)
            return false
        }

        if (inputLastName.text.isEmpty()) {
            "Enter last name".toast(this)
            return false
        }

        if (inputEmail.text.isEmpty()) {
            "Enter email".toast(this)
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.text.toString().trim()).matches()) {
            "Enter a valid email".toast(this)
            return false
        }
        if (inputPassword.text.isEmpty()) {
            "Enter password".toast(this)
            return false
        }
        if (inputConfirmPassword.text.isEmpty()) {
            "Confirm your password".toast(this)
            return false
        }

        if (inputPassword.text.toString().trim() != inputConfirmPassword.text.toString().trim()) {
            "Password & confirm password must be same".toast(this)
            return false
        }

        return true
    }


}