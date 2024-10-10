package org.example
import org.example.model.TicketDataPayload

fun main(){

    val ticketDataPayload = TicketDataPayload(
        ticketId = 1250184,
        creatorId = 275,
        creationTime = "2019-09-11T19:38:30+08:00",
        validityPeriod = "PT15M",
        signature = "023034021859527B7951E77EB6CB250149FFA2006B1A415297D13AA48A021840986DC05DB2235088DB4599389823A324842E73A635B3FD"
    )


    val v1= QCATQrTicketing()
    val v2= QCATQrTicketingTwo()

    val result1 = v1.BuildQr()
        .setTicketId(ticketDataPayload.ticketId)
        .setCreatorId(ticketDataPayload.creatorId)
        .setCreationTime(ticketDataPayload.creationTime)
        .setValidityPeriod(ticketDataPayload.validityPeriod)
        .setSignature(ticketDataPayload.signature)
        .generate()

    val result2 = v2.BuildQr()
        .setTicketId(ticketDataPayload.ticketId)
        .setCreatorId(ticketDataPayload.creatorId)
        .setCreationTime(ticketDataPayload.creationTime)
        .setValidityPeriod(ticketDataPayload.validityPeriod)
        .setSignature(ticketDataPayload.signature)
        .generate()

    println("resultv1 : $result1")
    println("resultv2 : $result2")

}