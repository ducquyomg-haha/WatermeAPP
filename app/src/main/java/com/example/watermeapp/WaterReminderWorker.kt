package com.example.watermeapp
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class WaterReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        // Lấy tên cây được truyền vào từ WorkManager
        val plantName = inputData.getString(NAME_KEY) ?: return Result.failure()

        makePlantReminderNotification(
            message = "Đã đến lúc tưới nước cho cây $plantName rồi!",
            context = applicationContext
        )

        return Result.success()
    }

    private fun makePlantReminderNotification(message: String, context: Context) {
        // 1. Tạo NotificationChannel (Bắt buộc từ Android 8.0 / API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Nhắc nhở tưới cây",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Thông báo nhắc lịch tưới nước cho cây"
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // 2. Tạo Intent để mở lại ứng dụng khi người dùng nhấn vào thông báo
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // 3. Xây dựng nội dung thông báo
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Water Me!")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // 4. Kiểm tra quyền trước khi hiển thị thông báo
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        ) {
            NotificationManagerCompat.from(context)
                .notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

    companion object {
        const val NAME_KEY = "NAME"
        const val CHANNEL_ID = "WATER_REMINDER_CHANNEL"
    }
}