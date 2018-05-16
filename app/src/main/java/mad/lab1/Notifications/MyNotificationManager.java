package mad.lab1.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import mad.lab1.MainPageMenu;
import mad.lab1.R;

import static android.content.Context.NOTIFICATION_SERVICE;
import static mad.lab1.Notifications.Constants.CHANNEL_ID;

/**
 * Created by Matteo on 16/05/2018.
 */

public class MyNotificationManager {

    private Context mCtx;
    private static MyNotificationManager mInstance;

    private MyNotificationManager(Context context) {
        mCtx = context;
    }

    public static synchronized MyNotificationManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyNotificationManager(context);
        }
        return mInstance;
    }

    public void displayNotification(String title, String body) {
        long time = System.currentTimeMillis();

        NotificationCompat.MessagingStyle.Message message =
                new NotificationCompat.MessagingStyle.Message(body, time, title);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mCtx, CHANNEL_ID)
                        .setSmallIcon(R.drawable.all_books_tab_icon)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setStyle(new NotificationCompat.MessagingStyle(title).addMessage(message))
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE);






        /*
        *  Clicking on the notification will take us to this intent
        *  Right now we are using the MainActivity as this is the only activity we have in our application
        *  But for your project you can customize it as you want
        * */

        Intent resultIntent = new Intent(mCtx, MainPageMenu.class);
        resultIntent
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .setAction(Constants.OPEN_CHAT);

        /*
        *  Now we will create a pending intent
        *  The method getActivity is taking 4 parameters
        *  All paramters are describing themselves
        *  0 is the request code (the second parameter)
        *  We can detect this code in the activity that will open by this we can get
        *  Which notification opened the activity
        * */
        PendingIntent pendingIntent = PendingIntent.getActivity(
                mCtx,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        /*
        *  Setting the pending intent to notification builder
        * */

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) mCtx.getSystemService(NOTIFICATION_SERVICE);

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        /*
        * The first parameter is the notification id
        * better don't give a literal here (right now we are giving a int literal)
        * because using this id we can modify it later
        * */
        if (mNotifyMgr != null) {
            mNotifyMgr.notify(Constants.NOTIFICATION_TAG, notification);
        }
    }

}