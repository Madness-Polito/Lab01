package mad.lab1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.File;
import java.io.FileOutputStream;

public class editProfile extends AppCompatActivity implements View.OnClickListener, IPickResult{

    private EditText name, mail, bio;
    private ImageView pic;
    private ImageButton imgBtn;
    private Globals g;
    private PickSetup setup;
    private final String PREFS_NAME = "MAD_Lab1_prefs";
    private final String PIC_FILE   = "MAD_Lab1_pic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        g = (Globals)getApplication();
        setContentView(R.layout.activity_edit_profile);

        name = findViewById(R.id.editTextName);
        mail = findViewById(R.id.editTextMail);
        bio  = findViewById(R.id.editTextBio);
        pic  = findViewById(R.id.editImageProfile);
        imgBtn = findViewById(R.id.selectImage);

        //set listeners
        imgBtn.setOnClickListener(this);

        //customize PickSetup (gallery/camera)
        setup = new PickSetup().setSystemDialog(true);

        //Intent i = getIntent();
        name.setText(g.getName());
        mail.setText(g.getMail());
        bio.setText(g.getBio());
        pic.setImageBitmap(g.getBmp());
        /*Bundle b = i.getExtras();
        name.setText(b.getString("name"));
        mail.setText(b.getString("mail"));
        bio.setText(b.getString("bio"));
        Bitmap bm = b.getParcelable("pic");
        pic.setImageBitmap(bm);*/
    }

    // create the edit bar next to the app name
    // all icons under menu folder are automatically put
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.save_profile, menu);
        return true;
    }

    // save data persistently
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        // save data
        try {
            // save simple data through sharedPreferences
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("name", name.getText().toString());
            editor.putString("mail", mail.getText().toString());
            editor.putString("bio", bio.getText().toString());
            editor.apply();

            // save pic to file
            Bitmap bmp = ((BitmapDrawable)pic.getDrawable()).getBitmap();
            if (bmp != null){
                FileOutputStream outStream = openFileOutput(PIC_FILE, Context.MODE_PRIVATE);
                bmp.compress(Bitmap.CompressFormat.JPEG, 30, outStream);
                outStream.close();
            }

            // update global vars
            g.setName(name.getText().toString());
            g.setMail(mail.getText().toString());
            g.setBio(bio.getText().toString());
            g.setBmp(bmp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // restart showProfile activity passing saved data
        Intent i = new Intent(getApplicationContext(), showProfile.class);
        startActivity(i);
        finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            Bitmap bmp = r.getBitmap();
            pic.setImageBitmap(bmp);
            g.setBmp(bmp);
        } /*else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
        }*/
    }

    @Override
    public void onClick(View view) {

        //figure out what button ha been pressed
        switch (view.getId()){

            case R.id.selectImage:
                //Button to edit image has been pressed
                PickImageDialog.build(setup).show(this);
                break;
        }
    }




}
