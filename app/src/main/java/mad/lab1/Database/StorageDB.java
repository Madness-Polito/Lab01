package mad.lab1.Database;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;

import mad.lab1.User.Authentication;

public class StorageDB {

    private final static String PICS_PATH = "userPics";      // name of folder in firebase storage where user's profile pictures are saved

    // returns a reference to the folder where profile pics are saved
    public static StorageReference getProfilePicRef(){

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        return storageRef.child(PICS_PATH);
    }

    public static void putProfilePic(String imgPath){

        // get user uid
        String uid = Authentication.getCurrentUser().getUid();

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
    public static void getProfilePic(Context context){

        // get user uid
        String uid = Authentication.getCurrentUser().getUid();

        // setup db reference
        StorageReference imgRef = StorageDB.getProfilePicRef().child(uid);

        // create local file
        String path = LocalDB.getProfilePicPath(context);
        File localFile = new File(path);
        System.out.println("---------->downloadProfilePic path: " + localFile.getPath());

        imgRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                System.out.println("------------------->profile pic downloaded");
                // Local temp file has been created
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }
}
