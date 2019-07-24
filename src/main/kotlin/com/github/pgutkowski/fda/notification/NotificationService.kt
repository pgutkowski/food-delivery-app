package com.github.pgutkowski.fda.notification

import com.github.kittinunf.result.coroutines.SuspendableResult
import org.springframework.stereotype.Service

@Service
class NotificationService(private val notificationListeners: List<NotificationListener>) {

    suspend fun <T> sendNotification(notification: Notification<T>): SuspendableResult<Unit, Exception> {
        return SuspendableResult.of {
            notificationListeners.forEach { it.fire(notification) }
        }
    }

}