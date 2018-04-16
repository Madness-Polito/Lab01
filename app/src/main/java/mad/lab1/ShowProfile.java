package mad.lab1;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

public class ShowProfile extends AppCompatActivity {

    private ImageView pic;
    private TextView[] TEXTVIEWS;
    private final String[] KEYS = Globals.KEYS;
    TextView name, mail, bio, date, city, phone;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        // set up view & references
        setContentView(R.layout.activity_show_profile);
        name = findViewById(R.id.showTextName);
        mail = findViewById(R.id.showTextMail);
        bio = findViewById(R.id.showTextBio);
        date = findViewById(R.id.showTextBirthDate);
        city = findViewById(R.id.showTextCityStateName);
        phone = findViewById(R.id.showTextTelephone);
        pic = findViewById(R.id.showImageProfile);
        TEXTVIEWS = new TextView[]{name, mail, bio, date, city, phone};
        ImageButton editButton = findViewById(R.id.editProfileButton);

        // load user info & update view
        UserInfo userInfo = LocalDB.getUserInfo(this);
        updateView(userInfo);

        // load profile pic if any
        if (LocalDB.isProfilePicSaved(this)){
            Uri picUri = Uri.parse(LocalDB.getProfilePicPath(this));
            pic.setImageURI(picUri);
        }

        editButton.setOnClickListener((View v) ->{

                Intent i = new Intent(getApplicationContext(), EditProfile.class);
                for (int j = 0; j < KEYS.length; j++)
                    i.putExtra(KEYS[j], TEXTVIEWS[j].getText().toString());
                startActivityForResult(i, Globals.EDIT_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // get the return data from EditProfile
        if (requestCode == Globals.EDIT_CODE && resultCode == RESULT_OK && data != null) {
            UserInfo userInfo = data.getExtras().getParcelable("userInfo");
            updateView(userInfo);

            // load profile pic if any
            if (LocalDB.isProfilePicSaved(this)){
                Uri picUri = Uri.parse(LocalDB.getProfilePicPath(this));
                pic.setImageURI(null);
                pic.setImageURI(picUri);
            }
        }

       /* if (requestCode == Authentication.RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {

                // check a user exist in users path
                ValueEventListener userListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get Post object and use the values to update the UI
                        UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);

                        // user exists: populate view
                        if (userInfo != null){
                            updateView(userInfo);
                        }
                        // user not exists: write basic data to db
                        else{
                            FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                            userInfo = new UserInfo(fbUser.getUid(),
                                                fbUser.getDisplayName(),
                                                fbUser.getEmail(),
                                                fbUser.getPhoneNumber());
                            UsersDB.setUser(userInfo);
                            updateView(userInfo);
                        }

                        // ...
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                };

                UsersDB.getCurrentUser(userListener);
                Toast.makeText(this, R.string.sign_in_ok, Toast.LENGTH_SHORT).show();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, R.string.sign_in_cancelled, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_SHORT).show();
            }
        }*/
    }

    // copies data from user info on the db to the textviews
    private void updateView(UserInfo userInfo){
        name.setText(userInfo.getName());
        mail.setText(userInfo.getMail());
        bio.setText(userInfo.getBio());
        date.setText(userInfo.getDob());
        city.setText(userInfo.getCity());
        phone.setText(userInfo.getPhone());
    }

  /*  protected void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        for (int i = 0; i < KEYS.length; i++)
            b.putString(KEYS[i], TEXTVIEWS[i].getText().toString());
    }

    protected void onRestoreInstanceState(Bundle b) {
        super.onRestoreInstanceState(b);
        for (int i = 0; i < KEYS.length; i++)
            TEXTVIEWS[i].setText(b.getString(KEYS[i]));
    }*/
}