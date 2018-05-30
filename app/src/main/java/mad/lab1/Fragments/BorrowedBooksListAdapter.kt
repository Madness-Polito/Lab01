package mad.lab1.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import mad.lab1.Database.Book
import mad.lab1.R
import mad.lab1.chat.ChatActivity
import mad.lab1.review.ReviewActivity
import java.util.ArrayList

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
            //Toast.makeText(c, user, Toast.LENGTH_SHORT).show()

            when(book?.status){
                "pending" ->{
                    openPendingDialog(c, user, book);
                }
                "booked" ->{
                    openBookedDialog(c, user, book)
                }
                else ->{}
            }

            //switch(book status)
            // case BOOKED  -> Chat    ||       Confirmation, book handed to owner (This will change status booked -> free)
            // case PENDING -> Chat    ||       Confirmation, book received from owner (This will change status pending -> booked)
        }


    }

    private fun openBookedDialog(c: Context, user: String?, book: Book) {
        val dialog = AlertDialog.Builder(c);
        val dialogView = LayoutInflater.from(c).inflate(R.layout.chat_recieved_returned_dialog, null);
        val btn_chat = dialogView.findViewById<ImageButton>(R.id.btn_chat);
        val btn_recieved_returned = dialogView.findViewById<Button>(R.id.btn_recieved_returned);
        btn_recieved_returned.text = c.getString(R.string.returnedBook);
        dialog.setView(dialogView);
        val customDialog = dialog.create();

        btn_chat.setOnClickListener(View.OnClickListener {
            val intent = Intent(c, ChatActivity::class.java)
            //TODO: make this intent start in the proper way
            intent.setAction(user);
            c.startActivity(intent)
            customDialog.cancel()
        })

        btn_recieved_returned.setOnClickListener(View.OnClickListener {
            val fbUser = FirebaseAuth.getInstance().currentUser

            //change the book status
            val ref = FirebaseDatabase.getInstance().reference.child("bookList").child(user).child(book!!.bookId)
            val ref2 = FirebaseDatabase.getInstance().reference.child("borrowedBooks").child(fbUser!!.uid).child(book.bookId)

            //set book to free
            ref.child("status").setValue("free")
            //remove book from booked books
            ref2.removeValue()

            val intent = Intent(c, ReviewActivity::class.java)
            intent.putExtra("uid", user);
            c.startActivity(intent)
            customDialog.cancel()

            val refBookId = FirebaseDatabase.getInstance().getReference("bookID")
            refBookId.child(book.bookId).child("status").setValue("free")
        })

        customDialog.show();
    }

    private fun openPendingDialog(c: Context, user: String?, book: Book?) {

        val dialog = AlertDialog.Builder(c);
        val dialogView = LayoutInflater.from(c).inflate(R.layout.chat_recieved_returned_dialog, null);
        val btn_chat = dialogView.findViewById<ImageButton>(R.id.btn_chat);
        val btn_recieved_returned = dialogView.findViewById<Button>(R.id.btn_recieved_returned);
        btn_recieved_returned.text = c.getString(R.string.recievedBook);
        dialog.setView(dialogView);
        val customDialog = dialog.create();

        //open chat
        btn_chat.setOnClickListener(View.OnClickListener {
            val intent = Intent(c, ChatActivity::class.java)
            //TODO: make this intent start in the proper way
            intent.setAction(user);
            c.startActivity(intent)
            customDialog.cancel()
        })

        //book status = booked; deleted all requests for this book
        btn_recieved_returned.setOnClickListener(View.OnClickListener {
            val fbUser = FirebaseAuth.getInstance().currentUser

            //change the book status
            val ref = FirebaseDatabase.getInstance().reference.child("bookList").child(user).child(book!!.bookId)
            val ref2 = FirebaseDatabase.getInstance().reference.child("borrowedBooks").child(fbUser!!.uid).child(book.bookId).child("status")


            //TODO: send notification to all other users that the book isn't available
            ref.child("requests").removeValue();
            ref.child("status").setValue("booked")
            ref2.setValue("booked")
            val refBookId = FirebaseDatabase.getInstance().getReference("bookID")
            refBookId.child(book.bookId).child("status").setValue("booked")
        })

        customDialog.show();

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BorrowedBooksViewHolder {
        var v : View = LayoutInflater.from(parent.context).inflate(R.layout.element_card_view_borrowed_books, parent, false)
        return BorrowedBooksViewHolder(v)
    }




    override fun getItemCount(): Int {
        return books.size
    }
}