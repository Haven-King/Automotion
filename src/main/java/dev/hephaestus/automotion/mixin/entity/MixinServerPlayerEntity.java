package dev.hephaestus.automotion.mixin.entity;

import dev.hephaestus.automotion.common.util.PlayerExtensions;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity implements PlayerExtensions {
    @Unique private boolean alternatePlacement = false;

    @Override
    public boolean doAltPlacement() {
        return this.alternatePlacement;
    }

    @Override
    public void setAltPlacement(boolean bl) {
        this.alternatePlacement = bl;
    }
}
