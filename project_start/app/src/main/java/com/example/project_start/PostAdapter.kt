package com.example.project_start

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.regex.Pattern

class PostAdapter(private val postsList : ArrayList<PostData>) :
    RecyclerView.Adapter<PostAdapter.MyViewHolder>() {

    private lateinit var ratingBarListener : OnRatingBarChangeListener

    interface OnRatingBarChangeListener{
        fun onRatingBarChanged(position: Int, rating: Float, song: String)
    }

    fun setOnRatingBarChangeListener(listener: OnRatingBarChangeListener){
        ratingBarListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.post_row,parent, false)
        return MyViewHolder(itemView, ratingBarListener)
    }

    override fun getItemCount(): Int {
        return postsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = postsList[position]
        //holder.ratingBar.rating = 5F
        holder.song.text = currentItem.song
        holder.song.makeLinks()
        holder.authorUsername.text = currentItem.userName
        holder.authorComment.text = currentItem.comment
        val drawableId = getIconDrawableResource(currentItem.icon)
        val drawable = ContextCompat.getDrawable(holder.itemView.context, drawableId)
        holder.authorIcon.setImageDrawable(drawable)
        holder.ratingBarResult.rating = currentItem.rating
        holder.ratingResultTextView.text = currentItem.rating.toString()
    }

    class MyViewHolder(itemView : View, listener: OnRatingBarChangeListener) : RecyclerView.ViewHolder(itemView){
        val ratingBar : RatingBar = itemView.findViewById(R.id.rating_bar)
        val authorUsername : TextView = itemView.findViewById(R.id.author_username)
        val song : TextView = itemView.findViewById(R.id.song)
        val authorComment : TextView = itemView.findViewById(R.id.author_comment)
        val authorIcon : ImageView = itemView.findViewById(R.id.author_icon)
        val ratingBarResult : RatingBar = itemView.findViewById(R.id.ratingBar_result)
        val ratingResultTextView : TextView = itemView.findViewById(R.id.result_view)

        init {
            ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
                listener.onRatingBarChanged(adapterPosition, rating, song.text.toString())
            }
        }

    }

    private fun TextView.makeLinks() {
        val pattern = Pattern.compile("\\bhttps?://\\S+\\b")
        val matcher = pattern.matcher(this.text)

        val spannableString = SpannableString(this.text)
        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val url = spannableString.subSequence(start, end).toString()
                    val context = widget.context
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(context, "no suitable app found to open the link", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        this.text = spannableString
        this.movementMethod = LinkMovementMethod.getInstance()
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
}