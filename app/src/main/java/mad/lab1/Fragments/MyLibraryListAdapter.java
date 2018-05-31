package mad.lab1.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import mad.lab1.Database.Book;
import mad.lab1.R;
import mad.lab1.User.Authentication;
import mad.lab1.review.ReviewActivity;

public class MyLibraryListAdapter extends RecyclerView.Adapter<MyLibraryListAdapter.MyLibraryListViewHolder> {



    private Context context;
    private final int SHOW_REQUESTS_CODE = 1;
    private OnBookClickedMyLibrary listener;


    public interface OnBookClickedMyLibrary {
        void onBookClickedMyLibrary(Book b);
    }


    public static class MyLibraryListViewHolder extends RecyclerView.ViewHolder{

        private String status;
        private Book b;
        private TextView titleText;
        private TextView authorText;
        private ImageView image;
        private CardView card;
        private TextView bookRequestCounter;
        private de.hdodenhof.circleimageview.CircleImageView bookRequestCounterBackground;
        private ImageView bookedState;
        private ImageView returningState;
        private ImageView pendingState;


        //Constructor
        public MyLibraryListViewHolder(View v){
            super(v);
            card = (CardView) v;
            titleText = v.findViewById(R.id.titleBookTextViewMyLibrary);
            authorText = v.findViewById(R.id.authorBookTextViewMyLibrary);
            image = v.findViewById(R.id.imageBookMyLibrary);
            bookRequestCounter = v.findViewById(R.id.newBookRequestCountMyLibrary);
            bookRequestCounterBackground = v.findViewById(R.id.newBookRequestCountBackgroundMyLibrary);
            bookedState = v.findViewById(R.id.bookBookedState);
            returningState = v.findViewById(R.id.bookReturningState);
            pendingState = v.findViewById(R.id.bookPendingState);

            bookedState.setVisibility(View.GONE);
            returningState.setVisibility(View.GONE);
            pendingState.setVisibility(View.GONE);

        }

    }


    private ArrayList<Book> allSharedBooks;

    //Constructor
    public MyLibraryListAdapter(ArrayList<Book> allSharedBooks, Context context, OnBookClickedMyLibrary listener){
        this.allSharedBooks = allSharedBooks;
        this.context = context;
        this.listener = listener;
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
                        listener.onBookClickedMyLibrary(holder.b);
                        break;
                    case "pending":
                        openPendingDialog(holder);
                        break;
                    case "booked":
                        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                        //set the flag "reviewed" to true. If the flag is already on true, don't ask for a new review
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("bookList")
                                .child(fbUser.getUid())
                                .child(holder.b.getBookId())
                                .child("reviewed");

                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String reviewed = dataSnapshot.getValue().toString();
                                if(reviewed.equals("false")){
                                    openBookedDialog(holder, "booked");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        break;
                    case "returning":
                        openBookedDialog(holder, "returning");
                        break;
                }
            }
        });



    }


    private void openBookedDialog(MyLibraryListViewHolder holder, String status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.reviewTitle);
        builder.setMessage(R.string.reviewBody);
        builder.setCancelable(true);

        builder.setPositiveButton(
                R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();

                        if(status.equals("booked")){

                            //set the flag "reviewed" to true. If the flag is already on true, don't ask for a new review
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("bookList")
                                    .child(fbUser.getUid())
                                    .child(holder.b.getBookId())
                                    .child("reviewed");
                            ref.setValue("true");
                            startReview(holder);
                            
                        }else if(status.equals("returning")){

                            //set the book to free
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("bookList")
                                    .child(fbUser.getUid())
                                    .child(holder.b.getBookId())
                                    .child("status");
                            ref.setValue("free");
                            ref = FirebaseDatabase.getInstance().getReference("bookID")
                                    .child(holder.b.getBookId())
                                    .child("status");
                            ref.setValue("free");

                            startReview(holder);

                        }


                        dialog.cancel();
                    }
                });

        builder.setNegativeButton(
                R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    private void startReview(MyLibraryListViewHolder holder) {
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();

        //get the user to review
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("bookList").child(fbUser.getUid()).child(holder.b.getBookId()).child("selectedRequest");

        ValueEventListener bookTitleListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get the user to review
                String user = dataSnapshot.getValue().toString();
                Intent intent = new Intent(context, ReviewActivity.class);
                intent.putExtra("uid", user);

                context.startActivity(intent);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.addListenerForSingleValueEvent(bookTitleListener);
    }


    private void openPendingDialog(MyLibraryListViewHolder holder) {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.pending_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);



        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton(context.getString(R.string.yes),     (dialog, id) -> {})
                .setNegativeButton(context.getString(R.string.no), (dialog, id) -> dialog.cancel());
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
        // override positive buttton to check data
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view2 ->{
            removerequest(holder);
            alertDialog.cancel();
        });
    }

    private void removerequest(MyLibraryListViewHolder holder) {
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();

        //get the user to delete
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("bookList").child(fbUser.getUid()).child(holder.b.getBookId()).child("selectedRequest");

        ValueEventListener bookTitleListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get the user to remove
                String user = dataSnapshot.getValue().toString();
                //remove the user's request from the database
                dataSnapshot.getRef().removeValue();
                dataSnapshot.getRef().getParent().child("requests").child(user).removeValue();
                //set book status to free
                DatabaseReference ref2 = FirebaseDatabase.getInstance()
                        .getReference("bookList")
                        .child(fbUser.getUid())
                        .child(holder.b.getBookId())
                        .child("status");

                ref2.setValue("free");

                DatabaseReference ref3 = FirebaseDatabase.getInstance()
                        .getReference("borrowedBooks")
                        .child(user)
                        .child(holder.b.getBookId());
                ref3.removeValue();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.addListenerForSingleValueEvent(bookTitleListener);
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
                holder.card.setCardBackgroundColor(Color.parseColor("#ffffff"));
                holder.returningState.setVisibility(View.GONE);
                holder.bookedState.setVisibility(View.GONE);
                holder.pendingState.setVisibility(View.GONE);

                break;
            case "pending":
                requestBookReference.addValueEventListener(requestBookListener);
                holder.returningState.setVisibility(View.GONE);
                holder.pendingState.setVisibility(View.VISIBLE);
                holder.bookedState.setVisibility(View.GONE);
                holder.bookRequestCounterBackground.setVisibility(View.VISIBLE);
                holder.bookRequestCounter.setVisibility(View.GONE);
                holder.card.setCardBackgroundColor(Color.parseColor("#90caf9"));
                break;
            case "booked":
                holder.card.setCardBackgroundColor(Color.parseColor("#ffe082"));
                holder.returningState.setVisibility(View.GONE);
                holder.bookedState.setVisibility(View.VISIBLE);
                holder.pendingState.setVisibility(View.GONE);
                holder.bookRequestCounterBackground.setVisibility(View.VISIBLE);
                holder.bookRequestCounter.setVisibility(View.GONE);
                break;
            case "returning":
                holder.card.setCardBackgroundColor(Color.parseColor("#bcaaa4"));
                holder.returningState.setVisibility(View.VISIBLE);
                holder.bookedState.setVisibility(View.GONE);
                holder.pendingState.setVisibility(View.GONE);
                holder.bookRequestCounter.setVisibility(View.GONE);
                holder.bookRequestCounterBackground.setVisibility(View.VISIBLE);
                break;
        }



    }


}
