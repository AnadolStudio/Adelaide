package com.anadolstudio.adelaide.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.RingtoneManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.anadolstudio.adelaide.R

class NotificationUtil {

    companion object {
        fun sendNotification(activity: AppCompatActivity, messageBody: String) {
            /*val intent = Intent(this, OpenActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(
                this, 0 *//* Request code *//*, intent,
            PendingIntent.FLAG_ONE_SHOT
        )*/
            val channelId = "12345"
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(activity, channelId)
                    .setSmallIcon(R.drawable.ic_notification)
//                .setColor(getColor(R.color.colorAccent))
                    .setContentTitle(activity.getString(R.string.app_name))
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
            /*.setContentIntent(pendingIntent)*/
            val notificationManager =
                activity.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(
                12345/* ID of notification */,
                notificationBuilder.build()
            )
        }
    }
}