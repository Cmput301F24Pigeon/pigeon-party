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
     * Allows notification to be sent to entrants based on their status
     * @param user
     * @param event
     * @param message
     */
    public void notifyUser(User user, Event event, String message) {
        int notificationId = (user.getUniqueId() + event.getEventId()).hashCode(); //this way a new notification doesnt ovveride the previous
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Event Notification")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());

    }
}
