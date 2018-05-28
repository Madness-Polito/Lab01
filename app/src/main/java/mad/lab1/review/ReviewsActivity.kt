package mad.lab1.review

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.google.firebase.database.*
import mad.lab1.R
import mad.lab1.User.Authentication

class ReviewsActivity : AppCompatActivity() {

    private val reviews: ArrayList<Review> = ArrayList()
    private var dbRef: DatabaseReference? = null
    private var reviewListRef: DatabaseReference? = null
    private var reviewListener: ChildEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reviews_layout)

        // get uid of user of which we want to get the reviews from intent
        val uid = intent.getStringExtra("uid")

        // init layout
        val rv = findViewById<RecyclerView>(R.id.reviewList)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = ReviewsAdapter(reviews, this)

        // get firebase reference for reading reviews
        dbRef = FirebaseDatabase.getInstance()
                                .reference
        reviewListRef = dbRef?.child("reviews")
                             ?.child(uid)
                             ?.child("reviewList")

        // define listener
        reviewListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot?, previousChildName: String?) {
                val review = dataSnapshot!!.getValue(Review::class.java)
                reviews.add(review!!)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot?, previousChildName: String?) {
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot?) {
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot?, previousChildName: String?) {
            }

            override fun onCancelled(databaseError: DatabaseError?) {
            }
        }

    }

    override fun onRestart() {
        super.onRestart()
        reviewListRef?.addChildEventListener(reviewListener)
    }

    override fun onPause() {
        super.onPause()
        reviewListRef?.removeEventListener(reviewListener)
    }
}