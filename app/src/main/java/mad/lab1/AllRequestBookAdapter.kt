package mad.lab1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.review.view.*
import mad.lab1.Database.Book
import mad.lab1.Database.UserInfo
import mad.lab1.User.ShowProfileAndReviews

class AllRequestBookAdapter (val d : ArrayList<String>,var bookRequestedId : String?, var owner : String?, val c : Context, listener : OnRequestClicked):RecyclerView.Adapter<AllRequestBookAdapter.AllRequestBookViewHolder>(){


    private var users : ArrayList<String> = d
    private var bookOwner : String? = owner
    private var book : String? = bookRequestedId
    private var bookRequest : Book? = null
    private var db: FirebaseDatabase? = null
    private var dbRef: DatabaseReference? = null
    private var userListener: ValueEventListener? = null
    private var listenerRequestClicked : OnRequestClicked = listener




    interface OnRequestClicked {
        fun onRequestClicked(u: UserInfo?, bookRequest : Book?, owner : String? )
    }

    class AllRequestBookViewHolder(val view : View) : RecyclerView.ViewHolder(view){
        var nameTextView : TextView = view.findViewById(R.id.requestProfileName)
        var cityTextView : TextView = view.findViewById(R.id.requestProfileCity)
        var profileImage : de.hdodenhof.circleimageview.CircleImageView = view.findViewById(R.id.requestProfileImage)
        var cardView : CardView = view.findViewById(R.id.requestBookCard)
        var ratingBar : RatingBar = view.findViewById(R.id.all_request_rating_bar)
        var totStarCount: Float? = null
        var totReviewCount: Float? = null
        var numStar: Float? = null
    }


    override fun onBindViewHolder(holder: AllRequestBookViewHolder, position: Int) {


        var u : UserInfo? = null
        val uid: String = users[position]
        db = FirebaseDatabase.getInstance()
        dbRef = db?.reference
                  ?.child("users")
                  ?.child(uid)

        // load profile pic
        val picRef : StorageReference = FirebaseStorage.getInstance()
                .getReference("userPics")
                .child(uid)
        GlideApp.with(c)
                .load(picRef)
                .placeholder(R.drawable.ic_account_circle_144dp)
                .into(holder.profileImage)

        // set rating stars
        val reviewRef = FirebaseDatabase.getInstance()
                .getReference("reviews")
                .child(uid)
        reviewRef.child("totStarCount")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(@NonNull p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val totStarCount: Float = p0.getValue(Float::class.java) ?: 0f

                        // dowload numReviews
                        reviewRef.child("reviewCount")
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onCancelled(@NonNull p0: DatabaseError) {
                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        val reviewCount: Float = p0.getValue(Float::class.java) ?: 1f

                                        holder.ratingBar.rating = totStarCount / reviewCount
                                    }
                                })
                    }
                })

        userListener = object : ValueEventListener{
            override fun onDataChange( p0: DataSnapshot) {
                u = p0.getValue(UserInfo::class.java)
                holder.nameTextView.text = u?.name
                holder.cityTextView.text = u?.city

                val dbRefBook = db?.reference
                                    ?.child("bookID")
                                    ?.child(book!!)
                dbRefBook?.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        bookRequest = p0.getValue(Book :: class.java)
                        bookRequest?.bookId = p0.key
                    }
                })


                holder.cardView.setOnClickListener {

                    listenerRequestClicked.onRequestClicked(u, bookRequest, bookOwner)


                }
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        }

        dbRef?.addListenerForSingleValueEvent(userListener!!)


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllRequestBookViewHolder {

        val v : View = LayoutInflater.from(parent.context).inflate(R.layout.element_card_view_all_requests, parent, false)
        return AllRequestBookViewHolder(v)

    }

    override fun getItemCount(): Int {
        return users.size
    }

}