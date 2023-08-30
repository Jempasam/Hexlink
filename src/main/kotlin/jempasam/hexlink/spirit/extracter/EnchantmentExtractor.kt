package jempasam.hexlink.spirit.extracter

import jempasam.hexlink.spirit.EnchantmentSpirit
import jempasam.hexlink.spirit.StackHelper
import jempasam.hexlink.utils.EnchantHelper
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.math.ColorHelper
import kotlin.math.sin

object EnchantmentExtractor : SpiritExtractor<EnchantmentSpirit> {

    override fun getName(): Text = Text.translatable("hexlink.extractor.enchantment")

    override fun getColor(): Int{
        val currentPiTime=(System.currentTimeMillis()%2000)/2000f*Math.PI*2
        return ColorHelper.Argb.getArgb(255, 255, (sin(currentPiTime)*127+127).toInt(), 255)
    }

    override fun extract(caster: PlayerEntity?, target: Entity): SpiritExtractor.ExtractionResult<EnchantmentSpirit> {
        val worldStack=StackHelper.stack(caster,target)
        if(worldStack==null)return SpiritExtractor.noResult()
        val enchantments=EnchantmentHelper.get(worldStack.stack)
        if(enchantments.size==0)return SpiritExtractor.noResult()
        val extracted=enchantments.entries.first()
        return SpiritExtractor.ExtractionResult(EnchantmentSpirit(extracted.key), extracted.value*extracted.value) {
            worldStack.replace(EnchantHelper.removeEnchantment(worldStack.stack, extracted.key))
        }
    }
}