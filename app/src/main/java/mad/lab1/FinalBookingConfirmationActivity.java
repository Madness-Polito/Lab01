package mad.lab1;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mad.lab1.Database.Book;
import mad.lab1.Database.UserInfo;
import mad.lab1.Map.MapsActivityFiltered;
import mad.lab1.R;

public class FinalBookingConfirmationActivity extends AppCompatActivity {

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

    final Context context = this;

    private UserInfo bookOwner;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_info_dialog_layout);

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
        Glide.with(bookImageView.getContext())
                .load(book.getThumbURL())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.my_library_selected_24dp)
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform())
                .into(bookImageView);
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
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("borrowedBooks");
            ref.child(user.getUid())
                    .child(book.getBookId())
                    .setValue(book);

            //TODO: Open chat
        });




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

    }
}


