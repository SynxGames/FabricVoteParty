package games.synx.fabricvoteparty

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.vexsoftware.votifier.fabric.event.VotifierEvent
import games.synx.fabricvoteparty.config.ConfigManager
import games.synx.fabricvoteparty.config.VotePartyConfig
import games.synx.fabricvoteparty.config.getConfig
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.kyori.adventure.platform.fabric.FabricServerAudiences
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.MinecraftServer
import java.text.NumberFormat

object FabricVoteParty : ModInitializer {

    lateinit var audienceProvider: FabricServerAudiences
    lateinit var minecraftServer: MinecraftServer

    var votePartyCount = 0

    override fun onInitialize() {

        ServerLifecycleEvents.SERVER_STARTING.register { server ->
            minecraftServer = server
            audienceProvider = FabricServerAudiences.of(server)

            ConfigManager.initConfigs(VotePartyConfig::class)
        }

        VotifierEvent.EVENT.register { vote ->
            val config = getConfig<VotePartyConfig>()
            votePartyCount++

            config.rewardMessage.sendToServer(
                "player",
                vote.username,
                "count",
                votePartyCount,
                "required",
                config.requiredCount,
                "percent",
                NumberFormat.getInstance().format(votePartyCount.toDouble() / config.requiredCount.toDouble() * 100)
                    .toInt()
            )

            if (votePartyCount == config.requiredCount) {
                votePartyCount = 0
                config.reachedMessage.sendToServer()

                val serverStack = minecraftServer.createCommandSourceStack()
                minecraftServer.playerList.players.forEach { player ->

                    config.rewardCommands.forEach { cmd ->
                        minecraftServer.commands.performPrefixedCommand(
                            serverStack,
                            cmd.replace("%player%", player.name.string)
                        )
                    }

                }
            }
        }

        CommandRegistrationCallback.EVENT.register { dispatcher, context, selection ->

            dispatcher.register(
                LiteralArgumentBuilder.literal<CommandSourceStack>("voteparty")
                    .executes { ctx ->

                        val player = ctx.source.playerOrException
                        val votePartyConfig = getConfig<VotePartyConfig>()

                        votePartyConfig.commandMessage.send(
                            audienceProvider.audience(player),
                            "count",
                            votePartyCount,
                            "required",
                            votePartyConfig.requiredCount,
                            "percent",
                            NumberFormat.getInstance()
                                .format(votePartyCount.toDouble() / votePartyConfig.requiredCount.toDouble() * 100)
                                .toInt()
                        )

                        Command.SINGLE_SUCCESS
                    }
            )
        }
    }

}