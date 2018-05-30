package mad.lab1.User

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import mad.lab1.Database.UserInfo
import mad.lab1.R
import mad.lab1.review.ReviewsActivity
import org.w3c.dom.Text

class ShowProfileAndReviews : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile_and_reviews)
        initialization()
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

        var bundle = intent.getBundleExtra("user")
        var u : UserInfo = bundle.getParcelable("user")

        initializeToolbar()

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


        //TODO Set image, set number of borrowed books


    }


    private fun initializeToolbar(){
        toolbar = findViewById<Toolbar>(R.id.requestBookListToolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_32dp)


        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })

    }
}
