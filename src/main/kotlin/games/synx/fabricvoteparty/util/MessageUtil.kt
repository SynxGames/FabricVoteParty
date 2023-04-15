package games.synx.fabricvoteparty.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

/**
 * Parse MiniMessage markdown into a component. This should be used rather than whenever
 * there are placeholders which are of non-String types. This allows cleaner code in implementation,
 * as it removes the need for all values to be a String.
 *
 * @param text         The MiniMessage syntax
 * @param placeholders A key, value array of placeholders.
 * @return The formatted component
 */
fun parse(text: String, vararg placeholders: Any): Component {
    if (placeholders.isNotEmpty()) {
        check(placeholders.size % 2 == 0) { "Placeholders Must be in a key: replacement order, found missing value!" }
        val TAG_BUILDER = TagResolver.builder()
        var i = 0
        while (i < placeholders.size) {
            val key = placeholders[i].toString()

            val value: Component = if(placeholders[i + 1] !is Component) {
                deserialize(placeholders[i + 1].toString())
            } else placeholders[i + 1] as Component

            TAG_BUILDER.tag(key, Tag.selfClosingInserting(value))
            i += 2
        }
        return miniMessage().deserialize(text, TAG_BUILDER.build()).decoration(TextDecoration.ITALIC, false)
    }
    return miniMessage().deserialize(text).decoration(TextDecoration.ITALIC, false)
}

fun deserialize(input: String): Component {
    return miniMessage().deserialize(input).decoration(TextDecoration.ITALIC, TextDecoration.State.NOT_SET)
}