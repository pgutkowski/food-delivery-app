package com.github.pgutkowski.fda.order.controller

import com.github.pgutkowski.fda.order.Order

data class UpdateStatusRequest(val newStatus: Order.Status)