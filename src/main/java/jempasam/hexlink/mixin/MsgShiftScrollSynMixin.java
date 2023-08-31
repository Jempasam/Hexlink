package jempasam.hexlink.mixin;

import jempasam.hexlink.item.functionnality.ItemScrollable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import at.petrak.hexcasting.common.network.MsgShiftScrollSyn;
import net.minecraft.util.Hand;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(MsgShiftScrollSyn.class)
class MsgShiftScrollSynMixin{

    @Inject(at = @At("TAIL"), method = "handleForHand")
    private void handleForHand(ServerPlayerEntity sender, Hand hand, double delta, CallbackInfo info) {
        if (delta != 0) {
            var stack = sender.getStackInHand(hand);
            var item = stack.getItem();
            if (item instanceof ItemScrollable rollable) {
                rollable.roll(stack,sender,hand,delta);
            }
        }
    }

    @Shadow
    private void spellbook(ServerPlayerEntity sender, Hand hand, ItemStack stack, double delta){}
}