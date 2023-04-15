package games.synx.fabricvoteparty.config

import games.synx.fabricvoteparty.FabricVoteParty
import games.synx.fabricvoteparty.util.parse
import net.kyori.adventure.audience.Audience
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class VotePartyConfig(
    val requiredCount: Int = 200,
    val rewardCommands: List<String> = listOf(
        "give %player% minecraft:diamond 1"
    ),
    val commandMessage: ParseableMessage = ParseableMessage("<green> The VoteParty is currently at <count>/<required> (<percent>%)"),
    val reachedMessage: ParseableMessage = ParseableMessage("<green> The VoteParty has been reached! Everyone gets a Vote Key!"),
    val rewardMessage: ParseableMessage = ParseableMessage("<green><player> has voted so 1x vote has been added to VP <count>/<required> (<percent>%)"),
)

@ConfigSerializable
data class ParseableMessage(
    val message: String = ""
) {

    fun send(audience: Audience, vararg placeholders: Any) {
        if(message.isNotEmpty()) {
            audience.sendMessage(parse(message, *placeholders))
        }
    }

    fun sendToServer(vararg placeholders: Any) {

        if(message.isNotEmpty()) {
            FabricVoteParty.audienceProvider.all().sendMessage(parse(message, *placeholders))
        }
    }

}