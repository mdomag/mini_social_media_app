package com.example.project_start

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.updateTransition
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore

class FeedActivity: AppCompatActivity()  {
    private lateinit var postRecyclerView: RecyclerView
    private lateinit var newArrayList : ArrayList<PostData>
    private lateinit var authorUsernameArray : ArrayList<String>
    private lateinit var songArray : ArrayList<String>
    private lateinit var commentArray : ArrayList<String>
    private lateinit var ratingsArray : ArrayList<Float>
    private lateinit var iconsPair : ArrayList<Pair<String, Int>>
    private lateinit var ratedSongs : String
    private var flag = true

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.feed)
        flag = true
        val profileButton = findViewById<Button>(R.id.profile_button)
        profileButton.setOnClickListener{
            val profileIntent = Intent(this, ProfileActivity::class.java)
            startActivity(profileIntent)
        }
        val signOutButton = findViewById<Button>(R.id.signout_button)
        signOutButton.setOnClickListener{
            Firebase.auth.signOut()
            val signoutIntent = Intent(this, LoginActivity::class.java)
            startActivity(signoutIntent)
        }
        val addPostButton = findViewById<Button>(R.id.add_post_button)
        addPostButton.setOnClickListener {
            val newPostIntent = Intent(this, CreatePost::class.java)
            startActivity(newPostIntent)
        }
        setIcon()
    }

    override fun onBackPressed() {

        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                super.onBackPressed()
                moveTaskToBack(true)
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        setIcon()
        flag = true
        updateFeed()
    }
    private fun setIcon(){
        val db = Firebase.firestore
        val auth = FirebaseAuth.getInstance()
        val profileName = auth.currentUser?.email
        val documentReference = db.collection("post").document("$profileName icon")
        documentReference.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val iconNumber = documentSnapshot.getLong("icon")?.toInt()
                    if (iconNumber != null) {
                        val icon = findViewById<Button>(R.id.profile_button)
                        val iconDrawable = getIconDrawableResource(iconNumber)
                        val picture = ContextCompat.getDrawable(this, iconDrawable)
                        icon.background = picture
                    } else {
                        println("Invalid icon number in Firestore document.")
                    }
                } else {
                    println("Document does not exist.")
                }
            }
    }

    private fun updateFeed() {
        val db = Firebase.firestore
        val postCollection = db.collection("post")

        val auth = FirebaseAuth.getInstance()
        val currentUserName = auth.currentUser?.email

        newArrayList = arrayListOf()
        authorUsernameArray = arrayListOf()
        songArray = arrayListOf()
        commentArray = arrayListOf()
        ratingsArray = arrayListOf()
        iconsPair = arrayListOf()

        postRecyclerView = findViewById(R.id.recycler_view_post)
        postRecyclerView.layoutManager = LinearLayoutManager(this)
        postRecyclerView.setHasFixedSize(false)

        postCollection.addSnapshotListener { documents, exception ->
            if (exception != null) {
                Log.e(TAG, "Error getting documents: ", exception)
                return@addSnapshotListener
            }

            if (documents != null) {
                for (change in documents.documentChanges) {
                    when (change.type) {
                        DocumentChange.Type.ADDED -> {
                            val newDocument = change.document
                            val user = newDocument.getString("user")
                            if(!flag && user != currentUserName && user != null){
                                showLocalNotification("new post!", "check out what $user posted")
                            }
                        }

                        DocumentChange.Type.MODIFIED -> {
                            val newDocument = change.document
                            val user = newDocument.getString("user")
                            val lastUserToRate = newDocument.getString("last user to rate")
                            val lastGivenRating = newDocument.getDouble("last given rating")

                            if(user == currentUserName && user != null && lastUserToRate != null && lastUserToRate != user){
                                showLocalNotification("new rating!", "$lastUserToRate rated your post with $lastGivenRating")
                            }

                        }

                        DocumentChange.Type.REMOVED -> {

                        }
                    }
                }
            }
            flag = false

            authorUsernameArray.clear()
            songArray.clear()
            commentArray.clear()
            ratingsArray.clear()
            newArrayList.clear()
            ratedSongs= ""


            for (document in documents!!) {
                val song = document.getString("song")
                val rating = document.getDouble("rating")
                val comment = document.getString("comment")
                val user = document.getString("user")
                val userIcon = document.getString("userIcon")
                val icon = document.getLong("icon")?.toInt()
                val ratedSongsByUser = document.getString("songs $currentUserName")

                if (user != null) {
                    authorUsernameArray.add(user)
                }
                if (song != null) {
                    songArray.add(song)
                }
                if (comment != null) {
                    commentArray.add(comment)
                }
                if (rating != null) {
                    ratingsArray.add(rating.toFloat())
                }
                if (userIcon != null && icon != null){
                    val pair = Pair(userIcon, icon)
                    iconsPair.add(pair)
                }
                if (ratedSongsByUser != null) {
                    ratedSongs = ratedSongsByUser
                }

            }
            getPostData()
        }
    }

    private fun findUserIconPair(user: String): Pair<String, Int>? {
        return iconsPair.find { it.first == user }
    }

    private fun getPostData() {
        for(i in authorUsernameArray.indices){
            val pair = findUserIconPair(authorUsernameArray[i])
            val icon = pair?.second
            val post = icon?.let { PostData(it, authorUsernameArray[i], songArray[i], commentArray[i], ratingsArray[i]) }
            if (post != null) {
                newArrayList.add(post)
            }
        }
        var adapter = PostAdapter(newArrayList)
        postRecyclerView.adapter = adapter
        adapter.setOnRatingBarChangeListener(object : PostAdapter.OnRatingBarChangeListener {

            override fun onRatingBarChanged(position: Int, rating: Float, song: String) {
                updateRatingBar(song, rating, position)
            }
        })

    }

    private fun updateRatingBar(song: String, rating: Float, position: Int) {
        val db = Firebase.firestore

        val sanitizedSong = song.replace("/", "_").replace(":", "_")
        val songDocumentReference = db.collection("post").document(sanitizedSong)

        var newRating = rating
        var oldRating = ratingsArray[position]
        if (oldRating != 0f) {
            newRating = (rating + oldRating) / 2f
        }

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser?.email

        val postData = hashMapOf(
            "rating" to newRating,
            "last user to rate" to user,
            "last given rating" to rating
        )

        if (song.isNotEmpty() && !ratedSongs.contains(song)) {
            songDocumentReference.set(postData, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot added with ID: ${songDocumentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error adding document", e)
                }

            ratedSongs = "$ratedSongs $song"

            val ratedDocumentReference = db.collection("post").document("rated by $user")

            ratedDocumentReference.get().addOnSuccessListener { documentSnapshot ->
                if (!documentSnapshot.exists()) {
                    val initialData = hashMapOf("songs $user" to "")
                    ratedDocumentReference.set(initialData)
                }

                db.runTransaction { transaction ->
                    val snapshot = transaction.get(ratedDocumentReference)
                    val currentData = snapshot.getString("songs $user") ?: ""

                    val newData = "$currentData $song"

                    transaction.update(ratedDocumentReference, "songs $user", newData)

                    newData
                }.addOnSuccessListener { result ->
                    Log.d(TAG, "Transaction success. Result: $result")
                }.addOnFailureListener { e ->
                    Log.e(TAG, "Transaction failure.", e)
                }

            }.addOnFailureListener { e ->
                Log.e(TAG, "Error checking if document exists", e)
            }

        }
        else{
            Toast.makeText(applicationContext, "you already rated this song!", Toast.LENGTH_SHORT).show()
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

    private fun showLocalNotification(title: String, contentText: String) {
        val channelId = "your_channel_id"
        val notificationId = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            var description = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = description
                setImportance(NotificationManager.IMPORTANCE_HIGH)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }


        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.icon_inverted)
            .setContentTitle(title)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                }
                startActivity(intent)
                return
            }
            notify(notificationId, builder.build())
        }

    }

}

