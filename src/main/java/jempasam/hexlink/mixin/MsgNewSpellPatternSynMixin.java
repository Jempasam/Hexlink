package jempasam.hexlink.mixin;

import at.petrak.hexcasting.api.spell.casting.ControllerInfo;
import at.petrak.hexcasting.api.spell.casting.ResolvedPattern;
import at.petrak.hexcasting.common.network.MsgNewSpellPatternAck;
import at.petrak.hexcasting.common.network.MsgNewSpellPatternSyn;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import jempasam.hexlink.item.functionnality.SpellCasterItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MsgNewSpellPatternSyn.class)
public abstract class MsgNewSpellPatternSynMixin {
    @Shadow @Final private Hand handUsed;

    @Shadow @Final private List<ResolvedPattern> resolvedPatterns;

    @Inject(at = @At("HEAD"), method = "handle", cancellable = true)
    public void handle(MinecraftServer server, ServerPlayerEntity sender, CallbackInfo info){
        var stack=sender.getStackInHand(this.handUsed);
        var item=stack.getItem();
        if(item instanceof SpellCasterItem spellCaster){
            server.execute(()->{
                var clientInfo=spellCaster.onCast(stack,this.handUsed, sender, resolvedPatterns.get(resolvedPatterns.size()-1).getPattern());
                IXplatAbstractions.INSTANCE.sendPacketToPlayer(sender,
                        new MsgNewSpellPatternAck(clientInfo, resolvedPatterns.size() - 1));
            });
            info.cancel();
        }
    }
}
