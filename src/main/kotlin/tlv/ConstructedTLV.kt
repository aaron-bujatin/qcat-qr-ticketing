package org.example.tlv

class ConstructedTLV(tag: String? = null, length: String? = null, val value: List<TLV>): TLV(tag, length) {
    override fun serialize(): String {
        val nestedValues = value.joinToString("") { it.serialize() }
        return if (tag != null && length != null) {
            String.format("%s%s%s", tag, length, nestedValues)
        } else {
            String.format("%s", nestedValues)
        }
    }
}