package com.example.watermeapp
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class WaterViewModel(application: Application) : AndroidViewModel(application) {

    private val workManager = WorkManager.getInstance(application)
    val plants = DataSource.plants

    fun scheduleReminder(duration: Long, unit: TimeUnit, plantName: String) {
        // 1. Đóng gói dữ liệu đầu vào (tên cây)
        val data = Data.Builder()
            .putString(WaterReminderWorker.NAME_KEY, plantName)
            .build()

        // 2. Tạo yêu cầu chạy 1 lần sau khoảng thời gian delay
        val workRequest = OneTimeWorkRequestBuilder<WaterReminderWorker>()
            .setInitialDelay(duration, unit)
            .setInputData(data)
            .build()

        // 3. Đưa vào hàng đợi theo tên cây (Nếu đặt lại lịch cho cùng 1 cây sẽ ghi đè lịch cũ)
        workManager.enqueueUniqueWork(
            plantName,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}