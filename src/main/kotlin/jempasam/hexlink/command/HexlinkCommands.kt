package jempasam.hexlink.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.cc.HexlinkLevelData
import jempasam.hexlink.world.LevelRanks
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.command.CommandException
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.command.argument.RegistryKeyArgumentType
import net.minecraft.command.argument.RegistryKeyArgumentType.registryKey
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.RegistrationEnvironment
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import java.util.*


object HexlinkCommands{

    fun registerCommands(){
        CommandRegistrationCallback.EVENT.register(::commandRegistration)
    }

    fun <T>rankArgument(key: RegistryKey<Registry<T>>): RegistryKeyArgumentType<T> = registryKey(key)

    fun getRank(context: CommandContext<ServerCommandSource>, registry: Registry<LevelRanks.Rank>, name: String): LevelRanks.Rank {
        val key = context.getArgument(name, RegistryKey::class.java)
        val castedKey=key.tryCast(registry.key).orElseThrow {throw CommandException(Text.translatable("argument.id.invalid"))}
        val obj=registry.get(castedKey)
        return obj ?: throw CommandException(Text.translatable("argument.id.invalid"))
    }

    private fun commandRegistration(dispatcher: CommandDispatcher<ServerCommandSource>, registryAccess: CommandRegistryAccess, environment: RegistrationEnvironment){
        dispatcher.register(literal("magicrank")
                .requires { it.hasPermissionLevel(3) }
                .then(literal("rank")
                        .then(CommandManager.argument("rank", registryKey(HexlinkRegistry.RANK_KEY))
                                .then(literal("owner")
                                        .then(literal("get")
                                                .executes{
                                                    val rank= getRank(it, HexlinkRegistry.RANK,"rank")
                                                    val data= it.source.world.levelProperties.getComponent(HexlinkLevelData.KEY)
                                                    val player=data.getPlayer(rank)
                                                    if(player!=null){
                                                        val name = it.source.server.playerManager.getPlayer(player)
                                                                ?.entityName
                                                                ?.let { Text.selector(it, Optional.empty()) }
                                                                ?: Text.of(player.toString())
                                                        it.source.sendFeedback(
                                                                name.copy().append(Text.translatable("hexlink.text.is")).append(rank.getName()),
                                                                false
                                                        )
                                                        1
                                                    }
                                                    else{
                                                        it.source.sendFeedback(Text.translatable("no.owner"), false)
                                                        0
                                                    }
                                                }
                                        )
                                        .then(literal("set")
                                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                                        .executes{
                                                            val rank= getRank(it, HexlinkRegistry.RANK,"rank")
                                                            val player= EntityArgumentType.getPlayer(it, "player")
                                                            val data= it.source.world.levelProperties.getComponent(HexlinkLevelData.KEY)
                                                            data.setRank(player.uuid,rank)
                                                            0
                                                        }
                                                )
                                        )
                                        .then(literal("clear")
                                                .executes{
                                                    val rank= getRank(it, HexlinkRegistry.RANK,"rank")
                                                    val data= it.source.world.levelProperties.getComponent(HexlinkLevelData.KEY)
                                                    data.setPlayer(rank,null)
                                                    0
                                                }
                                        )
                                )
                        )
                )
                .then(literal("ranks")
                        .executes{
                            val context=it
                            val data= context.source.world.levelProperties.getComponent(HexlinkLevelData.KEY)
                            val text=Text.empty()
                            for(rank in data.ranks()){
                                val player=data.getPlayer(rank)
                                text.append(rank.getName())
                                if(player!=null){
                                    val name=player
                                            .let { context.source.server.playerManager.getPlayer(it) }
                                            ?.entityName
                                            ?.let { Text.selector(it, Optional.empty()) }
                                            ?: Text.of(player.toString())
                                    text.append(" : ").append(name)
                                }
                                text.append("\n")
                            }
                            context.source.sendFeedback(text, false)
                            0
                        }
                )

        )
    }

}