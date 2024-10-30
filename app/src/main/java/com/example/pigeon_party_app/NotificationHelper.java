package com.example.pigeon_party_app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;

// https://stackoverflow.com/questions/48198954/creating-a-notification-with-notification-channel-gives-no-effect
// Stack thread on creating a Notification class

/**
 * This class aids in sending notifications to Users
 */
public class NotificationHelper
{
    private static final String CHANNEL_ID = "event_channel";
    private Context context;

    public NotificationHelper(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    /**
     * Allows the system to group and manage notifications based on user preferences/choices.
     */
    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "Event Channel";
            String description = "Notifications for event selection";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Lets an entrant know by notification if they've been selected for an event or not.
     * @param user
     * @param event
     */
    //Added condition to check if user has notifications turned on
    /*public void notifyUserIfChosen(User user, Event event) {
         if (event.isChosen() && user.isEntrant() && user.hasNotificationsOn()) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Great news!")
                   .setContentText("You have been chosen for " + event.getEventTitle() + "!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, builder.build());
        }
    }*/
}
