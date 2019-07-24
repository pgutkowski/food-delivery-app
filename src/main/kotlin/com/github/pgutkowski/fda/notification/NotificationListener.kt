package com.github.pgutkowski.fda.notification

interface NotificationListener {
    suspend fun <T> fire(notification: Notification<T>)
}