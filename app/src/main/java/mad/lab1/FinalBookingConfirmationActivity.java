package mad.lab1;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;
import mad.lab1.Database.Book;
import mad.lab1.Database.ChatInfo;
import mad.lab1.Database.UserInfo;
import mad.lab1.Map.MapsActivityFiltered;
import mad.lab1.Notifications.Constants;
import mad.lab1.R;
import mad.lab1.chat.Chat;
import mad.lab1.chat.ChatActivity;
import mad.lab1.review.ReviewsActivity;

public class FinalBookingConfirmationActivity extends AppCompatActivity {


    private final int BOOK_SELECTED_CODE = 3;

    private Toolbar toolbar;
    private TextView titleTextView;
    private TextView authorTextView;
    private ImageView bookImageView;
    private TextView publisherTextView;
    private TextView publicationYearTextView;
    private TextView isbnTextView;
    private TextView conditionTextView;
    private TextView descriptionTextView;
    private FloatingActionButton fab;

    private CircleImageView ownerImage;
    private TextView ownerName;
    private TextView ownerCity;
    private RatingBar ownerRating;
    private CardView ownerCardView;


    final Context context = this;

    private UserInfo bookOwner;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_book_confirmation);

        Intent i = getIntent();
        //Bundle b = i.getBundleExtra("argument");
        //Book book = b.getParcelable("book");
        Bundle bundle = i.getParcelableExtra("argument");
        Book book = bundle.getParcelable("book");
        bookOwner = bundle.getParcelable("user");


        initialization();

        titleTextView.setText(book.getTitle());
        authorTextView.setText(book.getAuthor());
        //bookImageView.setImageBitmap(book.getDecodedThumbnail());
        fillImageView(book);
        publisherTextView.setText(book.getPublisher());
        publicationYearTextView.setText(book.getPubYear());
        conditionTextView.setText(book.getCondition());
        isbnTextView.setText("ISBN: " + book.getIsbn());
        descriptionTextView.setText(book.getDescription());

        descriptionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.description_layout);

                // set the custom dialog components - text, image and button
                TextView txt_description = dialog.findViewById(R.id.txt_description);

                txt_description.setText(book.getDescription());

                dialog.show();

            }
        });

        fab.setOnClickListener(view -> {

            //add the selected book to the list of borrowed books
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


            //add the user to the list of users asking for this book

            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("bookList");

            ref.child(bookOwner.getUid())
                    .child(book.getBookId())
                    .child("requests")
                    .child(user.getUid())
                    .setValue(user.getUid());




            //TODO: close this activity

            sendNotification(book, bookOwner);

            //open the chat interface with the book owner
            /*ChatInfo c = new ChatInfo(0, bookOwner.getUid());
            Intent intent = new Intent(getBaseContext(), ChatActivity.class);
            Bundle b = new Bundle();
            b.putParcelable("chat", c);
            intent.putExtra("chatInfo", b);
            startActivity(intent);*/

            setResult(RESULT_OK);
            Toast.makeText(this, "Request sent", Toast.LENGTH_SHORT).show();
            finish();


        });




    }

    private void fillImageView(Book book) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("isbn").child(book.getIsbn()).child("thumbURL");

        ValueEventListener bookImageListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get the user to review
                String thumbnail = dataSnapshot.getValue().toString();
                Glide.with(bookImageView.getContext())
                        .load(thumbnail)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.my_library_selected_24dp)
                                .centerCrop()
                                .dontAnimate()
                                .dontTransform())
                        .into(bookImageView);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.addListenerForSingleValueEvent(bookImageListener);




    }

    private void sendNotification(Book book, UserInfo bookOwner) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL("https://fcm.googleapis.com/fcm/send");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Authorization", "key=" + Constants.SERVER_KEY);
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    JSONObject jsonParam2 =  new JSONObject();
                    jsonParam2.put("body", getString(R.string.requestBody) + " " + book.getTitle());
                    jsonParam2.put("title", getString(R.string.requestTitle));
                    jsonParam2.put("tag", Constants.NOTIFICATION_TAG);
                    jsonParam2.put("bookTitle", book.getTitle());
                    jsonParam2.put("type", Constants.NEWBOOKING);
                    jsonParam.put("data", jsonParam2);
                    jsonParam.put("to", "/topics/" + bookOwner.getUid());

                    /*jsonParam3.put("body", msg);
                    jsonParam3.put("title", "testTitle");
                    jsonParam2.put("topic", user);
                    jsonParam2.put("notification", jsonParam3);
                    jsonParam.put("message", jsonParam2);*/


                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }

    private void initialization(){
        toolbar = findViewById(R.id.showBookInfoToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Implemented by activity
            }
        });

        titleTextView = findViewById(R.id.showBookInfoTitle);
        authorTextView = findViewById(R.id.showBookInfoAuthor);
        bookImageView = findViewById(R.id.showBookInfoImage);
        publisherTextView = findViewById(R.id.showBookInfoPublisher);
        publicationYearTextView = findViewById(R.id.showBookInfoPublicationYear);
        isbnTextView = findViewById(R.id.showBookInfoISBN);
        conditionTextView = findViewById(R.id.showBookInfoCondition);
        descriptionTextView = findViewById(R.id.showBookInfoDescription);
        fab = findViewById(R.id.showBookInfoFab);



        ownerImage = findViewById(R.id.ownerProfileImageView);
        ownerName = findViewById(R.id.ownerProfileName);
        ownerCity = findViewById(R.id.ownerProfileCity);
        ownerRating = findViewById(R.id.owner_rating_bar);
        ownerCardView = findViewById(R.id.ownerProfileCardView);

        ownerCity.setText(bookOwner.getCity());
        ownerName.setText(bookOwner.getName());


        // set rating stars

        DatabaseReference reviewRef = FirebaseDatabase.getInstance()
                .getReference("reviews")
                .child(bookOwner.getUid());
        reviewRef.child("totStarCount")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        Float totStarCount = dataSnapshot.getValue(Float.class);

                        // dowload numReviews
                        reviewRef.child("reviewCount")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        Float reviewCount = dataSnapshot.getValue(Float.class);
                                        if(reviewCount == null){
                                            reviewCount = 0f;
                                        }
                                        if(reviewCount != 0f ){
                                            ownerRating.setRating(totStarCount / reviewCount);
                                        }else{
                                            ownerRating.setRating(0);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );


        ownerCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ReviewsActivity.class);
                i.putExtra("uid", bookOwner.getUid());
                startActivity(i);
            }
        });

    }
}


