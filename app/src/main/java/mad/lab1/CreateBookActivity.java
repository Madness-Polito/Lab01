package mad.lab1;


import android.*;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateBookActivity extends AppCompatActivity {
    private ImageView img_thumbnail;
    private EditText txt_bookTitle;
    private EditText txt_author;
    private EditText txt_publisher;
    private EditText txt_pubDate;
    private EditText txt_description;
    private Spinner spin_condition;
    private List<String> spinList;
    private ImageButton btn_thumbnail;

    private Button btn_ok;
    private Button btn_cancel;

    private Bitmap thumbnail;

    private Uri mCropImageUri;


    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        setContentView(R.layout.add_new_book_layout);

        img_thumbnail = findViewById(R.id.img_thumbnail);
        txt_bookTitle = findViewById(R.id.txt_bookTitle);
        txt_author = findViewById(R.id.txt_author);
        txt_publisher = findViewById(R.id.txt_publisher);
        txt_pubDate = findViewById(R.id.txt_date);
        txt_description = findViewById(R.id.txt_description);
        spin_condition = findViewById(R.id.spin_condition);
        btn_thumbnail = findViewById(R.id.btn_thumbnail);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_ok = findViewById(R.id.btn_save);

        spinList = new ArrayList<>();



        spinList.add(getString(R.string.cond_vUsed));
        spinList.add(getString(R.string.cond_used));
        spinList.add(getString(R.string.cond_new));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_condition.setAdapter(adapter);

        btn_thumbnail.setOnClickListener(view -> {
            CropImage.startPickImageActivity(this);
        });



        btn_ok.setOnClickListener(view -> {
            String condition = spin_condition.getSelectedItem().toString();
            String title = txt_bookTitle.getText().toString();
            String author = txt_author.getText().toString();
            String publisher = txt_publisher.getText().toString();
            String pubDate = txt_pubDate.getText().toString();
            String description = txt_description.getText().toString();
            Integer year = Integer.parseInt(pubDate);
            int thisYear = Calendar.getInstance().get(Calendar.YEAR);
            if(year <= thisYear) {
                if (condition != null && title.length() != 0 && author.length() != 0 && publisher.length() != 0 && pubDate.length() != 0 && description.length() != 0) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("condition", condition);
                    returnIntent.putExtra("thumbnail", getResizedBitmap(thumbnail, 130, 200));
                    returnIntent.putExtra("title", title);
                    returnIntent.putExtra("author", author);
                    returnIntent.putExtra("publisher", publisher);
                    returnIntent.putExtra("pubDate", pubDate);
                    returnIntent.putExtra("description", description);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    //data is missing
                    Toast.makeText(this, getString(R.string.missingData), Toast.LENGTH_LONG).show();
                }
            }else{
                //wrong year
                Toast.makeText(this, getString(R.string.wrongYear), Toast.LENGTH_LONG).show();
            }
        });

        btn_cancel.setOnClickListener(view -> {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
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
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},   CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already granted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                img_thumbnail.setImageURI(resultUri);
                thumbnail = ((BitmapDrawable)img_thumbnail.getDrawable()).getBitmap();


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                error.printStackTrace();
            }

        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .start(this);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth;
        float scaleHeight;
        if(width > height){
            scaleWidth = ((float) newWidth) / width;
            scaleHeight = scaleWidth;
        }else{
            scaleHeight = ((float) newHeight) / height;
            scaleWidth = scaleHeight;
        }

        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        String title = txt_bookTitle.getText().toString();
        String author = txt_author.getText().toString();
        String publisher = txt_publisher.getText().toString();
        String pubDate = txt_pubDate.getText().toString();
        String description = txt_description.getText().toString();
        savedInstanceState.putString("title", title);
        savedInstanceState.putString("author", author);
        savedInstanceState.putString("publisher", publisher);
        savedInstanceState.putString("pubDate", pubDate);
        savedInstanceState.putString("description", description);
        savedInstanceState.putParcelable("thumbnail", thumbnail);
        savedInstanceState.putInt("condition", spin_condition.getSelectedItemPosition());

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        txt_bookTitle.setText(savedInstanceState.getString("title"));
        txt_author.setText(savedInstanceState.getString("author"));
        txt_publisher.setText(savedInstanceState.getString("publisher"));
        txt_pubDate.setText(savedInstanceState.getString("pubDate"));
        txt_description.setText(savedInstanceState.getString("description"));
        thumbnail = savedInstanceState.getParcelable("thumbnail");
        if(thumbnail != null) {
            img_thumbnail.setImageBitmap(thumbnail);
        }
        spin_condition.setSelection(savedInstanceState.getInt("condition"));

    }





}
