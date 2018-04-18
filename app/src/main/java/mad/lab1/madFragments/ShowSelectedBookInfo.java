package mad.lab1.madFragments;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import mad.lab1.Book;
import mad.lab1.R;

public class ShowSelectedBookInfo extends AppCompatActivity {

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_info_dialog_layout);

        Intent i = getIntent();
        //Bundle b = i.getBundleExtra("argument");
        //Book book = b.getParcelable("book");
        Book book = i.getParcelableExtra("argument");


        initialization();

        titleTextView.setText(book.getTitle());
        authorTextView.setText(book.getAuthor());
        bookImageView.setImageBitmap(book.getDecodedThumbnail());
        publisherTextView.setText(book.getPublisher());
        publicationYearTextView.setText(book.getPubYear());
        conditionTextView.setText(book.getCondition());
        isbnTextView.setText("ISBN: " + book.getIsbn());
        descriptionTextView.setText(book.getDescription());
        
        descriptionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: ADD DIALOG WITH COMPLETE DESCRIPTION
                Toast.makeText(ShowSelectedBookInfo.this, "Full Description", Toast.LENGTH_SHORT).show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShowSelectedBookInfo.this, "Book borrowed", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
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
