package com.example.watermeapp

import java.util.concurrent.TimeUnit

data class Plant(
    val name: String,
    val schedule: String,
    val type: String,
    val description: String
)

data class Reminder(
    val durationRes: String,
    val duration: Long,
    val unit: TimeUnit,
    val plantName: String
)

object DataSource {
    val plants = listOf(
        Plant(
            name = "Lithops (Sen đá sỏi)",
            schedule = "Mỗi tháng một lần",
            type = "Xương rồng & Sen đá",
            description = "Cây mọng nước có hình dáng giống những viên sỏi nhỏ."
        ),
        Plant(
            name = "Carrot (Cà rốt)",
            schedule = "Hàng ngày",
            type = "Rau củ",
            description = "Cây lấy củ cần giữ ẩm đất đều đặn để phát triển."
        ),
        Plant(
            name = "Peony (Mẫu đơn)",
            schedule = "Hàng tuần",
            type = "Hoa cảnh",
            description = "Cây hoa nở rộ vào mùa xuân, ưa đất thoát nước tốt."
        ),
        Plant(
            name = "Pothos (Trầu bà)",
            schedule = "Hàng tuần",
            type = "Cây nội thất",
            description = "Cây dây leo dễ sống trong nhà, chịu bóng bán phần."
        ),
        Plant(
            name = "Fiddle Leaf Fig (Bàng Singapore)",
            schedule = "Hàng tuần",
            type = "Cây thân gỗ trong nhà",
            description = "Cây lá lớn bóng đẹp, cần tưới khi lớp đất mặt khô."
        ),
        Plant(
            name = "Strawberry (Dâu tây)",
            schedule = "Hàng ngày",
            type = "Cây ăn quả",
            description = "Cây thân thảo cần nhiều nước và ánh nắng để ra quả."
        )
    )
}