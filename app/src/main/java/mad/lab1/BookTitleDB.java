package mad.lab1;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Matteo on 28/04/2018.
 */

public class BookTitleDB {

    private static final String TITLELIST = "titleList";

    // retrieves data of the book identified by title from the db
    public static void setBook(BookTitleInfo bookTitleInfo){

        FirebaseDatabase.getInstance()
                .getReference()
                .child(TITLELIST)
                .child(bookTitleInfo.getTitle())
                .setValue(bookTitleInfo);

    }


    // reads the isbn of a generic book identified by its title
    public static void getBook(String title, ValueEventListener listener){

        FirebaseDatabase.getInstance()
                .getReference()
                .child(TITLELIST)
                .child(title)
                .addListenerForSingleValueEvent(listener);
    }
}
