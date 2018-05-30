package mad.lab1.User

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import mad.lab1.R

class ShowProfileAndReviews : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile_and_reviews)
        initializeToolbar()
    }



    private fun initializeToolbar(){
        toolbar = findViewById<Toolbar>(R.id.requestBookListToolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_32dp)


        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })

    }
}
