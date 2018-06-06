package mad.lab1.User;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.theartofdev.edmodo.cropper.CropImage;

import mad.lab1.CustomTextWatcher;
import mad.lab1.Database.Globals;
import mad.lab1.Database.LocalDB;
import mad.lab1.Database.StorageDB;
import mad.lab1.Database.UserInfo;
import mad.lab1.Database.UsersDB;
import mad.lab1.Map.MapsBookToShare;
import mad.lab1.R;
import mad.lab1.TextValidation;

public class EditProfile extends AppCompatActivity{

    private ImageView pic;
    private Uri mCropImageUri;
    private TextView name, mail, bio, phone, DoB, city;
    private String latitude, longitude;
    private final String[] KEYS = Globals.KEYS;
    private TextView[] TEXTVIEWS;
    private String picUri;
    private int BOOK_LOCATION_CODE = 1234;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private RatingBar ratingBar;
    private Float totStarCount;
    private Float totReviewCount;
    private Float numStar;

    private TextView numberReviewsEditProfile;


    private ImageButton personalInfoButton, locationButton, bioButton, favBooksButton;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_edit_profile);

        //subscribe to firebase notification channel
        FirebaseMessaging.getInstance().subscribeToTopic(user.getUid());

        getLatLong();

        // get object references
        pic = findViewById(R.id.showImageProfile);
        ImageButton imgBtn = findViewById(R.id.selectImage);
        ImageButton but_nameCity = findViewById(R.id.editTextNameCity);
        ImageButton but_bookLocation = findViewById(R.id.editBookToShareLocation);
        ImageButton but_persInfo = findViewById(R.id.editPersonalInfo);
        ImageButton but_bio = findViewById(R.id.editBio);
        ImageButton backButton = findViewById(R.id.editProfileBackButton);
        ratingBar = findViewById(R.id.edit_profile_rating_bar);

        setRatingBar();

        this.personalInfoButton = but_persInfo;
        this.locationButton = but_bookLocation;
        this.bioButton = but_bio;
        //this.favBooksButton

        bio = findViewById(R.id.showTextBio);
        name = findViewById(R.id.showTextName);
        city = findViewById(R.id.showTextCityStateName);
        DoB = findViewById(R.id.showTextBirthDate);
        phone = findViewById(R.id.showTextTelephone);
        mail = findViewById(R.id.showTextMail);
        TEXTVIEWS = new TextView[]{name, mail, bio, DoB, city, phone};

        numberReviewsEditProfile = findViewById(R.id.numberReviewsEditProfile);
        numberReviewsEditProfile.setText("0");

        // parse intent
        Intent i = getIntent();
        if (i != null) {
            for (int j = 0; j < KEYS.length; j++) {
                String s = i.getStringExtra(KEYS[j]);
                TEXTVIEWS[j].setText(s);
            }
        }

        // load profile pic if any
        if (LocalDB.isProfilePicSaved(this)){
            Uri picUri = Uri.parse(LocalDB.getProfilePicPath(this));
            pic.setImageURI(picUri);
        }

        //set listeners
        imgBtn.setOnClickListener(v -> {
            if (v.getId() == R.id.selectImage)
                //CropImage.startPickImageActivity(this);
                CropImage.activity()
                    .start(this);
        });

        but_bookLocation.setOnClickListener((View v) ->{
            // start an activity that returns long and lat
            Intent intent = new Intent(getApplicationContext(), MapsBookToShare.class);
            startActivityForResult(intent, BOOK_LOCATION_CODE);
            //then manage activity result, take long and lat and load them on firebase
        });

        runFirstTimeTutorial();

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
                    .setCancelable(true)
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
                    .setCancelable(true)
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
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.ok),     (dialog, id) -> bio.setText(txt_editBio.getText().toString()))
                    .setNegativeButton(getString(R.string.cancel), (dialog, id) -> dialog.cancel());
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        });

        ImageButton saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener((View view) -> {
            if(name.getText().toString().equals("") || mail.getText().toString().equals("") || city.getText().toString().equals("") || phone.getText().toString().equals("")){

                Toast toast = Toast.makeText(this, "Please make sure that name, phone#, mail and city are filled", Toast.LENGTH_LONG);
                toast.show();

                name.setHintTextColor(Color.RED);
                phone.setHintTextColor(Color.RED);
                mail.setHintTextColor(Color.RED);
                city.setHintTextColor(Color.RED);


            }else{
                // save user data to firebase & locally

                UserInfo userInfo = new UserInfo(user.getUid(),
                            name.getText().toString(),
                            mail.getText().toString(),
                            phone.getText().toString(),
                            city.getText().toString(),
                            DoB.getText().toString(),
                            bio.getText().toString(),
                            latitude,
                            longitude);

                UsersDB.setUser(userInfo);
                LocalDB.putUserInfo(this, userInfo);

                // save pic to firebase & locally
                if (picUri != null) {
                    StorageDB.putProfilePic(picUri);
                    LocalDB.putProfilePic(this, picUri);
                }

                // save strings through sharedPreferences
                    /*SharedPreferences.Editor editor = getSharedPreferences(Globals.PREFS_NAME, MODE_PRIVATE).edit();
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
                    }*/

                // return data to ShowProfile
                Intent intent = new Intent();
                intent.putExtra("userInfo", userInfo);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("reviews").child(user.getUid()).child("reviewCount");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer reviews = dataSnapshot.getValue(Integer.class);
                if(reviews != null){
                    numberReviewsEditProfile.setText(reviews.toString());
                }else{
                    numberReviewsEditProfile.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void runFirstTimeTutorial(){
        //  Initialize SharedPreferences
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        //  Create a new boolean and preference and set it to true
        boolean isFirstStart = getPrefs.getBoolean("editProfileFirstStart", true);

        Log.d("first", ""+getPrefs.getBoolean("editProfileFirstStart", true));
        //  If the activity has never started before...
        if (isFirstStart) {
            // set intro of edit button

            //TODO add strings.xml strings for each button once everything is finished
            showcase("locationButton");
            //  Make a new preferences editor
            SharedPreferences.Editor e = getPrefs.edit();

            //  Edit preference to make it false because we don't want this to run again
            e.putBoolean("editProfileFirstStart", false);

            //  Apply changes
            e.apply();

        }
    }

    private void showcase(String btnName){
        switch (btnName){
            case "locationButton" :
                new ShowcaseView.Builder(this)
                        .withMaterialShowcase()
                        .setStyle(R.style.CustomShowcaseTheme2)
                        .setTarget(new ViewTarget(locationButton))
                        .setContentTitle("book location")
                        .setContentText("press this button to edit your actual location")
                        .setShowcaseEventListener(
                                new SimpleShowcaseEventListener(){
                                    @Override
                                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                                        showcase("personalInfoButton");
                                    }
                                }
                        )
                        .build();
                break;

            case "personalInfoButton":
                new ShowcaseView.Builder(this)
                        .withMaterialShowcase()
                        .setStyle(R.style.CustomShowcaseTheme2)
                        .setTarget(new ViewTarget(personalInfoButton))
                        .setContentTitle("personal information")
                        .setContentText("press this button to edit your personal info")
                        .setShowcaseEventListener(
                                new SimpleShowcaseEventListener(){
                                    @Override
                                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                                        showcase("bioButton");
                                    }
                                }
                        )
                        .build();
                break;

            case "bioButton":

                new ShowcaseView.Builder(this)
                        .withMaterialShowcase()
                        .setStyle(R.style.CustomShowcaseTheme2)
                        .setTarget(new ViewTarget(bioButton))
                        .setContentTitle("biography")
                        .setContentText("write something about you to let other users to know who you are")
                        .setShowcaseEventListener(
                                new SimpleShowcaseEventListener(){
                                    @Override
                                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                                        //showcaseBio(bioButton);
                                        //call again this method with a new String if you have to add another button
                                    }
                                }
                        )
                        .build();

                break;
        }
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
                System.out.println("..............." + resultUri);
                picUri = resultUri.toString().substring(7);
                pic.setImageURI(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                error.printStackTrace();
            }

        } else if (requestCode == BOOK_LOCATION_CODE){

            if (resultCode == RESULT_OK){
                Bundle coordinates =  data.getExtras();
                if(coordinates != null){
                    LatLng coo = (LatLng) coordinates.get("LatLng");
                    latitude = new Double(coo.latitude).toString();
                    longitude = new Double(coo.longitude).toString();

                    //Toast.makeText(this, "lat "+lat+" , lng "+lng, Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(this, "ERROR, coo is null", Toast.LENGTH_SHORT).show();

            }
            else if (resultCode == RESULT_CANCELED){
                //nothing
                //Toast.makeText(this, "nothing to be returned", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "unknown error", Toast.LENGTH_SHORT).show();
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

    private void getLatLong(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInfo user = dataSnapshot.getValue(UserInfo.class);
                latitude = user.getLatitude();
                longitude = user.getLongitude();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                latitude = null;
                longitude = null;
            }
        });
    }


    private void setRatingBar(){



        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference().child("reviews").child(Authentication.getCurrentUser().getUid());
        DatabaseReference totCountRef = dbRef.child("totStarCount");
        DatabaseReference reviewCountRef = dbRef.child("reviewCount");



        totCountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    totStarCount = dataSnapshot.getValue(Float.class);
                    if (totReviewCount != null) {
                        numStar = totStarCount / totReviewCount;
                    } else {
                        numStar = new Float(0);
                    }
                    ratingBar.setRating(numStar);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reviewCountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totReviewCount = dataSnapshot.getValue(Float.class);
                if(totStarCount != null){
                    numStar = totStarCount / totReviewCount;
                }else{
                    numStar = new Float(0);
                }
                ratingBar.setRating(numStar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

}