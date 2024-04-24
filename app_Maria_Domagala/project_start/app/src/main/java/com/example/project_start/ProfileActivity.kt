package com.example.project_start

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class ProfileActivity: ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile)
        val profileNameView = findViewById<TextView>(R.id.profile_name)
        auth = FirebaseAuth.getInstance()
        val profileName = auth.currentUser?.email
        profileNameView.text = "email: $profileName"
        val db = Firebase.firestore
        val documentReference = db.collection("post").document("$profileName icon")
        documentReference.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val iconNumber = documentSnapshot.getLong("icon")?.toInt()
                    if (iconNumber != null) {
                        val icon = findViewById<ImageView>(R.id.profile_avatar)
                        val iconDrawable = getIconDrawableResource(iconNumber)
                        val picture = ContextCompat.getDrawable(this, iconDrawable)
                        icon.setImageDrawable(picture)
                    } else {
                        println("Invalid icon number in Firestore document.")
                    }
                } else {
                    println("Document does not exist.")
                }
            }

        val iconButton1 = findViewById<ImageButton>(R.id.icon1_button)
        iconButton1.setOnClickListener{
            val icon = findViewById<ImageView>(R.id.profile_avatar)
            val picture = ContextCompat.getDrawable(this, R.drawable.icon1)
            icon.setImageDrawable(picture)
            val icons = hashMapOf(
                "userIcon" to profileName,
                "icon" to 1
            )
            db.collection("post").document("$profileName icon")
                .set(icons)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }

        val iconButton2 = findViewById<ImageButton>(R.id.icon2_button)
        iconButton2.setOnClickListener{
            val icon = findViewById<ImageView>(R.id.profile_avatar)
            val picture = ContextCompat.getDrawable(this, R.drawable.icon2)
            icon.setImageDrawable(picture)
            val icons = hashMapOf(
                "userIcon" to profileName,
                "icon" to 2
            )
            db.collection("post").document("$profileName icon")
                .set(icons)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }

        val iconButton3 = findViewById<ImageButton>(R.id.icon3_button)
        iconButton3.setOnClickListener{
            val icon = findViewById<ImageView>(R.id.profile_avatar)
            val picture = ContextCompat.getDrawable(this, R.drawable.icon3)
            icon.setImageDrawable(picture)
            val icons = hashMapOf(
                "userIcon" to profileName,
                "icon" to 3
            )
            db.collection("post").document("$profileName icon")
                .set(icons)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }

        val iconButton4 = findViewById<ImageButton>(R.id.icon4_button)
        iconButton4.setOnClickListener{
            val icon = findViewById<ImageView>(R.id.profile_avatar)
            val picture = ContextCompat.getDrawable(this, R.drawable.icon4)
            icon.setImageDrawable(picture)
            val icons = hashMapOf(
                "userIcon" to profileName,
                "icon" to 4
            )
            db.collection("post").document("$profileName icon")
                .set(icons)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }

    }
    private fun getIconDrawableResource(iconNumber: Int): Int {
        return when (iconNumber) {
            1 -> R.drawable.icon1
            2 -> R.drawable.icon2
            3 -> R.drawable.icon3
            4 -> R.drawable.icon4
            else -> throw IllegalArgumentException("Invalid icon number: $iconNumber")
        }
    }

    override fun onResume() {
        super.onResume()
        val profileNameView = findViewById<TextView>(R.id.profile_name)
        auth = FirebaseAuth.getInstance()
        val profileName = auth.currentUser?.email

        profileNameView.text = "email: $profileName"
        val db = Firebase.firestore
        val documentReference = db.collection("post").document("$profileName icon")
        documentReference.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val iconNumber = documentSnapshot.getLong("icon")?.toInt()
                    if (iconNumber != null) {
                        val icon = findViewById<ImageView>(R.id.profile_avatar)
                        val iconDrawable = getIconDrawableResource(iconNumber)
                        val picture = ContextCompat.getDrawable(this, iconDrawable)
                        icon.setImageDrawable(picture)
                    } else {
                        println("Invalid icon number in Firestore document.")
                    }
                } else {
                    println("Document does not exist.")
                }
            }


        val iconButton1 = findViewById<ImageButton>(R.id.icon1_button)
        iconButton1.setOnClickListener{
            val icon = findViewById<ImageView>(R.id.profile_avatar)
            val picture = ContextCompat.getDrawable(this, R.drawable.icon1)
            icon.setImageDrawable(picture)
            val icons = hashMapOf(
                "userIcon" to profileName,
                "icon" to 1
            )
            db.collection("post").document("$profileName icon")
                .set(icons)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }

        val iconButton2 = findViewById<ImageButton>(R.id.icon2_button)
        iconButton2.setOnClickListener{
            val icon = findViewById<ImageView>(R.id.profile_avatar)
            val picture = ContextCompat.getDrawable(this, R.drawable.icon2)
            icon.setImageDrawable(picture)
            val icons = hashMapOf(
                "userIcon" to profileName,
                "icon" to 2
            )
            db.collection("post").document("$profileName icon")
                .set(icons)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }

        val iconButton3 = findViewById<ImageButton>(R.id.icon3_button)
        iconButton3.setOnClickListener{
            val icon = findViewById<ImageView>(R.id.profile_avatar)
            val picture = ContextCompat.getDrawable(this, R.drawable.icon3)
            icon.setImageDrawable(picture)
            val icons = hashMapOf(
                "userIcon" to profileName,
                "icon" to 3
            )
            db.collection("post").document("$profileName icon")
                .set(icons)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }

        val iconButton4 = findViewById<ImageButton>(R.id.icon4_button)
        iconButton4.setOnClickListener{
            val icon = findViewById<ImageView>(R.id.profile_avatar)
            val picture = ContextCompat.getDrawable(this, R.drawable.icon4)
            icon.setImageDrawable(picture)
            val icons = hashMapOf(
                "userIcon" to profileName,
                "icon" to 4
            )
            db.collection("post").document("$profileName icon")
                .set(icons)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }


    }


}