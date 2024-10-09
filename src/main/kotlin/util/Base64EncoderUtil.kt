package org.example.util

import org.example.util.HexStringUtil.Companion.hexStringToByteArray
import java.util.Base64

class Base64EncoderUtil {
    companion object {
        fun toBase64String(hexString: String): String {
            return Base64.getEncoder().encodeToString(hexString.hexStringToByteArray())
        }
    }
}