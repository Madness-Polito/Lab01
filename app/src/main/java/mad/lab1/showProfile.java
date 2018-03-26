package mad.lab1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.io.File;

public class showProfile extends AppCompatActivity implements MenuItem.OnMenuItemClickListener {

    private Globals g;
    private TextView name, mail, bio, phone, dateOfBirth;
    private ImageView pic;
    private final String PREFS_NAME = "MAD_Lab1_prefs";
    private final String PIC_FILE   = "MAD_Lab1_pic";
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        g = (Globals)getApplication();

        // set up view & references
        setContentView(R.layout.activity_show_profile);
        name = findViewById(R.id.showTextName);
        mail = findViewById(R.id.showTextMail);
        bio = findViewById(R.id.showTextBio);
        pic = findViewById(R.id.showImageProfile);
        phone = findViewById(R.id.showTextTelephone);
        dateOfBirth = findViewById(R.id.showTextBirthDate);

        // first app run: load data from storage
        if (b == null){

            // set global data
            prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            g.setProfileSet(prefs.getBoolean("profileSet", false));
            g.setName(prefs.getString("name", null));
            g.setMail(prefs.getString("mail", null));
            g.setBio(prefs.getString("bio", null));
            g.setPhone(prefs.getString("phone", null));
            g.setDateOfBirth(prefs.getString("dateOfBirth", null));
            // load pic if exists
            File f = new File(getFilesDir().getPath() + "/" + PIC_FILE);
            if (f.exists()) {
                Bitmap bmp = BitmapFactory.decodeFile(getFilesDir().getPath() + "/" + PIC_FILE);
                g.setBmp(bmp);
            }

        }
    }

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
                startActivity(i);
                return true;
            default:
                return false;
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        // redirect to editProfile if no user data set
        /*if (!g.isProfileSet()){
            Intent i = new Intent(getApplicationContext(), editProfile.class);
            startActivity(i);
        }*/
    }

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

    // read data from globals
    @Override
    public void onResume() {
        super.onResume();

        name.setText(g.getName());
        mail.setText(g.getMail());
        bio.setText(g.getBio());
        pic.setImageBitmap(g.getBmp());
        phone.setText(g.getPhone());
        dateOfBirth.setText(g.getDateOfBirth());
    }
}
