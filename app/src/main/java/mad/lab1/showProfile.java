package mad.lab1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.drawable.BitmapDrawable;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import java.io.File;

public class showProfile extends AppCompatActivity  {

    //private Globals g;
    private TextView name, mail, bio, date, city, phone;
    private ImageView pic;
    private SharedPreferences prefs;

    private ImageButton editButton;


    private TextView[] TEXTVIEWS;
    private final String[] KEYS = Globals.KEYS;


    @Override
    protected void onCreate(Bundle b) {

        super.onCreate(b);

        // set up view & references
        setContentView(R.layout.activity_show_profile);
        name = findViewById(R.id.showTextName);
        mail = findViewById(R.id.showTextMail);

        editButton = findViewById(R.id.editProfileButton);

        bio  = findViewById(R.id.showTextBio);
        date = findViewById(R.id.showTextBirthDate);
        city = findViewById(R.id.showTextCityStateName);
        phone= findViewById(R.id.showTextTelephone);
        pic  = findViewById(R.id.showImageProfile);
        TEXTVIEWS = new TextView[]{name, mail, bio, date, city, phone};


        // first app run: load data from storage
        if (b == null){


            // load preferences
            prefs = getSharedPreferences(Globals.PREFS_NAME, MODE_PRIVATE);
            for (int i = 0; i < KEYS.length; i++){
                String s = prefs.getString(KEYS[i], null);
                TEXTVIEWS[i].setText(s);

            }
        }


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), editProfile.class);
                startActivity(i);
            }
        });

        // load pic if exists
        Globals.loadPic(this, pic);

    }

    /*
    TODO: To restore when using the 3 points in show profile with a popup menu.
    IMPLEMENT:
    implements MenuItem.OnMenuItemClickListener

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this::onMenuItemClick);
        popup.inflate(R.menu.edit_profile);
        popup.show();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editButton:
                Intent i = new Intent(getApplicationContext(), editProfile.class);
                for (int j = 0; j < KEYS.length; j++)
                    i.putExtra(KEYS[j], TEXTVIEWS[j].getText().toString());
                startActivityForResult(i, Globals.EDIT_CODE);
                return true;
            default:
                return false;
        }
    }
    */


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {

        // get the return data from editProfile
        if (requestCode == Globals.EDIT_CODE && resultCode == RESULT_OK && i != null) {
            for (int j = 0; j < KEYS.length; j++){
                String s = i.getStringExtra(KEYS[j]);
                TEXTVIEWS[j].setText(s);
            }

            // load pic if exists
            Globals.loadPic(this, pic);
        }
    }

    /*@Override
    public void onStart() {

        super.onStart();

        // redirect to editProfile if no user data set
        if (!g.isProfileSet()){
            Intent i = new Intent(getApplicationContext(), editProfile.class);
            startActivity(i);
        }
    }*/

    // create the edit bar next to the app name
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
      //getMenuInflater().inflate(R.menu.edit_profile, menu);
      return true;
    }

    // associate event listener to the edit bar -> go to edit activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        Intent i = new Intent(getApplicationContext(), editProfile.class);
        startActivity(i);

        return super.onOptionsItemSelected(item);
    }
    */


    protected void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        for (int i = 0; i < KEYS.length; i++)
            b.putString(KEYS[i], TEXTVIEWS[i].getText().toString());
    }
    protected void onRestoreInstanceState(Bundle b) {
        super.onRestoreInstanceState(b);
        for (int i = 0; i < KEYS.length; i++)
            TEXTVIEWS[i].setText(b.getString(KEYS[i]));

    }




}
