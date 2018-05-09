package mad.lab1.User;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import mad.lab1.Database.Globals;
import mad.lab1.Database.LocalDB;
import mad.lab1.Database.UserInfo;
import mad.lab1.R;

public class ShowProfile extends AppCompatActivity {

    private ImageView pic;
    private TextView[] TEXTVIEWS;
    private final String[] KEYS = Globals.KEYS;
    TextView name, mail, bio, date, city, phone;
    private ImageButton backButton, editButton;

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
        editButton = findViewById(R.id.editProfileButton);
        backButton = findViewById(R.id.showProfileBackButton);

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
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
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