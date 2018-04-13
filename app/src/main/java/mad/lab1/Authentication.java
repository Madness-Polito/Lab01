package mad.lab1;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

// manages user authentication with firebase
public class Authentication {

    // providers
    private final static List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.PhoneBuilder().build());

    // return code of sign-in
    public static final int RC_SIGN_IN = 123;

    // returns the uid of the current logged in user
    public static String getCurrentUid(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();
        return uid;
    }


    // check that user is logged in: if not starts login/signup activity
    public static boolean checkSession(){

        FirebaseAuth auth = FirebaseAuth.getInstance();
        return auth.getCurrentUser() != null;
    }

    public static void signIn(Activity activity){

        // create and launch sign-in intent
        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public static void signOut(Activity activity){
        AuthUI.getInstance()
                .signOut(activity)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        activity.startActivity(new Intent(activity, showProfile.class));
                        activity.finish();
                    }
                });

    }

    // deletes an authentication account: note that user data still has to be removed from firebase db
    public static void deleteAccount(Activity activity){
        AuthUI.getInstance()
                .delete(activity)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Deletion succeeded
                        } else {
                            // Deletion failed
                        }
                    }
                });
    }



}
