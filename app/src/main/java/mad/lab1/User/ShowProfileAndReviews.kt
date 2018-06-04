package mad.lab1.User

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.review.view.*
import mad.lab1.AllRequestsBookList
import mad.lab1.Database.Book
import mad.lab1.Database.UserInfo
import mad.lab1.GlideApp
import mad.lab1.Notifications.Constants
import mad.lab1.R
import mad.lab1.review.ReviewsActivity
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class ShowProfileAndReviews : AppCompatActivity() {


    private val SHOW_PROFILE_AND_REVIEWS_CODE = 1

    private lateinit var toolbar: Toolbar
    private lateinit var name : TextView
    private lateinit var city : TextView
    private lateinit var birthDate : TextView
    private lateinit var email : TextView
    private lateinit var phone : TextView
    private lateinit var image : ImageView
    private lateinit var bookBorrowedNumber : TextView
    private lateinit var showMoreReviews : ImageView
    private lateinit var showMoreReviewsText : TextView
    private lateinit var chooseUserFab : FloatingActionButton
    private lateinit var b : Book
    private lateinit var u: UserInfo
    private lateinit var bookOwner: String



    private var ratingBar : RatingBar? = null
    private var totStarCount: Float? = null
    private var totReviewCount: Float? = null
    private var numStar: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile_and_reviews)

        var bundle = intent.getBundleExtra("user_book")
        u = bundle.getParcelable("user")
        b = bundle.getParcelable("book")
        bookOwner = bundle.getString("owner")


        initialization()



        name.text = u.name
        city.text = u.city
        birthDate.text = u.dob
        email.text = u.mail
        phone.text = u.phone


        setUpRatingBar(u.uid)

        showMoreReviews.setOnClickListener{
            var i = Intent(this, ReviewsActivity::class.java)
            i.putExtra("uid", u.uid)
            startActivity(i)
        }

        showMoreReviewsText.setOnClickListener{
            var i = Intent(this, ReviewsActivity::class.java)
            i.putExtra("uid", u.uid)
            startActivity(i)
        }


        chooseUserFab.setOnClickListener {



            //Adding book to borrowed book

            val refBorrowed = FirebaseDatabase.getInstance().getReference("borrowedBooks")
            refBorrowed.child(u.uid)
                    .child(b?.bookId)
                    .setValue(b)
            refBorrowed.child(u.uid)
                    .child(b?.bookId)
                    .child("owner")
                    .setValue(bookOwner)

            refBorrowed.child(u.uid).child(b.bookId).child("status").setValue("pending")

            val refBookId = FirebaseDatabase.getInstance().getReference("bookID")
            refBookId.child(b.bookId).child("status").setValue("pending")

            //Changing status from free to pending
            var refPending = FirebaseDatabase.getInstance().getReference("bookList")
            refPending.child(bookOwner)
                    .child(b?.bookId)
                    .child("status")
                    .setValue("pending")
            refPending.child(bookOwner)
                    .child(b?.bookId)
                    .child("reviewed")
                    .setValue("false")
            refPending.child(bookOwner).child(b?.bookId).child("selectedRequest").setValue(u.uid)

            //send a notification to the borrower that their request has been accepted
            sendNotification(b?.title, u.uid);


            //Starting chat
            val intent = Intent(this, AllRequestsBookList::class.java)
            intent.putExtra("uid", u.uid)

            setResult(Activity.RESULT_OK, intent)
            finish()

        }



    }

    private fun sendNotification(title: String?, uid: String?) {

        val thread = Thread(Runnable {
            try {

                val url = URL("https://fcm.googleapis.com/fcm/send")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8")
                conn.setRequestProperty("Authorization", "key=" + Constants.SERVER_KEY)
                conn.setRequestProperty("Accept", "application/json")
                conn.doOutput = true
                conn.doInput = true

                val jsonParam = JSONObject()
                val jsonParam2 = JSONObject()
                jsonParam2.put("body",  getString(R.string.requestAcceptedBody1) + " " + title + " " + getString(R.string.requestAcceptedBody2))
                jsonParam2.put("title", getString(R.string.requestAcceptedTitle))
                jsonParam2.put("tag", Constants.NOTIFICATION_TAG)
                jsonParam2.put("bookTitle", title)
                jsonParam2.put("type", Constants.REQUESTACCEPTED)
                jsonParam.put("data", jsonParam2)
                jsonParam.put("to", "/topics/" + uid)

                /*jsonParam3.put("body", msg);
                    jsonParam3.put("title", "testTitle");
                    jsonParam2.put("topic", user);
                    jsonParam2.put("notification", jsonParam3);
                    jsonParam.put("message", jsonParam2);*/


                Log.i("JSON", jsonParam.toString())
                val os = DataOutputStream(conn.outputStream)
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                os.writeBytes(jsonParam.toString())

                os.flush()
                os.close()

                Log.i("STATUS", conn.responseCode.toString())
                Log.i("MSG", conn.responseMessage)

                conn.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        thread.start()
    }


    private fun initialization(){

        name = findViewById(R.id.showRequestTextName)
        city = findViewById(R.id.showTextCityStateName)
        birthDate = findViewById(R.id.showRequestTextBirthDate)
        email = findViewById(R.id.showRequestTextMail)
        phone = findViewById(R.id.showRequestTextTelephone)
        image = findViewById(R.id.showRequestImageProfile)
        bookBorrowedNumber = findViewById(R.id.showRequestBookBorrowed)
        showMoreReviews = findViewById(R.id.showMoreReviews)
        showMoreReviewsText = findViewById(R.id.textView6)

        chooseUserFab = findViewById(R.id.choose_user_fab)
        ratingBar = findViewById(R.id.show_profile_and_review_rating_bar)


        initializeToolbar()



        //TODO Set image, set number of borrowed books


        // load profile pic
        val picRef : StorageReference = FirebaseStorage.getInstance()
                .getReference("userPics")
                .child(u.uid)
        GlideApp.with(this)
                .load(picRef)
                .into(image)
    }


    private fun initializeToolbar(){
        toolbar = findViewById<Toolbar>(R.id.requestBookListToolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_32dp)


        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener(View.OnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
            })

    }

    private fun setUpRatingBar(uid : String?){
        val db = FirebaseDatabase.getInstance()
        val dbRef = db.reference.child("reviews").child(uid!!)
        val totCountRef = dbRef.child("totStarCount")
        val reviewCountRef = dbRef.child("reviewCount")


        totCountRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    totStarCount = dataSnapshot.getValue(Float::class.java)
                    if (totReviewCount != null) {
                        numStar = totStarCount!! / totReviewCount!!
                    } else {
                        numStar = 0f
                    }
                    ratingBar?.rating = numStar!!

                }


            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        reviewCountRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                totReviewCount = dataSnapshot.getValue(Float::class.java)
                if (totStarCount != null) {
                    numStar = totStarCount!! / totReviewCount!!
                } else {
                    numStar = 0f
                }
                ratingBar?.rating = numStar!!

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })


    }
}
