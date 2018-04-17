package mad.lab1.madFragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import mad.lab1.Book;
import mad.lab1.BookIdInfo;
import mad.lab1.IsbnDB;
import mad.lab1.IsbnInfo;
import mad.lab1.LocalDB;
import mad.lab1.R;
import mad.lab1.StorageDB;

public class AllBooksFragment extends Fragment {

    private FloatingActionButton fab;
    private ListView lsv_Books;
    private String isbn;
    private String bookID;
    private ArrayList<Book> allBookList;
    private DatabaseReference dbRef;
    FloatingActionButton fab;
    private RecyclerView cardViewList;
    private LinearLayoutManager layoutManager;
    private AllBooksListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Inflating the layout for the fragment
        View v = inflater.inflate(R.layout.all_books_fragment_layout, container, false);

        lsv_Books = v.findViewById(R.id.allBookListView);
        fab = v.findViewById(R.id.addBookToShareActionButton);

        //initialize db
        dbRef = FirebaseDatabase.getInstance().getReference().child("bookID");





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
        cardViewList = v.findViewById(R.id.recyclerViewAllBooks);
        layoutManager = new LinearLayoutManager(getContext());

        cardViewList.setLayoutManager(layoutManager);


        //JUST TO TRY THE ADAPTER
        ArrayList<String> prova = new ArrayList<>();
        prova.add("prova");
        prova.add("prova");
        prova.add("prova");
        prova.add("prova");
        prova.add("prova");
        prova.add("prova");
        prova.add("prova");
        prova.add("prova");
        prova.add("prova");
        prova.add("prova");


        adapter = new AllBooksListAdapter(prova, new AllBooksListAdapter.OnBookClicked() {
            @Override
            public void onBookClicked(String value) {
                //create a dialog fragment that shows all the informatio related to the book selected
                Toast.makeText(getContext(), value, Toast.LENGTH_SHORT).show();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                ShowSelelctedBookInfoDialogFragment newFragment = ShowSelelctedBookInfoDialogFragment.newInstance(value);
                newFragment.show(ft, "dialog");
            }
        });
        cardViewList.setAdapter(adapter);



        fab.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v)
                    {

                        // set dialog message
                        alertDialogBuilder
                                .setCancelable(true)
                                .setPositiveButton(getString(R.string.ok),     (dialog2, id) -> {
                                    GetBookInfo getBookInfo = new GetBookInfo();
                                    getBookInfo.execute(txt_isbn.getText().toString());
                                })
                                .setNegativeButton(getString(R.string.cancel), (dialog2, id) -> dialog2.cancel())
                                .setTitle(R.string.insertISBN);
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();


                    });

                    dialog.show();




                /*
                IntentIntegrator.forSupportFragment(AllBooksFragment.this)
                                    .setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES)
                                    .setOrientationLocked(false)
                                    .setBeepEnabled(false)
                                    .initiateScan();
                 */
        });

        //TODO: CREATE METHODS A ACTIONS FOR THIS FRAGMENT

        //Returning the view to viewPager
        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        ValueEventListener bookIdListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allBookList = new ArrayList<>();
                // Get Post object and use the values to update the UI
                Map<String, Map<String, String>> td = (HashMap<String, Map<String, String>>)dataSnapshot.getValue();

                if(td != null) {
                    for (String bookId : td.keySet()) {

                        Book newBook = new Book(
                                bookId,
                                td.get(bookId).get("isbn"),
                                td.get(bookId).get("title"),
                                td.get(bookId).get("author"),
                                td.get(bookId).get("status"),
                                td.get(bookId).get("condition"),
                                td.get(bookId).get("publisher"),
                                td.get(bookId).get("pubYear"),
                                td.get(bookId).get("encodedThumbnail")
                        );
                        allBookList.add(newBook);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(getActivity(), "Failed to load book list.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        dbRef.addValueEventListener(bookIdListener);


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO: fill in the listview
        if(data != null) {
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            String scanContent = scanningResult.getContents().toString();
            //txt_isbn.setText(scanContent);

            GetBookInfo getBookInfo = new GetBookInfo();
            getBookInfo.execute(scanContent);
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

                String title;
                String author;
                String publisher;
                String pubYear;
                String description;




                JSONArray items = jsonObject.getJSONArray("items");
                //get 1st item
                JSONObject item = items.getJSONObject(0);

                JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                JSONArray authors = volumeInfo.getJSONArray("authors");
                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                JSONArray industryIdentifiers = volumeInfo.getJSONArray("industryIdentifiers");

                GetBookThumb getBookThumb = new GetBookThumb();
                getBookThumb.execute(imageLinks.getString("thumbnail"));

                isbn = industryIdentifiers.getJSONObject(0).getString("identifier");
                author = authors.getString(0);
                title = volumeInfo.getString("title");
                description = volumeInfo.getString("description");
                publisher = volumeInfo.getString("publisher");
                pubYear = volumeInfo.getString("publishedDate");

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

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                //TODO: fix status, condition

                BookIdInfo bookIdInfo = new BookIdInfo(
                        user.getUid(),
                        isbn,
                        title,
                        author,
                        "free",
                        "good",
                        publisher,
                        pubYear
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

            } catch (JSONException e) {
                e.printStackTrace();
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
            String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("isbn");
            ref.child(isbn).child("encodedThumbnail").setValue(encodedImage);

            ref = FirebaseDatabase.getInstance().getReference("bookID");
            ref.child(bookID).child("encodedThumbnail").setValue(encodedImage);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            ref = FirebaseDatabase.getInstance().getReference("bookList");
            ref.child(user.getUid()).child(bookID).child("encodedThumbnail").setValue(encodedImage);


            StorageDB.putProfilePic(getImageUri(getActivity(), thumbImg).toString());
            //thumbView.setImageBitmap(thumbImg);
        }

    }



    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}
