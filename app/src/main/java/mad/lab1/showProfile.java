package mad.lab1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class showProfile extends AppCompatActivity {

    private ImageView pic;
    private TextView[] TEXTVIEWS;
    private final String[] KEYS = Globals.KEYS;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        // set up view & references
        setContentView(R.layout.activity_show_profile);
        TextView name = findViewById(R.id.showTextName);
        TextView mail = findViewById(R.id.showTextMail);
        TextView bio  = findViewById(R.id.showTextBio);
        TextView date = findViewById(R.id.showTextBirthDate);
        TextView city = findViewById(R.id.showTextCityStateName);
        TextView phone= findViewById(R.id.showTextTelephone);
        pic  = findViewById(R.id.showImageProfile);
        TEXTVIEWS = new TextView[]{name, mail, bio, date, city, phone};
        ImageButton editButton = findViewById(R.id.editProfileButton);


        // first app run: load data from storage
        if (b == null){

            // load preferences
            SharedPreferences prefs = getSharedPreferences(Globals.PREFS_NAME, MODE_PRIVATE);
            for (int i = 0; i < KEYS.length; i++){
                String s = prefs.getString(KEYS[i], null);
                TEXTVIEWS[i].setText(s);
            }
        }

        // load pic if exists
        Globals.loadPic(this, pic);

        editButton.setOnClickListener((View v) ->{

                Intent i = new Intent(getApplicationContext(), editProfile.class);
                for (int j = 0; j < KEYS.length; j++)
                    i.putExtra(KEYS[j], TEXTVIEWS[j].getText().toString());
                startActivityForResult(i, Globals.EDIT_CODE);
        });
    }

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