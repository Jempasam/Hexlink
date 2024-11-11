package jempasam.hexlink.mixin;

import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import jempasam.hexlink.HexlinkMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CastingHarness.class)
public class CanTakeMediaFromInventory {

    @Redirect(method = "withdrawMedia", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isIn(Lnet/minecraft/tag/TagKey;)Z"))
    public final boolean withdrawMedia(ItemStack instance, TagKey<Item> tag){
        return instance.isIn(tag) || instance.isIn(CASTER);
    }

    @Unique
    private static TagKey<Item> CASTER = TagKey.of(Registry.ITEM_KEY, new Identifier(HexlinkMod.MODID,"casters"));
}
