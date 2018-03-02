package br.com.ajeferson.combat.view.service.message

import br.com.ajeferson.combat.view.view.enumeration.Owner

/**
 * Created by ajeferson on 02/03/2018.
 */
data class Message(
        var senderId: Long = -1L,
        val kind: MessageKind,
        val tokens: MutableList<String> = mutableListOf()) {

    var owner = Owner.SERVER

    fun setOwnerFromId(id: Long) {
        owner = when {
            senderId < 0 -> Owner.SERVER
            id == senderId -> Owner.SELF
            else -> Owner.OPPONENT
        }
    }

    fun addValues(vararg values: Any) {
        values.forEach { tokens.add(it.toString()) }
    }

    override fun toString() = tokens.map { it }
            .toMutableList()
            .apply { add(0, senderId.toString()) }
            .apply { add(1, kind.toString()) }
            .joinToString(SEPARATOR)

    companion object {

        private const val SEPARATOR = "|"

        fun from(raw: String): Message {
            val tokens: List<String> = raw.split(SEPARATOR)
            return Message(
                    senderId = tokens[0].toLong(),
                    kind = MessageKind.valueOf(tokens[1] as? String ?: ""),
                    tokens = tokens
                            .toMutableList()
                            .apply { removeAt(0); } // Remove the sender id
                            .apply { removeAt(0) }  // Remote the Kind
            )
        }

    }

}