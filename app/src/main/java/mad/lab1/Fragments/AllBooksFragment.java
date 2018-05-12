package mad.lab1.Fragments;

import android.app.Activity;
import android.app.Dialog;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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
import mad.lab1.Map.MapsActivity;
import mad.lab1.R;

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

    private ArrayList<Book> allBookList;


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


        allBookList = new ArrayList<>();
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
                allBookList.add(b);
                adapter.notifyItemInserted(allBookList.indexOf(b));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int i = 0;
                Book b = dataSnapshot.getValue(Book.class);
                while (allBookList.get(i).getBookId() != b.getBookId()){
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
        fab.setVisibility(View.GONE);

        fab.setOnClickListener(view -> {


            // custom dialog
            final Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.isbn_input_layout);

            // set the custom dialog components - text, image and button
            ImageButton btn_camera = dialog.findViewById(R.id.btn_camera);
            ImageButton btn_manual = dialog.findViewById(R.id.btn_manual);

            btn_camera.setOnClickListener(view1 -> {
                dialog.dismiss();

                IntentIntegrator.forSupportFragment(AllBooksFragment.this)
                        .setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES)
                        .setOrientationLocked(false)
                        .setBeepEnabled(false)
                        .initiateScan();
            });

            btn_manual.setOnClickListener(view1 -> {
                dialog.dismiss();

                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(getContext());
                View promptsView = li.inflate(R.layout.isbn_manual_input_layout, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                EditText txt_isbn = promptsView.findViewById(R.id.txt_isbn);

                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.ok), (dialog2, id) -> {
                            GetBookInfo getBookInfo = new GetBookInfo();
                            getBookInfo.execute(txt_isbn.getText().toString());
                        })
                        .setNegativeButton(getString(R.string.cancel), (dialog2, id) -> dialog2.cancel())
                        .setTitle(R.string.insertISBN);

                //create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                //show it
                alertDialog.show();

            });

            dialog.show();

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

        if(requestCode == IntentIntegrator.REQUEST_CODE) {
            //result from zxing
            if (data != null) {
                IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                String scanContent = scanningResult.getContents().toString();
                //txt_isbn.setText(scanContent);

                GetBookInfo getBookInfo = new GetBookInfo();
                getBookInfo.execute(scanContent);
            }
        }else if(requestCode == 1 && resultCode == Activity.RESULT_OK){

            String condition = data.getStringExtra("condition");


            // upload book to firebase
            IsbnInfo isbnInfo = new IsbnInfo(
                    isbn,
                    title,
                    author,
                    publisher,
                    pubYear,
                    description,
                    null);
            IsbnDB.setBook(isbnInfo);

            BookTitleInfo bookTitleInfo = new BookTitleInfo(
                    title,
                    isbn);
            BookTitleDB.setBook(bookTitleInfo);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


            BookIdInfo bookIdInfo = new BookIdInfo(
                    user.getUid(),
                    isbn,
                    title,
                    author,
                    "free",
                    condition,
                    publisher,
                    pubYear,
                    description
            );
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("bookID");

            // read the bookId key and save bookId Info
            bookID = ref.push().getKey();
            ref.child(bookID).setValue(bookIdInfo);


            //generate bookList
            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("bookList");

            ref2.child(user.getUid())
                    .child(bookID)
                    .setValue(bookIdInfo);

            //get the thumbnail

            GetBookThumb getBookThumb = new GetBookThumb();
            getBookThumb.execute(imageLinks);

        }
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

    private class GetBookInfo extends AsyncTask<String, Object, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... isbns) {

            // Stop if cancelled
            if(isCancelled()){
                return null;
            }

            String apiUrlString = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbns[0];
            try{
                HttpURLConnection connection = null;
                // Build Connection.
                try{
                    URL url = new URL(apiUrlString);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(5000); // 5 seconds
                    connection.setConnectTimeout(5000); // 5 seconds
                } catch (MalformedURLException e) {
                    // Impossible: The only two URLs used in the app are taken from string resources.
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    // Impossible: "GET" is a perfectly valid request method.
                    e.printStackTrace();
                }
                int responseCode = connection.getResponseCode();
                if(responseCode != 200){
                    Log.w(getClass().getName(), "GoogleBooksAPI request failed. Response Code: " + responseCode);
                    connection.disconnect();
                    return null;
                }

                // Read data from response.
                StringBuilder builder = new StringBuilder();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = responseReader.readLine();
                while (line != null){
                    builder.append(line);
                    line = responseReader.readLine();
                }
                String responseString = builder.toString();
                Log.d(getClass().getName(), "Response String: " + responseString);
                JSONObject responseJson = new JSONObject(responseString);
                // Close connection and return response code.
                connection.disconnect();
                return responseJson;
            } catch (SocketTimeoutException e) {
                Log.w(getClass().getName(), "Connection timed out. Returning null");
                return null;
            } catch(IOException e){
                Log.d(getClass().getName(), "IOException when connecting to Google Books API.");
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                Log.d(getClass().getName(), "JSONException when connecting to Google Books API.");
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {

                JSONArray items = jsonObject.getJSONArray("items");
                //get 1st item
                JSONObject item = items.getJSONObject(0);

                JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                JSONArray authors = volumeInfo.getJSONArray("authors");
                JSONObject imageLinksJSON = volumeInfo.getJSONObject("imageLinks");
                imageLinks = imageLinksJSON.getString("thumbnail");
                JSONArray industryIdentifiers = volumeInfo.getJSONArray("industryIdentifiers");

                isbn = industryIdentifiers.getJSONObject(0).getString("identifier");
                author = authors.getString(0);
                title = volumeInfo.getString("title");
                description = volumeInfo.getString("description");
                publisher = volumeInfo.getString("publisher");
                pubYear = volumeInfo.getString("publishedDate");

                Intent i = new Intent(getActivity(), AddingBookActivity.class);
                i.putExtra("author",author);
                i.putExtra("description",description);
                i.putExtra("title",title);
                startActivityForResult(i, 1);


            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), getContext().getString(R.string.uploadError), Toast.LENGTH_SHORT);
            }
        }
    }

    private class GetBookThumb extends AsyncTask<String, Void, String> {
        private Bitmap thumbImg;

        @Override
        protected String doInBackground(String... thumbURLs) {

            try{
                //try to download
                URL thumbURL = new URL(thumbURLs[0]);
                URLConnection thumbConn = thumbURL.openConnection();
                thumbConn.connect();
                InputStream thumbIn = thumbConn.getInputStream();
                BufferedInputStream thumbBuff = new BufferedInputStream(thumbIn);
                thumbImg = BitmapFactory.decodeStream(thumbBuff);
                thumbBuff.close();
                thumbIn.close();

            }
            catch(Exception e) {
                e.printStackTrace();
            }

            return "";
        }

        protected void onPostExecute(String result) {

            //save the thumbnail to firebase

            ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
            thumbImg.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
            thumbImg.recycle();
            byte[] byteArray = bYtE.toByteArray();
            /*String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("isbn");
            ref.child(isbn).child("encodedThumbnail").setValue(encodedImage);

            ref = FirebaseDatabase.getInstance().getReference("bookID");
            ref.child(bookID).child("encodedThumbnail").setValue(encodedImage);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            ref = FirebaseDatabase.getInstance().getReference("bookList");
            ref.child(user.getUid()).child(bookID).child("encodedThumbnail").setValue(encodedImage);*/

            //save the thumbnail in FirebaseStorage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child("thumbnails/"+ isbn +".png");
            UploadTask uploadTask = imageRef.putBytes(byteArray);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //save the URL for the thumbnail on the database
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("isbn");
                    ref.child(isbn).child("thumbURL").setValue(downloadUrl.toString());

                    ref = FirebaseDatabase.getInstance().getReference("bookID");
                    ref.child(bookID).child("thumbURL").setValue(downloadUrl.toString());

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    ref = FirebaseDatabase.getInstance().getReference("bookList");
                    ref.child(user.getUid()).child(bookID).child("thumbURL").setValue(downloadUrl.toString());
                }
            });


            //StorageDB.putProfilePic(getImageUri(getActivity(), thumbImg).toString());
            //thumbView.setImageBitmap(thumbImg);
        }

    }


    @Override
    public void onPause() {
        super.onPause();
        //Remove childEventListener
        dbRef.removeEventListener(bookIDListener);
        int size = allBookList.size();
        allBookList.clear();
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
