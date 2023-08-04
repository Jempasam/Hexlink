package jempasam.hexlink.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import at.petrak.hexcasting.common.network.MsgShiftScrollSyn;
import net.minecraft.util.Hand;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import jempasam.hexlink.item.UpgradedBookItem;

@Mixin(MsgShiftScrollSyn.class)
class MsgShiftScrollSynMixin{

    @Inject(at = @At("TAIL"), method = "handleForHand")
    private void handleForHand(ServerPlayerEntity sender, Hand hand, double delta, CallbackInfo info) {
        if (delta != 0) {
            var stack = sender.getStackInHand(hand);
            if (stack.getItem() instanceof UpgradedBookItem) {
                spellbook(sender, hand, stack, delta);
            }
        }
    }

    @Shadow
    private void spellbook(ServerPlayerEntity sender, Hand hand, ItemStack stack, double delta){}
}