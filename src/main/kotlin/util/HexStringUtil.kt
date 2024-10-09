package org.example.util

import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class HexStringUtil {

    companion object {

        fun Int.toHexString(): String {
            return this.toString(16).uppercase().let { if(it.length % 2 != 0) "0$it" else it }
        }

        fun String.toISO8601Duration(): Duration {
            return  Duration.parse(this)
        }

        fun Duration.toHexString(): String {
            return this.seconds.toString(16).uppercase().padStart(4,'0')
        }

        fun String.toISO8601DateTime(): OffsetDateTime {
            return OffsetDateTime.parse(this, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        }

        fun OffsetDateTime.toHexString(): String {
            return this.toEpochSecond().toString(16).uppercase()
        }

        fun String.toHexString(): String{
            return this.map { it.code.toString(16).uppercase() }.joinToString("")
        }

        fun Int.lengthToHexString(): String {
            val bytes = when{
                this < 128 -> byteArrayOf(this.toByte())
                this <= 255 -> byteArrayOf(0x81.toByte(), this.toByte())
                else -> byteArrayOf(0x82.toByte(), (this shr 8).toByte(), this.toByte())
            }
            return bytes.joinToString (""){ String.format("%02X", it) }
        }

        fun String. hexStringToByteArray(): ByteArray {
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
    }
}