package mad.lab1

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View

class AllRequestsBookList : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_requests_book_list)

        recyclerView = findViewById(R.id.requestBookListRecyclerView)
        viewManager = LinearLayoutManager(this)
        recyclerView.layoutManager = viewManager

        //TODO: to change as soon as db is ready
        var data : ArrayList<String> = ArrayList()
        data.add("ciao")
        data.add("prova")
        data.add("Nome")

        viewAdapter = AllRequestBookAdapter(data, this)
        recyclerView.adapter = viewAdapter
        initializeToolbar()

    }

    private fun initializeToolbar(){
        toolbar = findViewById<Toolbar>(R.id.requestBookListToolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_32dp)


        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })

    }
}


