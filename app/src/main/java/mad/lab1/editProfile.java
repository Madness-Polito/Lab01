package mad.lab1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class editProfile extends AppCompatActivity {

    private EditText name, mail, bio;
    private ImageView pic;
    private ImageButton imgBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        name = findViewById(R.id.editTextName);
        mail = findViewById(R.id.editTextMail);
        bio  = findViewById(R.id.editTextBio);
        pic  = findViewById(R.id.editImageProfile);
        imgBtn = findViewById(R.id.selectImage);

    }

    // create the edit bar next to the app name
    // all icons under menu folder are automatically put
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.profile_edit, menu);
        return true;
    }

    // associate event listener to the edit bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        Intent i = new Intent(getApplicationContext(), editProfile.class);
        startActivity(i);

        return super.onOptionsItemSelected(item);
    }

    // save user profile in Bundle
    @Override
    public void onSaveInstanceState(Bundle b) {

        b.putString("name", name.getText().toString());
        b.putString("mail", mail.getText().toString());
        b.putString("bio", bio.getText().toString());
        Bitmap bm = pic.getDrawingCache();
        b.putParcelable("pic", bm);

        super.onSaveInstanceState(b);
    }

    // restore data stored in bundle
    @Override
    public void onRestoreInstanceState(Bundle b){
        super.onRestoreInstanceState(b);

        name.setText(b.getString("name"));
        mail.setText(b.getString("mail"));
        bio.setText(b.getString("bio"));
        Bitmap bm = b.getParcelable("pic");
        pic.setImageBitmap(bm);
    }



}
