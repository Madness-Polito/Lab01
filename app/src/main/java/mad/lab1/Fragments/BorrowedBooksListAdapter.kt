package mad.lab1.Fragments

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.element_card_view_borrowed_books.view.*
import mad.lab1.Database.Book
import mad.lab1.Database.UserInfo
import mad.lab1.R
import java.util.ArrayList
import java.util.zip.Inflater

class BorrowedBooksListAdapter(val b : ArrayList<Book>, val c : Context):RecyclerView.Adapter<BorrowedBooksListAdapter.BorrowedBooksViewHolder>() {

    val books : ArrayList<Book> = b

    class BorrowedBooksViewHolder(val view : View) : RecyclerView.ViewHolder(view){
        var name : TextView = view.findViewById(R.id.titleBookBorrowedTextView)
        var author : TextView = view.findViewById(R.id.authorBookBorrowedTextView)
        var bookImage : ImageView = view.findViewById(R.id.imageBookBorrowed)
        var card : CardView = view.findViewById(R.id.borrowedBookCardView)

    }




    override fun onBindViewHolder(holder: BorrowedBooksViewHolder, position: Int) {
        var book : Book? = books?.get(position)
        var user : String? = null
        holder.name.text = book?.title
        holder.author.text = book?.author

        var db : FirebaseDatabase? = FirebaseDatabase.getInstance()
        var dbRef : DatabaseReference? = db?.reference?.child("bookID")?.child(book?.bookId)?.child("uid")
        var listener : ValueEventListener? = null

        listener = object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot?) {
                user = p0?.getValue(String::class.java)

            }

            override fun onCancelled(p0: DatabaseError?) {

            }
        }
        dbRef?.addListenerForSingleValueEvent(listener)

        Glide.with(holder.bookImage.context)
                .load(books.get(position).thumbURL)
                .apply(RequestOptions()
                .placeholder(R.drawable.my_library_selected_24dp)
                .centerCrop()
                .dontAnimate()
                .dontTransform())
                .into(holder.bookImage)

        holder.card.setOnClickListener {
            //TODO Open Dialog based on notification and book status
            Toast.makeText(c, user, Toast.LENGTH_SHORT).show()

            //switch(book status)
            // case BOOKED  -> Chat    ||       Confirmation, book handed to owner (This will change status booked -> free)
            // case PENDING -> Chat    ||       Confirmation, book received from owner (This will change status pending -> booked)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BorrowedBooksViewHolder {
        var v : View = LayoutInflater.from(parent.context).inflate(R.layout.element_card_view_borrowed_books, parent, false)
        return BorrowedBooksViewHolder(v)
    }




    override fun getItemCount(): Int {
        return books.size
    }
}