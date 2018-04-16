package mad.lab1;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class IsbnDB {

    private static final String ISBN = "isbn";

    // retrieves data of the book identified by isbn from the db
    public static void setBook(IsbnInfo isbnInfo){

        FirebaseDatabase.getInstance()
                .getReference()
                .child(ISBN)
                .child(isbnInfo.getIsbn())
                .setValue(isbnInfo);

    }


    // reads the data of a generic book identified by its isbn
    public static void getBook(String isbn, ValueEventListener listener){

        FirebaseDatabase.getInstance()
                .getReference()
                .child(ISBN)
                .child(isbn)
                .addListenerForSingleValueEvent(listener);
    }
}
