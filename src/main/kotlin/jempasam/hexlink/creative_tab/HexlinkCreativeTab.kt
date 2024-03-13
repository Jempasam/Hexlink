package jempasam.hexlink.creative_tab

import jempasam.hexlink.HexlinkMod
import jempasam.hexlink.HexlinkRegistry
import jempasam.hexlink.item.HexlinkItems
import jempasam.hexlink.spirit.SpecialSpirit
import net.minecraft.item.ItemGroup
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object HexlinkCreativeTab {
    val MAIN_TAB: ItemGroup = ItemGroup.create(null,-1)
        .displayName(Text.translatable(Identifier(HexlinkMod.MODID,"hexlink").toTranslationKey("itemGroup")))
        .icon { HexlinkItems.UpgradedBook.defaultStack }
        .entries { context, entries ->
            HexlinkItems.run {
                // Items
                entries.add(UpgradedBook)
                entries.add(SpecialStaff)

                for(i in 0..<3){
                    entries.add(MixedPigment.defaultStack.also {
                        MixedPigment.setColor1(it, (Math.random()*0xFFFFFF).toInt())
                        MixedPigment.setColor2(it, (Math.random()*0xFFFFFF).toInt())
                    })
                }

                // Blocks
                entries.add(Vortex)
                entries.add(BigTablet)

                // Extractors
                for(extractors in HexlinkRegistry.EXTRACTOR){
                    entries.add(Crystal.defaultStack.also { Crystal.setExtractor(it,extractors) })
                }

                // Containers
                entries.add(Tablet)
                entries.add(SmallBag)
                entries.add(MediumBag)
                entries.add(BigBag)

                entries.add(HauntedCrystal)
                for(specialType in HexlinkRegistry.SPECIAL_SPIRIT){
                    entries.add(Spirit.defaultStack.also { Spirit.setSpirit(it,SpecialSpirit(specialType)) })
                    entries.add(PhilosophicalCrystal.defaultStack.also { PhilosophicalCrystal.getSpirits(it).add(SpecialSpirit(specialType)) })
                }

                entries.add(MixedPigment)
            }
        }.build()

}