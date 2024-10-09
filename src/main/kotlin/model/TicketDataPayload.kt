package org.example.model

data class TicketDataPayload(
    val ticketId: Int? = null,
    val creatorId: Int? = null,
    val creationTime: String? = null,
    val validityPeriod: String? = null,
    val signature: String? = null
)
