package mad.lab1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class editProfile extends AppCompatActivity implements View.OnClickListener{

    private EditText name, mail, bio;
    private ImageView pic;
    private ImageButton imgBtn;
    private Globals g;
    private final String PREFS_NAME = "MAD_Lab1_prefs";
    private final String PIC_FILE   = "MAD_Lab1_pic";
    private Uri mCropImageUri;

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

        // return to showProfile
        finish();

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onClick(View view) {

        //figure out what button ha been pressed
        switch (view.getId()){

            case R.id.selectImage:
                //Button to edit image has been pressed
                CropImage.startPickImageActivity(this);
                break;
        }
    }


    private void startCropImageActivity() {
        CropImage.activity()
                .start(this);
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},   CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already granted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    pic.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //pic.setImageURI(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropImageActivity(mCropImageUri);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .start(this);
    }




}
