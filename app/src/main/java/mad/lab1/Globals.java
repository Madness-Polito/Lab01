package mad.lab1;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;


import java.io.File;


public class Globals extends Application {

    public static final String[] KEYS = new String[]{"name", "mail", "bio", "date", "city", "phone"};
    public static final String KEY_PIC = "pic";
    public static final int EDIT_CODE = 2;
    public static final String PIC_FILE = "MAD_Lab1_pic";
    public static final String PREFS_NAME = "MAD_Lab1_prefs";


    public static void loadPic(Context c, ImageView pic){

        String path = c.getFilesDir().getPath() + "/" + PIC_FILE;
        File f = new File(path);
        if (f.exists()){
            pic.setImageURI(null); // needed to refresh the cache
            pic.setImageURI(Uri.parse(path));
        }
    }
}
