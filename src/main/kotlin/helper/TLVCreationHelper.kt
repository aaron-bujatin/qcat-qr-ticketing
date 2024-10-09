package org.example.helper

import org.example.const.FieldTag
import org.example.model.TicketDataPayload
import org.example.tlv.ConstructedTLV
import org.example.tlv.SimpleTLV
import org.example.tlv.TLV
import org.example.util.HexStringUtil.Companion.lengthToHexString
import org.example.util.HexStringUtil.Companion.toHexString
import org.example.util.HexStringUtil.Companion.toISO8601DateTime
import org.example.util.HexStringUtil.Companion.toISO8601Duration

class TLVCreationHelper {

    companion object {
        fun createTLVStructure(ticketDataPayload: TicketDataPayload): TLV {
            val ticketIdHex = ticketDataPayload.ticketId?.toHexString()
            val creatorIdHex = ticketDataPayload.creatorId?.toHexString()
            val creationTimeHex = ticketDataPayload.creationTime?.toISO8601DateTime()?.toHexString()
            val validityPeriodHex = ticketDataPayload.validityPeriod?.toISO8601Duration()?.toHexString()
            val signatureHex = ticketDataPayload.signature

            val adf = "QCAT01"
            val ADFHex = adf.toHexString()

            val appSpecsTransTemplate =
                listOf(
                    SimpleTLV(
                        tag = FieldTag.TICKET_ID,
                        length = (ticketIdHex!!.length / 2).lengthToHexString(),
                        value = ticketIdHex
                    ),
                    SimpleTLV(
                        tag = FieldTag.CREATOR_ID,
                        length = (creatorIdHex!!.length / 2).lengthToHexString(),
                        value = creatorIdHex
                    ),
                    SimpleTLV(
                        tag = FieldTag.CREATION_TIME,
                        length = (creationTimeHex!!.length / 2).lengthToHexString(),
                        value = creationTimeHex
                    ),
                    SimpleTLV(
                        tag = FieldTag.VALIDITY_PERIOD,
                        length = (validityPeriodHex!!.length / 2).lengthToHexString(),
                        value = validityPeriodHex
                    ),
                    SimpleTLV(
                        tag = FieldTag.SIGNATURE,
                        length = (signatureHex!!.length / 2).lengthToHexString(),
                        value = signatureHex
                    )
                )

            val appTemplate =
                listOf(
                    SimpleTLV(
                        tag = FieldTag.ADF,
                        length = (ADFHex.length / 2).lengthToHexString(),
                        value = ADFHex
                    ),
                    ConstructedTLV(
                        tag = FieldTag.APPLICATION_SPECIFIC_TRANSPARENT_TEMPLATE,
                        length = appSpecsTransTemplate.sumOf {
                            it.serialize().length / 2
                        }.lengthToHexString(),
                        value = appSpecsTransTemplate
                    )
                )

            val payloadFormatIndicator = "CPV01"
            val payloadFormatIndicatorHex = payloadFormatIndicator.toHexString()

            return ConstructedTLV(null, null, listOf(
                SimpleTLV(
                    tag = FieldTag.PAYLOAD_FORMAT_INDICATOR,
                    length = (payloadFormatIndicatorHex.length / 2).lengthToHexString(),
                    value = payloadFormatIndicatorHex
                ),
                ConstructedTLV(
                    tag = FieldTag.APPLICATION_TEMPLATE,
                    length = appTemplate.sumOf { it.serialize().length / 2 }.lengthToHexString(),
                    value = appTemplate
                )
            ))
        }
    }

}