package hexlink.iota

import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.IotaType
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry


class BlockTypeIota(val blocktype: Block) : Iota(TYPE, blocktype){
    
    fun getBlockType(): Block{
        return payload as Block
    }

    override fun toleratesOther(that: Iota): Boolean {
        return that is BlockTypeIota && that.getBlockType()==getBlockType()
    }

    override fun isTruthy(): Boolean {
        return getBlockType()!=Blocks.AIR
    }

    override fun serialize(): NbtElement {
        return NbtString.of(Registry.BLOCK.getId(getBlockType()).toString())
    }

    companion object{
        val TYPE=object : IotaType<BlockTypeIota>(){
            override fun color(): Int {
                return 0x662200
            }
            
            override fun typeName(): Text {
                val key = HexIotaTypes.REGISTRY.getId(this)
                return Text.translatable("hexcasting.iota.$key") .styled({ style -> style.withColor(TextColor.fromRgb(color()))})
            }

            override fun deserialize(tag: NbtElement, world: ServerWorld): BlockTypeIota {
                if(tag is NbtString){
                    val type=Registry.BLOCK.getOrEmpty(Identifier(tag.asString())).orElseThrow(::IllegalArgumentException)
                    return BlockTypeIota(type)
                }
                else throw IllegalArgumentException()
            }

            override fun display(tag: NbtElement): Text {
                if(tag is NbtString){
                    val type=Registry.BLOCK.getOrEmpty(Identifier(tag.asString()))
                    if(!type.isEmpty()){
                        return type.get().getName()
                    }
                }
                return Text.of("Invalid Type")
            }
        }
    }

}
