package com.example.project_start

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        auth = Firebase.auth

        val signupButton = findViewById<Button>(R.id.create_account_button)
        val passwordText = findViewById<EditText>(R.id.password_signup)
        val passwordRepeatText = findViewById<EditText>(R.id.repeat_password_signup)
        val loginText = findViewById<EditText>(R.id.login_signup)
        signupButton.setOnClickListener {
            val password = passwordText.text.toString()
            val login = loginText.text.toString()
            val passwordRepeat = passwordRepeatText.text.toString()

            if(passwordRepeat != password){
                Toast.makeText(
                    baseContext,
                    "passwords have to match!",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            else if(password.length < 6){
                Toast.makeText(
                    baseContext,
                    "passwpord must have at leat 6 characters",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            else{
                auth.createUserWithEmailAndPassword(login, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d(ContentValues.TAG, "createUserWithEmail:success")
                            val user = auth.currentUser
                            Toast.makeText(
                                baseContext,
                                "Welcome $login!",
                                Toast.LENGTH_SHORT,
                            ).show()
                            val feedIntent = Intent(this, FeedActivity::class.java)
                            startActivity(feedIntent)
                        } else {
                            Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Authentication failed. E-mail already in use.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            }

        }

    }

}