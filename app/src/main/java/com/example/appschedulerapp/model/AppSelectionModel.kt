package com.example.appschedulerapp.model

import kotlinx.serialization.Serializable

@Serializable
data class AppSelectionModel(
    val id: Int?,
    val appName: String?,
    val appPackageName: String?,
    val note: String?,
    val dateTime: String?,
    val year: String?,
    val month: String?,
    val day: String?,
    val hour: String?,
    val minute: String?,
    val second: String?,
    val isRepeatable: String?,
    val isExecuted: String?
)