package mad.lab1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.theartofdev.edmodo.cropper.CropImage;
import java.io.File;
import java.io.FileOutputStream;


public class editProfile extends AppCompatActivity{

    private ImageView pic;
    private Uri mCropImageUri;
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
        ImageButton imgBtn = findViewById(R.id.selectImage);
        ImageButton but_nameCity = findViewById(R.id.editTextNameCity);
        ImageButton but_persInfo = findViewById(R.id.editPersonalInfo);
        ImageButton but_bio = findViewById(R.id.editBio);
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
        imgBtn.setOnClickListener(v -> {
            if (v.getId() == R.id.selectImage)
            CropImage.startPickImageActivity(this);}
        );
        but_nameCity.setOnClickListener((View v) -> {
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
                    int color = TextValidation.isValidName(s.toString()) ? Color.BLACK : Color.RED;
                    txt_editName.setTextColor(color);
                }
            });

            txt_editName.setText(name.getText());
            txt_editCity.setText(city.getText());

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok),     (dialog, id) -> {})
                    .setNegativeButton(getString(R.string.cancel), (dialog, id) -> dialog.cancel());
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
            // override positive buttton to check data
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {

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
            });
        });

        but_persInfo.setOnClickListener((View v) -> {
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
                    .setPositiveButton(getString(R.string.ok),     (dialog, id) -> {})
                    .setNegativeButton(getString(R.string.cancel), (dialog, id) -> dialog.cancel());
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
            // override positive buttton to check data
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view ->{

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
                        String dob = datePicker.getDayOfMonth() + "/" + (datePicker.getMonth() + 1) + "/" + (datePicker.getYear());
                        DoB.setText(dob);
                        alertDialog.dismiss();
                    }
            });
        });

        but_bio.setOnClickListener((View v) -> {
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
                    .setPositiveButton(getString(R.string.ok),     (dialog, id) -> bio.setText(txt_editBio.getText().toString()))
                    .setNegativeButton(getString(R.string.cancel), (dialog, id) -> dialog.cancel());
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        });

        ImageButton saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener((View view) ->{

                // save strings through sharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences(Globals.PREFS_NAME, MODE_PRIVATE).edit();
                for (int j = 0; j < KEYS.length; j++)
                    editor.putString(KEYS[j], TEXTVIEWS[j].getText().toString());
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
                Intent intent = new Intent();
                for (int j = 0; j < KEYS.length; j++)
                    intent.putExtra(KEYS[j], TEXTVIEWS[j].getText().toString());
                setResult(Activity.RESULT_OK, intent);
                finish();

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
                error.printStackTrace();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            // required permissions granted, start crop image activity
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCropImageActivity(mCropImageUri);
            }
            else {
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
        picUri = b.getString(Globals.KEY_PIC);
        if (picUri != null)
            pic.setImageURI(Uri.parse(picUri));
    }
}