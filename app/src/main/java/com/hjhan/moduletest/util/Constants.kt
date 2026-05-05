package com.hjhan.moduletest.util

object Constants {

    // RetrofitClient에도 동일한 URL이 있음 - 중복 (레거시 문제)
    const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    // 하드코딩된 테스트 계정 - 보안 취약점
    const val TEST_USERNAME = "admin"
    const val TEST_PASSWORD = "1234"

    const val DEFAULT_CACHE_DURATION_MS = 5 * 60 * 1000L // 5분

    // Intent extra 키
    const val EXTRA_USER_ID = "user_id"
    const val EXTRA_USER_NAME = "user_name"
    const val EXTRA_USER_EMAIL = "user_email"
}
