package br.com.ajeferson.combat.view.service.message

/**
 * Created by ajeferson on 02/03/2018.
 */
data class Message(
        val kind: MessageKind,
        val tokens: List<Any> = listOf()) {

    override fun toString() = tokens.map { it }
            .toMutableList()
            .apply { add(0, kind) }
            .joinToString(SEPARATOR)

    companion object {

        private const val SEPARATOR = "|"

        fun from(raw: String): Message {
            val tokens = raw.split(SEPARATOR)
            return Message(MessageKind.valueOf(tokens[0]), tokens.toMutableList().apply { removeAt(0) })
        }

    }

}