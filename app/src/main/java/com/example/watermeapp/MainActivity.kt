package com.example.watermeapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WaterMeApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterMeApp(waterViewModel: WaterViewModel = viewModel()) {
    val context = LocalContext.current
    var selectedPlant by remember { mutableStateOf<Plant?>(null) }

    // Xin quyền hiển thị thông báo trên Android 13+ (API 33+)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                Toast.makeText(
                    context,
                    "Bạn cần cấp quyền thông báo để nhận nhắc nhở tưới cây!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Water Me!", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(waterViewModel.plants) { plant ->
                PlantListItem(
                    plant = plant,
                    onItemClick = { selectedPlant = plant }
                )
            }
        }

        // Hiển thị hộp thoại chọn thời gian khi người dùng nhấn vào 1 cây
        selectedPlant?.let { plant ->
            ReminderDialogContent(
                plantName = plant.name,
                onDismiss = { selectedPlant = null },
                onScheduleReminder = { reminder ->
                    waterViewModel.scheduleReminder(
                        duration = reminder.duration,
                        unit = reminder.unit,
                        plantName = reminder.plantName
                    )
                    Toast.makeText(
                        context,
                        "Đã hẹn nhắc tưới ${plant.name} sau ${reminder.durationRes}",
                        Toast.LENGTH_SHORT
                    ).show()
                    selectedPlant = null
                }
            )
        }
    }
}

@Composable
fun PlantListItem(plant: Plant, onItemClick: (Plant) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(plant) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = plant.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Loại: ${plant.type} • Lịch gợi ý: ${plant.schedule}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = plant.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ReminderDialogContent(
    plantName: String,
    onDismiss: () -> Unit,
    onScheduleReminder: (Reminder) -> Unit
) {
    val reminders = listOf(
        Reminder("5 giây (Test nhanh)", 5, TimeUnit.SECONDS, plantName),
        Reminder("1 ngày", 1, TimeUnit.DAYS, plantName),
        Reminder("7 ngày", 7, TimeUnit.DAYS, plantName),
        Reminder("30 ngày", 30, TimeUnit.DAYS, plantName)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Nhắc tưới cây $plantName") },
        text = {
            Column {
                reminders.forEach { reminder ->
                    TextButton(
                        onClick = { onScheduleReminder(reminder) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = reminder.durationRes,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}