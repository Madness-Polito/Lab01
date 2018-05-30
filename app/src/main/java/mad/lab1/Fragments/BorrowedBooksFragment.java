package mad.lab1.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import mad.lab1.AddingBookActivity;
import mad.lab1.Database.Book;
import mad.lab1.Database.BookIdInfo;
import mad.lab1.Database.BookTitleDB;
import mad.lab1.Database.BookTitleInfo;
import mad.lab1.Database.IsbnDB;
import mad.lab1.Database.IsbnInfo;
import mad.lab1.R;

public class BorrowedBooksFragment extends Fragment {

    private FloatingActionButton map;
    private FloatingActionButton fab;
    private String isbn;
    private String bookID;
    private String title;
    private String author;
    private String publisher;
    private String pubYear;
    private String description;
    private String imageLinks;

    private ArrayList<Book> allBookList;


    private DatabaseReference dbRef;
    private RecyclerView cardViewList;
    private LinearLayoutManager layoutManager;
    private BorrowedBooksListAdapter adapter;
    private ChildEventListener bookIDListener;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize db
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            dbRef = FirebaseDatabase.getInstance().getReference().child("borrowedBooks").child(user.getUid());


            allBookList = new ArrayList<>();
            adapter = new BorrowedBooksListAdapter(allBookList, getContext());

            bookIDListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Book b = dataSnapshot.getValue(Book.class);
                    allBookList.add(b);
                    adapter.notifyItemInserted(allBookList.indexOf(b));
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    int i = 0;
                    Book b = dataSnapshot.getValue(Book.class);
                    while (allBookList.get(i).getBookId() != b.getBookId()) {
                        i++;
                    }
                    allBookList.set(i, b);
                    adapter.notifyItemChanged(i);

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    //Toast.makeText(getActivity(), "Failed to load book list.",
                    //      Toast.LENGTH_SHORT).show();
                }
            };

        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Inflating the layout for the fragment
        View v = inflater.inflate(R.layout.all_books_fragment_layout, container, false);

        map = v.findViewById(R.id.showMapActionButton);
        map.setVisibility(View.GONE);


        fab = v.findViewById(R.id.addBookToShareActionButton);
        fab.setVisibility(View.GONE);

        cardViewList = v.findViewById(R.id.recyclerViewAllBooks);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        cardViewList.setLayoutManager(layoutManager);
        cardViewList.setItemAnimator(new DefaultItemAnimator());




        cardViewList.setAdapter(adapter);

        return v;

    }

    @Override
    public void onStart(){
        super.onStart();

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }


    //Used by adapter to get an instance of this type of fragment. Possible to pass arguments via a bundle
    public static BorrowedBooksFragment newInstance(int page, String title){

        BorrowedBooksFragment fragment = new BorrowedBooksFragment();
        Bundle arg = new Bundle();
        arg.putString("title", title);
        arg.putInt("page", page);
        fragment.setArguments(arg);
        return fragment;
    }



    @Override
    public void onPause() {
        super.onPause();
        //Remove childEventListener
        if(dbRef != null) {
            dbRef.removeEventListener(bookIDListener);
            int size = allBookList.size();
            allBookList.clear();
            adapter.notifyItemRangeRemoved(0, size);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //add childEventListener
        if(bookIDListener != null) {
            dbRef.addChildEventListener(bookIDListener);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {


        savedInstanceState.putString("isbn", isbn);
        savedInstanceState.putString("bookID", bookID);
        savedInstanceState.putString("title", title);
        savedInstanceState.putString("author", author);
        savedInstanceState.putString("publisher", publisher);
        savedInstanceState.putString("pubYear", pubYear);
        savedInstanceState.putString("description", description);
        savedInstanceState.putString("imageLinks", imageLinks);



        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            isbn = savedInstanceState.getString("isbn");
            bookID = savedInstanceState.getString("bookID");
            title = savedInstanceState.getString("title");
            author = savedInstanceState.getString("author");
            publisher = savedInstanceState.getString("publisher");
            pubYear = savedInstanceState.getString("pubYear");
            description = savedInstanceState.getString("description");
            imageLinks = savedInstanceState.getString("imageLinks");
        }
    }
}

