package mad.lab1;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class StorageDB {

    private final static String picsPath = "userPics";


    // returns a reference to the folder where profile pics are saved
    private static StorageReference getProfilePicRef(){

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imgRef = storageRef.child(picsPath);

        return imgRef;
    }

    public static void uploadProfilePic(String imgPath){

        imgPath = imgPath.substring(7);

        // get user uid
        String uid = Authentication.getCurrentUid();

        // setup db reference
        StorageReference imgRef = StorageDB.getProfilePicRef().child(uid);

        // upload file
        Uri file = Uri.fromFile(new File(imgPath));
        imgRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        System.out.println("----------->File uploaded");
                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

    // downloads the profile picture and updates the provided imageview
    public static void downloadProfilePic(ImageView imageView){

        try {
            // get user uid
            String uid = Authentication.getCurrentUid();

            // setup db reference
            StorageReference imgRef = StorageDB.getProfilePicRef().child(uid);

            // create local file
            File localFile = File.createTempFile("images", "jpg");

            imgRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    System.out.println("------------------->profile pic downloaded");
                    imageView.setImageURI(Uri.fromFile(localFile));
                    // Local temp file has been created
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
}
