package org.example

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import org.example.model.TicketDataPayload
import java.io.ByteArrayOutputStream
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class QCATQrTicketing {
        private val PAYLOAD_FORMAT_INDICATOR = "85"
        private val APPLICATION_TEMPLATE = "61"
        private val ADF = "4F"
        private val APPLICATION_SPECIFIC_TRANSPARENT_TEMPLATE = "63"

        private val TICKET_ID = "C1"
        private val CREATOR_ID = "C2"
        private val CREATION_TIME = "C3"
        private val VALIDITY_PERIOD = "C4"

        private val SIGNATURE = "DE"

        private val BASE64_DATA_IMAGE_PREFIX = "data:image/png;base64"

        private fun Int.toHexString(): String {
            return this.toString(16).uppercase().let { if(it.length % 2 != 0) "0$it" else it }
        }

        private fun String.toISO8601Duration(): Duration {
            return  Duration.parse(this)
        }

        private fun Duration.toHexString(): String {
            return this.seconds.toString(16).uppercase().padStart(4,'0')
        }

        private fun String.toISO8601DateTime(): OffsetDateTime {
            return OffsetDateTime.parse(this, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        }

        private fun OffsetDateTime.toHexString(): String {
            return this.toEpochSecond().toString(16).uppercase()
        }

        private fun String.toHexString(): String{
            return this.map { it.code.toString(16).uppercase() }.joinToString("")
        }

        private fun Int.lengthToHexString(): String {
            val bytes = when{
                this < 128 -> byteArrayOf(this.toByte())
                this <= 255 -> byteArrayOf(0x81.toByte(), this.toByte())
                else -> byteArrayOf(0x82.toByte(), (this shr 8).toByte(), this.toByte())
            }
            return bytes.joinToString (""){ String.format("%02X", it) }
        }

        private fun String. hexStringToByteArray(): ByteArray {
            require(this.length % 2 == 0) { "hex string must have an even length" }

            val len = this.length
            val data = ByteArray(len / 2)
            var i = 0

            while (i < len) {
                val firstChar = Character.digit(this[i], 16)
                val secondChar = Character.digit(this[i + 1], 16)

                require(firstChar != -1 && secondChar != -1) { "invalid hex character at index $i" }

                data[i / 2] = ((firstChar shl 4) + secondChar).toByte()

                i += 2
            }
            return data
        }

        private fun toBase64String(hexString: String): String {
            return Base64.getEncoder().encodeToString(hexString.hexStringToByteArray())
        }

        private fun toQrCodeImage(base64String: String): String {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(base64String, BarcodeFormat.QR_CODE,512, 512)
            val stream = ByteArrayOutputStream()

            MatrixToImageWriter.writeToStream(bitMatrix, "png", stream)
            stream.close()

            return "$BASE64_DATA_IMAGE_PREFIX, ${Base64.getEncoder().encodeToString(stream.toByteArray())}"
        }
    

    private sealed class TLV(val tag: String?) {
        abstract fun serialize(): String
    }

    private inner class SimpleTLV(tag: String?, val value: String): TLV(tag) {
        override fun serialize(): String {
            val length = (value.toHexString().length / 2).lengthToHexString()
            return if (tag != null){
                String.format("%s%s%s", tag, length, value)
            } else {
                String.format("%s", value)
            }
        }
    }

    private inner class ConstructedTLV(tag: String? = null, val value: List<TLV>): TLV(tag) {
        override fun serialize(): String {
            val nestedValues = value.joinToString("") { it.serialize() }
            val length = value.sumOf {
                it.serialize().length / 2
            }.lengthToHexString()
            return if (tag != null) {
                String.format("%s%s%s", tag, length, nestedValues)
            } else {
                String.format("%s", nestedValues)
            }
        }
    }

    fun generateQrCode(ticketId: Int?,
                       creatorId: Int?,
                       creationTime: String?,
                       validityPeriod: String?,
                       signature: String?): String {

        val ticketIdHex = ticketId!!.toHexString()
        val creatorIdHex =creatorId?.toHexString()
        val creationTimeHex = creationTime?.toISO8601DateTime()?.toHexString()
        val validityPeriodHex = validityPeriod?.toISO8601Duration()?.toHexString()
        val signatureHex = signature

        val adf = "QCAT01"
        val ADFHex = adf.toHexString()

        val appSpecsTransTemplate =
            listOf(
                SimpleTLV(
                    tag = TICKET_ID,
                    value = ticketIdHex
                ),
                SimpleTLV(
                    tag = CREATOR_ID,

                    value = creatorIdHex!!
                ),
               SimpleTLV(
                    tag = CREATION_TIME,

                    value = creationTimeHex!!
                ),
                SimpleTLV(
                    tag = VALIDITY_PERIOD,

                    value = validityPeriodHex!!
                ),
                SimpleTLV(
                    tag = SIGNATURE,
                    value = signatureHex!!
                )
            )

        val appTemplate =
            listOf(
                SimpleTLV(
                    tag = ADF,
                    value = ADFHex
                ),
                ConstructedTLV(
                    tag = APPLICATION_SPECIFIC_TRANSPARENT_TEMPLATE,

                    value = appSpecsTransTemplate
                )
            )

        val payloadFormatIndicator = "CPV01"
        val payloadFormatIndicatorHex = payloadFormatIndicator.toHexString()

        val serializedTlv =  ConstructedTLV(
            null, listOf(
                SimpleTLV(
                    tag = PAYLOAD_FORMAT_INDICATOR,
                    value = payloadFormatIndicatorHex
                ),
                ConstructedTLV(
                    tag = APPLICATION_TEMPLATE,
                    value = appTemplate
                )
            )
        ).serialize()

        val base64EncodedData = toBase64String(serializedTlv)
        val qrCodeImage = toQrCodeImage(base64EncodedData)
        return qrCodeImage
    }

    inner class BuildQr{
        private var ticketId: Int? = null
        private var creatorId: Int? = null
        private var creationTime: String? = null
        private var validityPeriod: String? = null
        private var signature: String? = null

        fun setTicketId(ticketId: Int?) = apply { this.ticketId = ticketId }
        fun setCreatorId(creatorId: Int?) = apply { this.creatorId = creatorId }
        fun setCreationTime(creationTime: String?) = apply { this.creationTime = creationTime }
        fun setValidityPeriod(validityPeriod: String?) = apply { this.validityPeriod = validityPeriod }
        fun setSignature(signature: String?) = apply { this.signature = signature }

        fun generate() = generateQrCode(ticketId, creatorId, creationTime, validityPeriod, signature)
    }
}