package mad.lab1;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.ImageView;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

public class LocalDB {

    private final static String PREFS_NAME  = "Madness_prefs";   // name of shared prefs file
    private final static String USER_INFO   = "UserInfo";        // name of key of userInfo object stored into the shared prefs
    public  final static String PROFILE_PIC = "profilePic";     // name of local file where we store the profile picture
    private final static String BOOKS_FILE  = "books";          // filename whre to store book info


    // returns the shared preferences of this app
    private static SharedPreferences getPrefs(Context context){
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // saves userInfo into shared preferences
    public static void putUserInfo(Context context, UserInfo userInfo){

        // get app prefs
        SharedPreferences prefs = LocalDB.getPrefs(context);

        // add object to prefs
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(userInfo);
        prefsEditor.putString(USER_INFO, json);
        prefsEditor.apply();
    }

    // reads the user info from the preferences and returns it
    public static UserInfo getUserInfo(Context context){

        // get app prefs
        SharedPreferences prefs = LocalDB.getPrefs(context);

        // read userInfo
        Gson gson = new Gson();
        String json = prefs.getString(USER_INFO, null);
        return gson.fromJson(json, UserInfo.class);
    }

    public static void putProfilePic(Context context, String tmpPicUri){

        File src = new File(tmpPicUri);
        File dst = new File(getProfilePicPath(context));

        try {
            LocalDB.copyFile(src, dst);
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }

    }

    private static void deleteProfilePic(Context context){

        File file = new File(getProfilePicPath(context));
        file.delete();
    }

    // clears all the local files of the user (used when logging out)
    public static void clearProfile(Context context){

        // remove shared prefs
        SharedPreferences prefs = getPrefs(context);
        prefs.edit().clear().apply();

        // remove profile pic
        deleteProfilePic(context);
    }

    // read the
    public static String getProfilePicPath(Context context){
        return context.getFilesDir() + "/" + PROFILE_PIC;
    }


    // returns true if user data is stored locally
    public static boolean isProfileSaved(Context context){

        // get app prefs
        SharedPreferences prefs = LocalDB.getPrefs(context);
        return prefs.contains(USER_INFO);
    }

    public static boolean isProfilePicSaved(Context context){
        String path = LocalDB.getProfilePicPath(context);
        System.out.println("-------->isProfilePicSaved path: " + path);
        File f = new File(path);
        return f.exists();
    }

    private static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    private static File getBooksFile(Context c){
        return new File(c.getFilesDir(), BOOKS_FILE);
    }

    // saves a list of books to a file
    public static void putIsbnList(Context c, List<IsbnInfo> books){

        try{
            File f = getBooksFile(c);
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(books);
            oos.close();
            System.out.println("---------->putIsbnList " + f.getPath());
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    // retrieves a list of books from a file
    public static List<IsbnInfo> getIsbnList(Context c){

        List<IsbnInfo> isbnList = null;

        try{
            File f = getBooksFile(c);
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            isbnList = (List<IsbnInfo>)ois.readObject();
            ois.close();
        }
        catch(IOException | ClassNotFoundException ex){
            ex.printStackTrace();
        }

        return isbnList;
    }
}
