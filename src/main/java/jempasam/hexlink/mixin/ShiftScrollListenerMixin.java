package jempasam.hexlink.mixin;

import at.petrak.hexcasting.client.ShiftScrollListener;
import at.petrak.hexcasting.common.lib.HexItems;
import jempasam.hexlink.item.UpgradedBook;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShiftScrollListener.class)
public class ShiftScrollListenerMixin {
    @Inject(at = @At("RETURN"), method = "IsScrollableItem", cancellable = true)
    private static void IsScrollableItem(Item item, CallbackInfoReturnable<Boolean> cir) {
        if(item instanceof UpgradedBook)cir.setReturnValue(true);
    }
}
