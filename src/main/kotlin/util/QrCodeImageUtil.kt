package org.example.util

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream
import java.util.*

class QrCodeImageUtil {
    companion object {

        private const val BASE64_DATA_IMAGE_PREFIX = "data:image/png;base64"

        fun toQrCodeImage(base64String: String): String {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(base64String, BarcodeFormat.QR_CODE,512, 512)
            val stream = ByteArrayOutputStream()

            MatrixToImageWriter.writeToStream(bitMatrix, "png", stream)
            stream.close()

            return "${BASE64_DATA_IMAGE_PREFIX}, ${Base64.getEncoder().encodeToString(stream.toByteArray())}"
        }

    }
}