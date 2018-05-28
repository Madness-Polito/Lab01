package mad.lab1.Fragments;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import mad.lab1.AllRequestsBookList;
import mad.lab1.Database.Book;
import mad.lab1.R;
import mad.lab1.User.Authentication;

public class MyLibraryListAdapter extends RecyclerView.Adapter<MyLibraryListAdapter.MyLibraryListViewHolder> {



    private Context context;


    public static class MyLibraryListViewHolder extends RecyclerView.ViewHolder{

        private String status;
        private Book b;
        private TextView titleText;
        private TextView authorText;
        private ImageView image;
        private CardView card;
        private TextView bookRequestCounter;
        private de.hdodenhof.circleimageview.CircleImageView bookRequestCounterBackground;

        //Constructor
        public MyLibraryListViewHolder(View v){
            super(v);
            card = (CardView) v;
            titleText = v.findViewById(R.id.titleBookTextViewMyLibrary);
            authorText = v.findViewById(R.id.authorBookTextViewMyLibrary);
            image = v.findViewById(R.id.imageBookMyLibrary);
            bookRequestCounter = v.findViewById(R.id.newBookRequestCountMyLibrary);
            bookRequestCounterBackground = v.findViewById(R.id.newBookRequestCountBackgroundMyLibrary);
        }

    }


    private ArrayList<Book> allSharedBooks;

    //Constructor
    public MyLibraryListAdapter(ArrayList<Book> allSharedBooks, Context context){
        this.allSharedBooks = allSharedBooks;
        this.context = context;
    }

    @NonNull
    @Override
    public MyLibraryListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_card_view_my_library, parent, false);
        MyLibraryListViewHolder holder = new MyLibraryListViewHolder(v);
        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull MyLibraryListViewHolder holder, int position) {
        holder.titleText.setText(allSharedBooks.get(position).getTitle());
        holder.authorText.setText(allSharedBooks.get(position).getAuthor());
        //holder.image.setImageBitmap(allSharedBooks.get(position).getDecodedThumbnail());
        Glide.with(holder.image.getContext())
                .load(allSharedBooks.get(position).getThumbURL())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.my_library_selected_24dp)
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform())
                .into(holder.image);

        /*
        * Listener on card based on the status of the book
        * FREE && counter > 0
        *   Open list of user
        * PENDING
        *   Dialog, possible to remove request
        * BOOKED
        *   Write a review of the user or modify an existing one
        *
        * */




        holder.b = allSharedBooks.get(position);
        holder.status = holder.b.getStatus();
        setBookCounter(holder, allSharedBooks.get(position));


        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: based on the book status, it will trigger different actions

                switch(holder.status){
                    case "free":
                        Intent i = new Intent(context, AllRequestsBookList.class);
                        context.startActivity(i);
                        break;
                    case "pending":
                        Toast.makeText(context, "Pending", Toast.LENGTH_SHORT).show();
                        break;
                    case "booked":
                        break;
                }
            }
        });



    }



    @Override
    public int getItemCount() {
        return allSharedBooks.size();
    }


    private void setBookCounter(MyLibraryListViewHolder holder, Book b){
        //TODO this function should retrieve the number of request from firebase and return the corresponding constant


        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference requestBookReference = db.getReference().child("bookList")
                .child(Authentication.getCurrentUser().getUid())
                .child(b.getBookId()).child("numRequests");

        ValueEventListener requestBookListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer requestCounter = dataSnapshot.getValue(Integer.class);

                if(requestCounter != null){
                    if (requestCounter > 0) {
                        //New requests, show the number
                        holder.bookRequestCounter.setVisibility(View.VISIBLE);
                        holder.bookRequestCounterBackground.setVisibility(View.VISIBLE);
                        holder.bookRequestCounter.setText(requestCounter.toString());

                        // play shake animation
                        Animation animation;
                        animation = AnimationUtils.loadAnimation(context,R.anim.shake_animation);
                        holder.bookRequestCounterBackground.startAnimation(animation);
                        holder.bookRequestCounter.startAnimation(animation);

                    }else {
                        //No new requests, hide the number
                        holder.bookRequestCounter.setVisibility(View.GONE);
                        holder.bookRequestCounterBackground.setVisibility(View.GONE);

                    }
                }
                else {
                    //No new requests, hide the number
                    holder.bookRequestCounter.setVisibility(View.GONE);
                    holder.bookRequestCounterBackground.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        switch(holder.status){
            case "free":
                requestBookReference.addValueEventListener(requestBookListener);
                break;
            case "pending":
                requestBookReference.addValueEventListener(requestBookListener);
                break;
            case "booked":
                hideBookCounter(holder);
                break;
        }



    }

    private void hideBookCounter(MyLibraryListViewHolder holder){
        holder.bookRequestCounter.setVisibility(View.GONE);
        holder.bookRequestCounterBackground.setVisibility(View.GONE);
    }
}
