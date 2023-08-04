package jempasam.hexlink.item

import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.common.items.ItemFocus
import at.petrak.hexcasting.xplat.IXplatAbstractions
import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.iota.spiritual.BlockSpiritIota
import jempasam.hexlink.iota.spiritual.EntitySpiritIota
import jempasam.hexlink.iota.spiritual.ItemSpiritIota
import jempasam.hexlink.iota.spiritual.PotionSpiritIota
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.potion.PotionUtil
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import net.minecraft.util.registry.Registry

object HexlinkItems {

    var items=HashMap<String,Item>()

    fun make(id: String, item: Item): Item{
        items.put(id, item)
        return item
    }

    fun registerAll(){
        for(entry in items )Registry.register(Registry.ITEM, Identifier(HexlinkMod.MODID, entry.key), entry.value)
    }

    val PotionFocus=make("potion_focus", GreatFocusItem(epicProps(), ::greatFocusPotion))
    val BlockFocus=make("block_focus", GreatFocusItem(epicProps(), ::greatFocusBlock))
    val EntityFocus=make("entity_focus", GreatFocusItem(epicProps(), ::greatFocusEntity))
    val ItemFocus=make("item_focus", GreatFocusItem(epicProps(), ::greatFocusItem))
    val UltimateFocus=make("ultimate_focus", GreatFocusItem(epicProps(), ::greatFocusUltimate))

    var UpgradedBook=make("upgraded_book", UpgradedBookItem(epicProps()))
    var FocusCollar=make("focus_collar", ItemFocus(simpleProps()))
    var MixedPigment=make("mixed_pigment", MixedPigmentItem(stackableProps()))

    fun epicProps(): Item.Settings{
        return Item.Settings()
                .group(IXplatAbstractions.INSTANCE.tab)
                .maxCount(1)
                .rarity(Rarity.EPIC)
    }

    fun simpleProps(): Item.Settings{
        return Item.Settings()
                .group(IXplatAbstractions.INSTANCE.tab)
                .maxCount(1)
                .rarity(Rarity.COMMON)
    }

    fun stackableProps(): Item.Settings{
        return Item.Settings()
                .group(IXplatAbstractions.INSTANCE.tab)
                .maxCount(64)
                .rarity(Rarity.COMMON)
    }

    fun greatFocusPotion(target: Entity): PotionSpiritIota?{
        if(target is ItemEntity && target.stack.item==Items.POTION){
            val effects=PotionUtil.getPotionEffects(target.stack)
            if(!effects.isEmpty())return PotionSpiritIota(effects.get(0).effectType)
        }
        return null
    }

    fun greatFocusItem(target: Entity): ItemSpiritIota?{
        if(target is ItemEntity){
            return ItemSpiritIota(target.stack.item)
        }
        return null
    }

    fun greatFocusBlock(target: Entity): BlockSpiritIota?{
        if(target is ItemEntity && target.stack.item is BlockItem){
            return BlockSpiritIota((target.stack.item as BlockItem).block)
        }
        return null
    }

    fun greatFocusEntity(target: Entity): EntitySpiritIota?{
        if(target.type.isSummonable){
            return EntitySpiritIota(target.type)
        }
        return null
    }

    fun greatFocusUltimate(target: Entity): Iota? {
        val potion= greatFocusPotion(target)
        if(potion!=null) return potion

        val block= greatFocusBlock(target)
        if(block!=null) return block

        val item= greatFocusItem(target)
        if(item!=null) return item

        val entity= greatFocusEntity(target)
        if(entity!=null) return entity

        return null
    }
}