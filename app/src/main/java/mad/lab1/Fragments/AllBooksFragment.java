package mad.lab1.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
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
import java.util.List;

import mad.lab1.AddingBookActivity;
import mad.lab1.CustomTextWatcher;
import mad.lab1.Database.Book;
import mad.lab1.Database.BookIdInfo;
import mad.lab1.Database.BookTitleDB;
import mad.lab1.Database.BookTitleInfo;
import mad.lab1.Database.IsbnDB;
import mad.lab1.Database.IsbnInfo;
import mad.lab1.Map.MapsActivity;
import mad.lab1.R;
import mad.lab1.TextValidation;

import static android.content.ContentValues.TAG;

public class AllBooksFragment extends Fragment {

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

    private ArrayList<Book> allBookListCopy; //needed to not lose data when filtering
    private ArrayList<Book> allBookList;

    private Boolean filterApplied = false;
    private ArrayList<String> categories;


    private DatabaseReference dbRef;
    private RecyclerView cardViewList;
    private LinearLayoutManager layoutManager;
    private AllBooksListAdapter adapter;
    private ChildEventListener bookIDListener;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //initialize db

        dbRef = FirebaseDatabase.getInstance().getReference().child("isbn");

        categories = new ArrayList<>();
        allBookList = new ArrayList<>();
        allBookListCopy = new ArrayList<>();
        adapter = new AllBooksListAdapter(allBookList, new AllBooksListAdapter.OnBookClicked() {
            @Override
            public void onBookClicked(Book b) {
                //create a dialog fragment that shows all the informatio related to the book selected
                //Toast.makeText(getContext(), b.getTitle(), Toast.LENGTH_SHORT).show();
                /*
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                //Checking if previous dialog are active
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                ShowSelelctedBookInfoDialogFragment newFragment = ShowSelelctedBookInfoDialogFragment.newInstance(b);
                newFragment.show(ft, "dialog");
                */
                Bundle arg = new Bundle();
                arg.putParcelable("book", b);
                Intent i = new Intent(getContext(), ShowSelectedBookInfo.class);
                i.putExtra("argument", b);
                startActivity(i);
            }
        });

        bookIDListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Book b = dataSnapshot.getValue(Book.class);
                //create a list of different categories of books to allow for filtering
                if(b.getCategory() != null && !categories.contains(b.getCategory()) && b.getCategory() != ""){
                    categories.add(b.getCategory());
                }
                allBookList.add(b);
                allBookListCopy.add(b);
                adapter.notifyItemInserted(allBookList.indexOf(b));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(!filterApplied) {
                    int i = 0;
                    Book b = dataSnapshot.getValue(Book.class);
                    while (allBookList.get(i).getBookId() != b.getBookId()) {
                        i++;
                    }
                    allBookList.set(i, b);
                    allBookListCopy.set(i, b);
                    adapter.notifyItemChanged(i);
                }

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Inflating the layout for the fragment
        View v = inflater.inflate(R.layout.all_books_fragment_layout, container, false);

        map = v.findViewById(R.id.showMapActionButton);
        map.setVisibility(View.GONE);

        map.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), MapsActivity.class);
            startActivity(i);
        });


        fab = v.findViewById(R.id.addBookToShareActionButton);

        fab.setOnClickListener(view -> {

            // get prompts.xml view
            LayoutInflater li = LayoutInflater.from(getContext());
            View promptsView = li.inflate(R.layout.select_category_dialog, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final ListView lsv_categories = promptsView.findViewById(R.id.lsv_categories);
            lsv_categories.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_multiple_choice, categories));



            // set dialog message
            alertDialogBuilder
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.ok),     (dialog, id) -> {})
                    .setNegativeButton(getString(R.string.cancel), (dialog, id) -> dialog.cancel());
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
            // override positive buttton to check data
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view2 ->{
                if(lsv_categories.getCheckedItemCount() > 0){
                    //some categories have been selected, only show the books in those categories.
                    filterApplied = true;
                    //find what categories were chosen, add books in the category to a list
                    allBookList.clear();


                    SparseBooleanArray checkedItems = lsv_categories.getCheckedItemPositions();
                    if (checkedItems != null) {
                        for (int i=0; i<checkedItems.size(); i++) {
                            if (checkedItems.valueAt(i)) {
                                String item = lsv_categories.getAdapter().getItem(checkedItems.keyAt(i)).toString();
                                Log.i(TAG,item + " was selected");
                                for(Book b : allBookListCopy){
                                    //select the given books
                                    if(b.getCategory().equals(item)){
                                        allBookList.add(b);
                                    }
                                }
                            }
                        }
                    }
                    //TODO
                    //adapter.notify();
                    adapter.notifyDataSetChanged();


                }else{
                    filterApplied = false;
                    //regenerate allBookList
                    allBookList.clear();
                    for(Book b : allBookListCopy){
                        allBookList.add(b);
                    }
                    //allBookList = (ArrayList<Book>) allBookListCopy.clone();
                    //TODO
                    //adapter.notify();
                    adapter.notifyDataSetChanged();

                }
                alertDialog.cancel();
            });

        });

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
    public static AllBooksFragment newInstance(int page, String title){

        AllBooksFragment fragment = new AllBooksFragment();
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
        dbRef.removeEventListener(bookIDListener);
        int size = allBookList.size();
        allBookList.clear();
        allBookListCopy.clear();
        adapter.notifyItemRangeRemoved(0, size);
    }

    @Override
    public void onResume() {
        super.onResume();
        //add childEventListener
        dbRef.addChildEventListener(bookIDListener);

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

