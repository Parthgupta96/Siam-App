package in.siamdtu.parth.siamapp;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class CurrentQuestionService extends FirebaseMessagingService {
    private static final String TAG = "CurrentQuestion";

    public CurrentQuestionService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());


//            Intent intent1 = new Intent(CurrentQuestionService.this, LoginActivity.class);
//            PendingIntent pIntent = PendingIntent.getActivity(CurrentQuestionService.this, 0, intent1, 0);
//
//            NotificationCompat.Builder builder1 = new NotificationCompat.Builder(CurrentQuestionService.this)
//                    .setContentTitle("Question Of The Day")
//                    .setContentText("Solve Now")
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setContentIntent(pIntent)
//                    .setAutoCancel(true)
//                    .setCategory(Notification.CATEGORY_ALARM);
//
//
//            long[] pattern = {100, 500, 250, 500, 500};
//            builder1.setVibrate(pattern);
//
//            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            builder1.setSound(alarmSound);
//
//
//            NotificationManager notificationManager =
//                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//            Notification notification = builder1.build();
//            notification.flags = Notification.FLAG_SHOW_LIGHTS;
//
//            notification.ledARGB = Color.WHITE;
//            notification.ledOnMS = 100;
//            notification.ledOffMS = 50;
//            notificationManager.notify(0, notification);

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Bundle bundle = intent.getExtras();
//        final PendingIntent pendingIntent = bundle.getParcelable("pendingIntent");
//        Intent intent1 = new Intent(CurrentQuestionService.this, LoginActivity.class);
//        PendingIntent pIntent = PendingIntent.getActivity(CurrentQuestionService.this, 0, intent1, 0);
//
//
//        NotificationCompat.Builder builder1 = new NotificationCompat.Builder(CurrentQuestionService.this)
//                .setContentTitle("Question Of The Day")
//                .setContentText("Solve Now")
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentIntent(pIntent)
//                .setAutoCancel(true)
//                .setCategory(Notification.CATEGORY_ALARM);
//
//
//        long[] pattern = {100,500,250,500,500};
//        builder1.setVibrate(pattern);
//
//        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        builder1.setSound(alarmSound);
//
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        Notification notification = builder1.build();
//        notification.flags = Notification.FLAG_SHOW_LIGHTS;
//
//        notification.ledARGB = Color.WHITE;
//        notification.ledOnMS = 100;
//        notification.ledOffMS = 50;
//        notificationManager.notify(0, notification);
//
////        Log.v("service", "in service");
//        return super.onStartCommand(intent, flags, startId);
//
//    }
}
