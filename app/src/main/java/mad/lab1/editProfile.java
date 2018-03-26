package mad.lab1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class editProfile extends AppCompatActivity{

    private ImageView pic;
    private Bitmap oldBmp, bmp;
    private boolean saved = false;
    private ImageButton imgBtn;
    private Globals g;
    private final String PREFS_NAME = "MAD_Lab1_prefs";
    private final String PIC_FILE   = "MAD_Lab1_pic";
    private Uri mCropImageUri;
    private ImageButton saveButton;
    private ImageButton but_nameCity;
    private ImageButton but_persInfo;
    private ImageButton but_bio;
    private TextView txt_bio;
    private TextView txt_name;
    private TextView txt_city;
    private TextView txt_dateOfBirth;
    private TextView txt_phone;
    private TextView txt_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        g = (Globals)getApplication();
        setContentView(R.layout.activity_edit_profile);

        // get object references

        /*
        name = findViewById(R.id.editTextName);
        mail = findViewById(R.id.editTextMail);
        bio  = findViewById(R.id.editTextBio);
        */
        pic = findViewById(R.id.showImageProfile);
        imgBtn = findViewById(R.id.selectImage);
        but_nameCity = findViewById(R.id.editTextNameCity);
        but_persInfo = findViewById(R.id.editPersonalInfo); //assign editPersonalInfo btn to the button
        but_bio = findViewById(R.id.editBio);

        txt_bio = findViewById(R.id.showTextBio);
        txt_name = findViewById(R.id.showTextName);
        txt_city = findViewById(R.id.showTextCityStateName);
        txt_dateOfBirth = findViewById(R.id.showTextBirthDate);
        txt_phone = findViewById(R.id.showTextTelephone);
        txt_email = findViewById(R.id.showTextMail);

        //set listeners

        imgBtn.setOnClickListener(v -> { if (v.getId() == R.id.selectImage)
                                            CropImage.startPickImageActivity(this);} );
        but_nameCity.setOnClickListener(v -> {
            // get prompts.xml view
            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.edit_name_city_layout, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText txt_editName = (EditText)promptsView.findViewById(R.id.txt_editName);
            final EditText txt_editCity = (EditText)promptsView.findViewById(R.id.txt_editCity);

            txt_editName.setText(txt_name.getText());
            txt_editCity.setText(txt_city.getText());

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    txt_name.setText(txt_editName.getText().toString());
                                    txt_city.setText(txt_editCity.getText().toString());
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        });

        but_persInfo.setOnClickListener(v -> {
            // get prompts.xml view
            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.edit_pers_info_layout, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText txt_editPhone = (EditText)promptsView.findViewById(R.id.txt_editPhone);
            final EditText txt_editEmail = (EditText)promptsView.findViewById(R.id.txt_editEmail);
            final DatePicker datePicker = (DatePicker)promptsView.findViewById(R.id.datePicker);

            txt_editPhone.setText(txt_phone.getText());
            txt_editEmail.setText(txt_email.getText());
            String[] date = txt_dateOfBirth.getText().toString().split("/");
            datePicker.updateDate(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0]));

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    txt_phone.setText(txt_editPhone.getText().toString());
                                    txt_email.setText(txt_editEmail.getText().toString());
                                    txt_dateOfBirth.setText(datePicker.getDayOfMonth() + "/" + (datePicker.getMonth() + 1) + "/" + (datePicker.getYear()));
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        });

        but_bio.setOnClickListener(v -> {
            // get prompts.xml view
            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.edit_bio_layout, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText txt_editBio = (EditText)promptsView.findViewById(R.id.txt_editBio);

            txt_editBio.setText(txt_bio.getText());

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    txt_bio.setText(txt_editBio.getText().toString());

                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        });

        /*
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextValidation.isValidName(s.toString()))
                    name.setTextColor(Color.RED);
            }
        });
        mail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextValidation.isValidMail(s.toString()))
                    mail.setTextColor(Color.RED);
            }
        });


        */

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saved = true;
                // check data satisfies regex
                // check name
                if (!TextValidation.isValidName(txt_name.getText().toString())) {
                    txt_name.setError(getString(R.string.invalidName));

                } else if (!TextValidation.isValidMail(txt_email.getText().toString())) {
                    // check mail
                    txt_email.setError(getString(R.string.invalidMail));

                }else{



                bmp = ((BitmapDrawable) pic.getDrawable()).getBitmap();

                //save an instance of Bitmap value
                oldBmp = bmp;
                // update global vars
                g.setProfileSet(true);
                g.setName(txt_name.getText().toString());
                g.setMail(txt_email.getText().toString());
                g.setBio(txt_bio.getText().toString());
                g.setBmp(bmp);
                g.setPhone(txt_phone.getText().toString());
                g.setDateOfBirth(txt_dateOfBirth.getText().toString());

                // save simple data through sharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                editor.putBoolean("profileSet", true);
                editor.putString("name", txt_name.getText().toString());
                editor.putString("mail", txt_email.getText().toString());
                editor.putString("bio", txt_bio.getText().toString());
                editor.putString("phone", txt_phone.getText().toString());
                editor.putString("dateOfBirth", txt_dateOfBirth.getText().toString());
                editor.apply();

                // save pic to file
                try {
                    if (bmp != null) {
                        FileOutputStream outStream = openFileOutput(PIC_FILE, Context.MODE_PRIVATE);
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        outStream.close();
                    }
                }
                catch (Exception e ){
                    e.printStackTrace();
                }

                // return to showProfile
                finish();
                }
            }
        });

        txt_phone.setText(g.getPhone());

        txt_name.setText(g.getName());
        txt_email.setText(g.getMail());
        txt_bio.setText(g.getBio());
        pic.setImageBitmap(g.getBmp());

    }

    // create the edit bar next to the app name
    // all icons under menu folder are automatically put
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //getMenuInflater().inflate(R.menu.save_profile, menu);
        return true;
    }

    // save data persistently
    @Override
    public boolean onOptionsItemSelected(MenuItem item){



        return super.onOptionsItemSelected(item);
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
                    g.setBmp(bitmap);
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

                .setMaxCropResultSize(1800, 1800)
                .setMinCropResultSize(1800, 1800)

                .start(this);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if(saved == false){
                Log.d("PAUSE", "saved -> FALSE -> switching back to old pic");

                g.setBmp(oldBmp);
                try {
                    FileOutputStream outStream = openFileOutput(PIC_FILE, Context.MODE_PRIVATE);
                    oldBmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    outStream.close();
                } catch(Exception e){
                    e.printStackTrace();
                }

            }

        }

        return super.onKeyDown(keyCode, event);
    }


}
