package mad.lab1

import android.app.Activity
import android.content.Intent
import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import com.firebase.ui.auth.data.model.User
import com.google.firebase.database.*
import mad.lab1.Database.Book
import mad.lab1.Database.UserInfo
import mad.lab1.User.Authentication
import mad.lab1.User.ShowProfileAndReviews


class AllRequestsBookList : AppCompatActivity() , AllRequestBookAdapter.OnRequestClicked{


    private val SHOW_PROFILE_AND_REVIEWS_CODE = 1

    private val users: ArrayList<String> = ArrayList()
    private var db: FirebaseDatabase? = null
    private var dbRef: DatabaseReference? = null
    private var userListener: ChildEventListener? = null
    private var recyclerView: RecyclerView? = null
    private var noRequestImage : ImageView? = null
    private var noRequestText : TextView? = null

    private lateinit var toolbar: Toolbar


    override fun onRequestClicked(u: UserInfo?, bookRequest : Book?, owner : String?  ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_requests_book_list)

        initializeToolbar()


        noRequestImage = findViewById(R.id.no_request_image_owl)
        noRequestText = findViewById(R.id.no_request_text)

        val currentUser = Authentication.getCurrentUser().uid


        val book = intent.getStringExtra("bookId")
        var b : Book? = null

        db = FirebaseDatabase.getInstance()

        dbRef = db?.reference?.child("bookList")
                ?.child(currentUser)
                ?.child(book)?.child("requests")


        // init layout
        recyclerView = findViewById<RecyclerView>(R.id.requestBookListRecyclerView)
        recyclerView?.visibility = GONE
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = AllRequestBookAdapter(users, book, currentUser,  this, object : AllRequestBookAdapter.OnRequestClicked{
            override fun onRequestClicked(u: UserInfo?, bookRequest: Book?, owner: String?) {

                var i = Intent(this@AllRequestsBookList, ShowProfileAndReviews::class.java)
                var b = Bundle()
                b.putParcelable("user", u)
                b.putParcelable("book", bookRequest)
                b.putString("owner", owner)
                i.putExtra("user_book", b)
                startActivityForResult(i, SHOW_PROFILE_AND_REVIEWS_CODE)
            }
        })





        // define listener
        userListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val u = dataSnapshot.key
                users.add(u!!)
                noRequestText?.visibility = GONE
                noRequestImage?.visibility = GONE
                recyclerView?.visibility = VISIBLE
                recyclerView?.adapter?.notifyItemInserted(users.indexOf(u))
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }

    }

    override fun onResume() {
        super.onResume()
        dbRef?.addChildEventListener(userListener!!)
    }

    override fun onPause() {
        super.onPause()
        dbRef?.removeEventListener(userListener!!)
        val size = users.size
        users.clear()
        recyclerView?.adapter?.notifyItemRangeChanged(0, size)


    }

    override fun onStop() {
        super.onStop()
        recyclerView?.visibility = GONE
        noRequestText?.visibility = VISIBLE
        noRequestImage?.visibility = VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            SHOW_PROFILE_AND_REVIEWS_CODE ->
                if(resultCode != Activity.RESULT_CANCELED){
                    val intent = Intent(this, AllRequestsBookList::class.java)
                    intent.putExtra("uid", data?.getStringExtra("uid"))

                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
        }

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