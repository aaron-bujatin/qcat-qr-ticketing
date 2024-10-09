package org.example.tlv

class SimpleTLV(tag: String?, length: String?, val value: String): TLV(tag, length) {

    override fun serialize(): String {
        return if (tag != null && length != null){
            String.format("%s%s%s", tag, length, value)
        } else {
            String.format("%s", value)
        }
    }
}