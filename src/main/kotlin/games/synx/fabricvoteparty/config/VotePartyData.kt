package games.synx.fabricvoteparty.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class VotePartyData(
    var count: Int = 0
)