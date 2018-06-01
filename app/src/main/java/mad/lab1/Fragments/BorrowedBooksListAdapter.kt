package mad.lab1.Fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import mad.lab1.Database.Book
import mad.lab1.Notifications.Constants
import mad.lab1.R
import mad.lab1.chat.ChatActivity
import mad.lab1.review.ReviewActivity
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

class BorrowedBooksListAdapter(val b : ArrayList<Book>, val c : Context):RecyclerView.Adapter<BorrowedBooksListAdapter.BorrowedBooksViewHolder>() {

    val books : ArrayList<Book> = b

    class BorrowedBooksViewHolder(val view : View) : RecyclerView.ViewHolder(view){
        var name : TextView = view.findViewById(R.id.titleBookBorrowedTextView)
        var author : TextView = view.findViewById(R.id.authorBookBorrowedTextView)
        var bookImage : ImageView = view.findViewById(R.id.imageBookBorrowed)
        var card : CardView = view.findViewById(R.id.borrowedBookCardView)
        var bookStatusBackground : CircleImageView = view.findViewById(R.id.bookStatusBackground)
        val pendingState: ImageView = view.findViewById(R.id.bookPendingWaitingStatus)


    }




    override fun onBindViewHolder(holder: BorrowedBooksViewHolder, position: Int) {
        val book : Book? = books.get(position)
        var user : String? = null
        holder.name.text = book?.title
        holder.author.text = book?.author

        holder.bookStatusBackground.visibility = GONE
        holder.pendingState.visibility = GONE

        val db : FirebaseDatabase? = FirebaseDatabase.getInstance()
        val dbRef : DatabaseReference? = db?.reference?.child("bookID")?.child(book?.bookId!!)?.child("uid")
        var listener : ValueEventListener? = null

        listener = object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                user = p0.getValue(String::class.java)

            }

            override fun onCancelled(p0: DatabaseError) {

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

        when(book?.status){
            "pending" -> {
                holder.bookStatusBackground.visibility = VISIBLE
                holder.pendingState.visibility = VISIBLE
                holder.card.setCardBackgroundColor(Color.parseColor("#90caf9"))
            }
            "booked" ->{
                holder.bookStatusBackground.visibility = GONE
                holder.pendingState.visibility = GONE
                holder.card.setCardBackgroundColor(Color.parseColor("#ffffff"))
            }
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

            //read the flag "reviewed" on the book to check if to put the status to free or to returning

            //change the book status
            val ref = FirebaseDatabase.getInstance().reference.child("bookList").child(user!!).child(book.bookId).child("reviewed");

            val bookStatusListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //get the reviewed status
                    val reviewed = dataSnapshot.value
                    if(reviewed!!.equals("true")){
                        //set the status to free

                        //set book to free
                        ref.parent!!.child("status").setValue("free")
                        //remove book from booked books


                        val refBookId = FirebaseDatabase.getInstance().getReference("bookID")
                        refBookId.child(book.bookId).child("status").setValue("free")

                    }else if (reviewed!!.equals("false")){
                        //set status to returning

                        //set book to returning
                        ref.parent!!.child("status").setValue("returning")
                        //remove book from booked books


                        val refBookId = FirebaseDatabase.getInstance().getReference("bookID")
                        refBookId.child(book.bookId).child("status").setValue("returning")
                    }


                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            }
            ref.addListenerForSingleValueEvent(bookStatusListener)

            val ref2 = FirebaseDatabase.getInstance().reference.child("borrowedBooks").child(fbUser!!.uid).child(book.bookId)
            ref2.removeValue()




            val intent = Intent(c, ReviewActivity::class.java)
            intent.putExtra("uid", user);
            c.startActivity(intent)
            customDialog.dismiss()


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
            customDialog.dismiss()
        })

        //book status = booked; deleted all requests for this book
        btn_recieved_returned.setOnClickListener(View.OnClickListener {
            val fbUser = FirebaseAuth.getInstance().currentUser

            //change the book status
            val ref = FirebaseDatabase.getInstance().reference.child("bookList").child(user!!).child(book!!.bookId)
            val ref2 = FirebaseDatabase.getInstance().reference.child("borrowedBooks").child(fbUser!!.uid).child(book.bookId).child("status")


            //send notification to all other users that the book isn't available
            val bookTitleListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //get the users that were in the waiting list
                    for(waitingUsers in dataSnapshot.children){
                        if(!waitingUsers.value!!.equals(fbUser.uid)){
                            sendNotification(waitingUsers.value!!, book.title)
                        }
                    }

                    ref.child("requests").removeValue();
                    ref.child("status").setValue("booked")
                    ref2.setValue("booked")
                    //TODO: fix this
                    val refBookId = FirebaseDatabase.getInstance().getReference("bookID")
                    refBookId.child(book.bookId).child("status").setValue("booked")


                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            }
            ref.child("requests").addListenerForSingleValueEvent(bookTitleListener)
            customDialog.dismiss()


        })

        customDialog.show();

    }

    private fun sendNotification(waitingUser: Any, bookTitle: String) {
        val thread = Thread(Runnable {
            try {

                val url = URL("https://fcm.googleapis.com/fcm/send")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8")
                conn.setRequestProperty("Authorization", "key=" + Constants.SERVER_KEY)
                conn.setRequestProperty("Accept", "application/json")
                conn.doOutput = true
                conn.doInput = true

                val jsonParam = JSONObject()
                val jsonParam2 = JSONObject()
                jsonParam2.put("body",  bookTitle + " " + c.getString(R.string.requestCancelledBody))
                jsonParam2.put("title", c.getString(R.string.requestCancelledTitle))
                jsonParam2.put("tag", Constants.NOTIFICATION_TAG)
                jsonParam2.put("bookTitle", bookTitle)
                jsonParam2.put("type", Constants.REQUESTCANCELLED)
                jsonParam.put("data", jsonParam2)
                jsonParam.put("to", "/topics/" + waitingUser)

                /*jsonParam3.put("body", msg);
                    jsonParam3.put("title", "testTitle");
                    jsonParam2.put("topic", user);
                    jsonParam2.put("notification", jsonParam3);
                    jsonParam.put("message", jsonParam2);*/


                Log.i("JSON", jsonParam.toString())
                val os = DataOutputStream(conn.outputStream)
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                os.writeBytes(jsonParam.toString())

                os.flush()
                os.close()

                Log.i("STATUS", conn.responseCode.toString())
                Log.i("MSG", conn.responseMessage)

                conn.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        thread.start()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BorrowedBooksViewHolder {
        var v : View = LayoutInflater.from(parent.context).inflate(R.layout.element_card_view_borrowed_books, parent, false)
        return BorrowedBooksViewHolder(v)
    }




    override fun getItemCount(): Int {
        return books.size
    }
}