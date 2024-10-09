package org.example.tlv

sealed class TLV(val tag: String?, val length: String?) {
    abstract fun serialize(): String
}