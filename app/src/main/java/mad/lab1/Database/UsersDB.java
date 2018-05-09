package mad.lab1.Database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UsersDB {

    private static final String USERS = "users";

    // retrieves data of the user identified by uid from the db
    public static void setUser(UserInfo userInfo){

        FirebaseDatabase.getInstance()
                .getReference()
                .child(USERS)
                .child(userInfo.getUid())
                .setValue(userInfo);

    }

    // reads the data of the currently logged in user
    public static void getCurrentUser(ValueEventListener listener){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        getUser(user.getUid(), listener);
    }

    // reads the data of a generic user identified by his uid
    public static void getUser(String uid, ValueEventListener listener){

        FirebaseDatabase.getInstance()
                .getReference()
                .child(USERS)
                .child(uid)
                .addListenerForSingleValueEvent(listener);
    }
}
