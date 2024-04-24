package com.example.project_start

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class CreatePost : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_post)

        val songEditText = findViewById<EditText>(R.id.spotify_link)
        val songCommentEditText = findViewById<EditText>(R.id.song_comment)
        val postButton = findViewById<Button>(R.id.post_button)

        postButton.setOnClickListener {
            val db = Firebase.firestore
            val auth = FirebaseAuth.getInstance()
            val user = auth.currentUser?.email

            val songValue = songEditText.text.toString()
            val commentValue = songCommentEditText.text.toString()

            val sanitizedSong = songValue.replace("/", "_").replace(":", "_")
            val songDocumentReference = db.collection("post").document(sanitizedSong)

            val context: Context = applicationContext

            if (commentValue.isEmpty() || songValue.isEmpty()) {
                Toast.makeText(context, "Enter song link and comment!", Toast.LENGTH_LONG).show()
            } else {
                val postData = hashMapOf(
                    "song" to songValue,
                    "comment" to commentValue,
                    "rating" to 0f,
                    "user" to user
                )

                songDocumentReference.set(postData)
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot added with ID: ${songDocumentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error adding document", e)
                    }

                Toast.makeText(context, "Posted!", Toast.LENGTH_LONG).show()

                val returnToFeedIntent = Intent(this, FeedActivity::class.java)
                startActivity(returnToFeedIntent)
            }
        }
    }
}
