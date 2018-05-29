package mad.lab1.Notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import mad.lab1.R;

/**
 * Created by Matteo on 16/05/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //if the message contains data payload
        //It is a map of custom keyvalues
        //we can read it easily
        //if(remoteMessage.getData().size() > 0){
            //handle the data message here
            //Log.d(TAG, "Refreshed message: " + remoteMessage.getData().size());
        //
        // }

        //getting the title and the body
        //String title = remoteMessage.getNotification().getTitle();
        //String body = remoteMessage.getNotification().getBody();
        String type = remoteMessage.getData().get("type");
        String title;
        String body;

        switch (type) {
            case Constants.MESSAGE:
                title = remoteMessage.getData().get("title");
                body = remoteMessage.getData().get("body");
                String user2 = remoteMessage.getData().get("user2");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel mChannel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, importance);
                    mChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
                    mChannel.enableLights(true);
                    mChannel.setLightColor(Color.WHITE);
                    mChannel.enableVibration(true);
                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    mNotificationManager.createNotificationChannel(mChannel);
                }


                MyNotificationManager.getInstance(this).displayNotification(title, body, user2);
                break;

            case Constants.NEWBOOKING:
                title = remoteMessage.getData().get("title");
                body = remoteMessage.getData().get("body");
                String bookTitle = remoteMessage.getData().get("bookTitle");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel mChannel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, importance);
                    mChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
                    mChannel.enableLights(true);
                    mChannel.setLightColor(Color.WHITE);
                    mChannel.enableVibration(true);
                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    mNotificationManager.createNotificationChannel(mChannel);
                }


                MyNotificationManager.getInstance(this).displayNotificationNewRequest(title, body, bookTitle);
                break;
        }

    }
}
