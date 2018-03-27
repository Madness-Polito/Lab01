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
import android.graphics.BitmapFactory;
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
    private ImageButton imgBtn;
    private Uri mCropImageUri;
    private ImageButton saveButton;
    private ImageButton but_nameCity;
    private ImageButton but_persInfo;
    private ImageButton but_bio;
    private TextView name, mail, bio, phone, DoB, city;
    private final String[] KEYS = Globals.KEYS;
    private TextView[] TEXTVIEWS;
    private String picUri;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_edit_profile);

        // get object references
        pic = findViewById(R.id.showImageProfile);
        imgBtn = findViewById(R.id.selectImage);
        but_nameCity = findViewById(R.id.editTextNameCity);
        but_persInfo = findViewById(R.id.editPersonalInfo);
        but_bio = findViewById(R.id.editBio);
        bio = findViewById(R.id.showTextBio);
        name = findViewById(R.id.showTextName);
        city = findViewById(R.id.showTextCityStateName);
        DoB = findViewById(R.id.showTextBirthDate);
        phone = findViewById(R.id.showTextTelephone);
        mail = findViewById(R.id.showTextMail);
        TEXTVIEWS = new TextView[]{name, mail, bio, DoB, city, phone};

        // parse intent
        Intent i = getIntent();
        if (i != null){
            for (int j = 0; j < KEYS.length; j++) {
                String s = i.getStringExtra(KEYS[j]);
                TEXTVIEWS[j].setText(s);
            }
        }

        // load pic if exists
        String uri = getFilesDir().getPath() + "/" + Globals.PIC_FILE;
        File f = new File(uri);
        if (f.exists()) {
            picUri = uri;
            Bitmap bmp = BitmapFactory.decodeFile(picUri);
            pic.setImageBitmap(bmp);
        }

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

            final EditText txt_editName = promptsView.findViewById(R.id.txt_editName);
            final EditText txt_editCity = promptsView.findViewById(R.id.txt_editCity);

            // add name listener
            txt_editName.addTextChangedListener(new CustomTextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    System.out.println("------->Checking text " + s.toString());
                    int color = TextValidation.isValidName(s.toString()) ? Color.BLACK : Color.RED;
                    txt_editName.setTextColor(color);
                }
            });

            txt_editName.setText(name.getText());
            txt_editCity.setText(city.getText());

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                }
                            })
                    .setNegativeButton(getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
            // override positive buttton to check data
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){

                    // check name
                    if (!TextValidation.isValidName(txt_editName.getText().toString())){
                        txt_editName.requestFocus();
                        String errMsg = getString(R.string.invalidName);
                        txt_editName.setError(errMsg);
                    }
                    // TODO check city
                    else {
                        // all good: copy data
                        name.setText(txt_editName.getText().toString());
                        city.setText(txt_editCity.getText().toString());
                        alertDialog.dismiss();
                    }
                }
            });
        });

        but_persInfo.setOnClickListener(v -> {
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(this);
                View promptsView = li.inflate(R.layout.edit_pers_info_layout, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText txt_editPhone = promptsView.findViewById(R.id.txt_editPhone);
                final EditText txt_editEmail = promptsView.findViewById(R.id.txt_editEmail);
                final DatePicker datePicker = promptsView.findViewById(R.id.datePicker);

                // phone listener
                txt_editPhone.addTextChangedListener(new CustomTextWatcher() {

                    @Override
                    public void afterTextChanged(Editable s) {
                        int color = TextValidation.isValidPhone(s.toString()) ? Color.BLACK : Color.RED;
                        txt_editPhone.setTextColor(color);
                    }
                });

                // mail listener
                txt_editEmail.addTextChangedListener(new CustomTextWatcher() {

                    @Override
                    public void afterTextChanged(Editable s) {
                        int color = TextValidation.isValidMail(s.toString()) ? Color.BLACK : Color.RED;
                        txt_editEmail.setTextColor(color);
                    }
                });

                txt_editPhone.setText(phone.getText());
                txt_editEmail.setText(mail.getText());

                if (!DoB.getText().toString().equals("")){
                    String[] date = DoB.getText().toString().split("/");
                    datePicker.updateDate(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0]));
                }

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {

                                }
                            })
                    .setNegativeButton(getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
            // override positive buttton to check data
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){

                    // check phone
                    if (!TextValidation.isValidPhone(txt_editPhone.getText().toString())){
                        txt_editPhone.requestFocus();
                        String errMsg = getString(R.string.invalidPhone);
                        txt_editPhone.setError(errMsg);
                    }
                    // check mail
                    else if (!TextValidation.isValidMail(txt_editEmail.getText().toString())){
                        txt_editEmail.requestFocus();
                        String errMsg = getString(R.string.invalidMail);
                        txt_editEmail.setError(errMsg);
                    }
                    else {
                        phone.setText(txt_editPhone.getText().toString());
                        mail.setText(txt_editEmail.getText().toString());
                        DoB.setText(datePicker.getDayOfMonth() + "/" + (datePicker.getMonth() + 1) + "/" + (datePicker.getYear()));
                        alertDialog.dismiss();
                    }
                }
            });
        });

        but_bio.setOnClickListener(v -> {
            // get prompts.xml view
            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.edit_bio_layout, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText txt_editBio = promptsView.findViewById(R.id.txt_editBio);

            txt_editBio.setText(bio.getText());

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    bio.setText(txt_editBio.getText().toString());
                                }
                            })
                    .setNegativeButton(getString(R.string.cancel),
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

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // save strings through sharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences(Globals.PREFS_NAME, MODE_PRIVATE).edit();
                for (int i = 0; i < KEYS.length; i++)
                    editor.putString(KEYS[i], TEXTVIEWS[i].getText().toString());
                editor.apply();

                // save pic to file
                try {
                    Bitmap bmp = ((BitmapDrawable) pic.getDrawable()).getBitmap();
                    if (bmp != null) {
                        FileOutputStream outStream = openFileOutput(Globals.PIC_FILE, Context.MODE_PRIVATE);
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        outStream.close();
                    }
                }
                catch (Exception e ){
                    e.printStackTrace();
                }

                // return data to showProfile
                Intent i = new Intent();
                for (int j = 0; j < KEYS.length; j++)
                    i.putExtra(KEYS[j], TEXTVIEWS[j].getText().toString());
                setResult(Activity.RESULT_OK, i);
                finish();
                }
            //}
        });
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
                picUri = resultUri.toString();
                pic.setImageURI(resultUri);

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


    protected void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        for (int i = 0; i < KEYS.length; i++)
            b.putString(KEYS[i], TEXTVIEWS[i].getText().toString());

        // save tmp pic
        if (picUri != null)
            b.putString(Globals.KEY_PIC, picUri);

    }
    protected void onRestoreInstanceState(Bundle b) {
        super.onRestoreInstanceState(b);
        for (int i = 0; i < KEYS.length; i++)
            TEXTVIEWS[i].setText(b.getString(KEYS[i]));

        // save uri of chosen pic
        String uri = b.getString(Globals.KEY_PIC);
        if (uri != null)
            pic.setImageURI(Uri.parse(uri));
    }
}
