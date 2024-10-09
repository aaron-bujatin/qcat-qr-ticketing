package org.example.helper


import org.example.helper.TLVCreationHelper.Companion.createTLVStructure
import org.example.model.TicketDataPayload
import org.example.util.Base64EncoderUtil.Companion.toBase64String
import org.example.util.QrCodeImageUtil.Companion.toQrCodeImage


class BeepQrTicketHelper(
//    private val tlvCreationHelper: TLVCreationHelper,
//    private val base64EncoderUtil: Base64EncoderUtil,
//    private val qrCodeImageUtil: QrCodeImageUtil
) {

    //call the createTLVStructure from the TLVCreationHelper. It will return a Constructed Object
    fun createQrCodeTicket(ticketDataPayload: TicketDataPayload): String{
        val serializedTLV = createTLVStructure(ticketDataPayload).serialize()
        val base64EncodedData = toBase64String(serializedTLV)
        val qrCodeImage = toQrCodeImage(base64EncodedData)
        return qrCodeImage
    }
}