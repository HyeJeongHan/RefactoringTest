package com.hjhan.moduletest.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {

    // SimpleDateFormat은 스레드 안전하지 않은데 싱글턴으로 사용 중 - 레거시 버그
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun formatTimestamp(timestamp: Long): String {
        return try {
            sdf.format(Date(timestamp))
        } catch (e: Exception) {
            "-"
        }
    }

    fun getRelativeTimeString(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        return when {
            diff < 0 -> "방금 전"
            diff < 60_000 -> "방금 전"
            diff < 3_600_000 -> "${diff / 60_000}분 전"
            diff < 86_400_000 -> "${diff / 3_600_000}시간 전"
            else -> "${diff / 86_400_000}일 전"
        }
    }
}
