package mad.lab1;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import java.util.List;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;

public class IsbnDB {

    private static final String ISBN = "isbn";

    private static DatabaseReference getIsbnRef(){
        return FirebaseDatabase.getInstance()
                .getReference()
                .child(ISBN);
    }

    // retrieves data of the book identified by isbn from the db
    public static void setBook(IsbnInfo isbnInfo){

        getIsbnRef()
                .child(isbnInfo.getIsbn())
                .setValue(isbnInfo);

    }


    // reads the data of a generic book identified by its isbn
    public static void getBook(String isbn, ValueEventListener listener){

        getIsbnRef()
                .child(ISBN)
                .child(isbn)
                .addListenerForSingleValueEvent(listener);
    }

    // download the books and stores them locally to speed-up the view process
    public static void getIsbnList(Context c){

        DatabaseReference dbRef = getIsbnRef();
        System.out.println("----------> getIsbnList");

        // download all books identified by isbn
        dbRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Log.e("Count " ,"" + dataSnapshot.getChildrenCount());
                        List<IsbnInfo> isbnList = new ArrayList<>();

                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            IsbnInfo isbn = postSnapshot.getValue(IsbnInfo.class);
                            isbnList.add(isbn);
                        }

                        // save downloaded books locally
                        LocalDB.putIsbnList(c, isbnList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("----------->getIsbnList error");
                    }
                });
    }
}
