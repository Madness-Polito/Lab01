package mad.lab1.User

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import mad.lab1.AllRequestsBookList
import mad.lab1.Database.Book
import mad.lab1.Database.UserInfo
import mad.lab1.R
import mad.lab1.review.ReviewsActivity
import org.w3c.dom.Text

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
            refPending.child(bookOwner).child(b?.bookId).child("selectedRequest").setValue(u.uid)


            //Starting chat
            val intent = Intent(this, AllRequestsBookList::class.java)
            intent.putExtra("uid", u.uid)

            setResult(Activity.RESULT_OK, intent)
            finish()

        }



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



        initializeToolbar()



        //TODO Set image, set number of borrowed books


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
}
