package mad.lab1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.*
import mad.lab1.Database.UserInfo
import mad.lab1.User.ShowProfileAndReviews

class AllRequestBookAdapter (val d : ArrayList<String>, val c : Context):RecyclerView.Adapter<AllRequestBookAdapter.AllRequestBookViewHolder>(){


    private var users : ArrayList<String> = d
    private var db: FirebaseDatabase? = null
    private var dbRef: DatabaseReference? = null
    private var userListener: ValueEventListener? = null

    class AllRequestBookViewHolder(val view : View) : RecyclerView.ViewHolder(view){
        var nameTextView : TextView = view.findViewById(R.id.requestProfileName)
        var cityTextView : TextView = view.findViewById(R.id.requestProfileCity)
        var profileImage : de.hdodenhof.circleimageview.CircleImageView = view.findViewById(R.id.requestProfileImage)
        var cardView : CardView = view.findViewById(R.id.requestBookCard)
    }


    override fun onBindViewHolder(holder: AllRequestBookViewHolder, position: Int) {


        var u : UserInfo? = null
        db = FirebaseDatabase.getInstance()
        dbRef = db?.reference?.child("users")?.child(users.get(position))

        userListener = object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot?) {
                u = p0?.getValue(UserInfo::class.java)
                holder.nameTextView.text = u?.name
                holder.cityTextView.text = u?.city
                holder.cardView.setOnClickListener {
                    var i = Intent(c, ShowProfileAndReviews::class.java)
                    var b = Bundle()
                    b.putParcelable("user", u)
                    i.putExtra("user", b)
                    c.startActivity(i)

                }
            }
            override fun onCancelled(p0: DatabaseError?) {

            }
        }

        dbRef?.addListenerForSingleValueEvent(userListener)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllRequestBookViewHolder {

        var v : View = LayoutInflater.from(parent.context).inflate(R.layout.element_card_view_all_requests, parent, false)
        return AllRequestBookViewHolder(v)

    }

    override fun getItemCount(): Int {
        return users.size
    }

}