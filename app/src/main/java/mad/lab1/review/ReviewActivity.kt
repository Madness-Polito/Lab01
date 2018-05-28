package mad.lab1.review

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.EditText
import android.widget.RatingBar
import com.google.firebase.database.*
import mad.lab1.R
import mad.lab1.User.Authentication

class ReviewActivity : AppCompatActivity() {

    // constants
    private val TITLE : String = "title"
    private val BODY  : String = "body"
    private val NUM_STARS : String = "numStars"

    private val uid = intent.getStringExtra("uid") // uid of user we want to give a review
    private var title : EditText? = null
    private var body  : EditText? = null
    private var ratingBar : RatingBar? = null
    private var okBtn : FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        // identify layout objects
        title = findViewById(R.id.title)
        body  = findViewById(R.id.body)
        ratingBar = findViewById(R.id.ratingBar)
        okBtn = findViewById(R.id.btn_ok)

        // define okBtn action when pressed
        okBtn?.setOnClickListener{view ->

            val reviewRef = FirebaseDatabase.getInstance()
                                            .reference
                                            .child("reviews")
                                            .child(uid)

            // 1) add stars and 1 to user's starCount & reviewCount
            reviewRef.child("totStarCount")
                    .runTransaction(object : Transaction.Handler{
                        override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                        }

                        override fun doTransaction(mutableData: MutableData?): Transaction.Result {

                            var totStarCount : Int? = mutableData?.getValue(Int::class.javaObjectType)

                            if (totStarCount == null)
                                return Transaction.success(mutableData)

                            totStarCount += ratingBar!!.numStars

                            mutableData?.value = totStarCount
                            return Transaction.success(mutableData)
                        }
                    })

            reviewRef.child("reviewCount")
                    .runTransaction(object : Transaction.Handler{
                        override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                        }

                        override fun doTransaction(mutableData: MutableData?): Transaction.Result {

                            var reviewCount : Int? = mutableData?.getValue(Int::class.javaObjectType)

                            if (reviewCount == null)
                                return Transaction.success(mutableData)

                            reviewCount++

                            mutableData?.value = reviewCount
                            return Transaction.success(mutableData)
                        }
                    })


            // 2) add review to his path
            val review : Review = Review(Authentication.getCurrentUser().uid,
                                        Authentication.getCurrentUser().displayName!!,
                                        ratingBar!!.numStars,
                                        title!!.text.toString(),
                                        body!!.text.toString())
            reviewRef.child("reviewList")
                    .push()
                    .setValue(review)
        }


    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        // save form data
        outState?.putString(TITLE, title!!.text.toString())
        outState?.putString(BODY, body!!.text.toString())
        outState?.putInt(NUM_STARS, ratingBar!!.numStars)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        // restore form data
        title!!.setText(savedInstanceState!!.getString(TITLE))
        body!!.setText(savedInstanceState.getString(BODY))
        ratingBar!!.numStars = savedInstanceState.getInt(NUM_STARS)
    }
}