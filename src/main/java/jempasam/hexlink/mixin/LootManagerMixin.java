package jempasam.hexlink.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import jempasam.hexlink.data.HexlinkDataLoaders;
import jempasam.hexlink.item.functionnality.ItemScrollable;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.mixin.resource.loader.KeyedResourceReloadListenerMixin;
import net.minecraft.loot.LootManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(LootManager.class)
public abstract class LootManagerMixin implements IdentifiableResourceReloadListener {
    @ModifyReturnValue(at = @At("RETURN"), method = "getFabricDependencies", remap = false)
    Collection<Identifier> getFabricDependencies(Collection<Identifier> info) {
        var ret = new ArrayList<>(info);
        ret.add(HexlinkDataLoaders.getEXTRACTORS());
        ret.add(HexlinkDataLoaders.getSPIRITS());
        return ret;
    }
}
