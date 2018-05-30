package mad.lab1

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import com.firebase.ui.auth.data.model.User
import com.google.firebase.database.*
import mad.lab1.Database.Book
import mad.lab1.Database.UserInfo
import mad.lab1.User.Authentication


class AllRequestsBookList : AppCompatActivity() {

    private val users: ArrayList<String> = ArrayList()
    private var db: FirebaseDatabase? = null
    private var dbRef: DatabaseReference? = null
    private var userListener: ChildEventListener? = null
    private var recyclerView: RecyclerView? = null

    private lateinit var toolbar: Toolbar




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_requests_book_list)

        initializeToolbar()

        val currentUser = Authentication.getCurrentUser().uid


        val book = intent.getStringExtra("bookId")
        var b : Book? = null

        db = FirebaseDatabase.getInstance()

        dbRef = db?.reference?.child("bookList")
                ?.child(currentUser)
                ?.child(book)?.child("requests")


        // init layout
        recyclerView = findViewById<RecyclerView>(R.id.requestBookListRecyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = AllRequestBookAdapter(users, book, currentUser,  this)




        // define listener
        userListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot?, previousChildName: String?) {
                val u = dataSnapshot!!.key
                users.add(u!!)
                recyclerView?.adapter?.notifyItemInserted(users.indexOf(u))
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

    override fun onResume() {
        super.onResume()
        dbRef?.addChildEventListener(userListener)
    }

    override fun onPause() {
        super.onPause()
        dbRef?.removeEventListener(userListener)
        var size = users.size
        users.clear()
        recyclerView?.adapter?.notifyItemRangeChanged(0, size)
    }




    private fun initializeToolbar(){
        toolbar = findViewById<Toolbar>(R.id.requestBookListToolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_32dp)


        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })

    }

}