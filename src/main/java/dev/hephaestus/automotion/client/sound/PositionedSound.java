package dev.hephaestus.automotion.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PositionedSound extends PositionedSoundInstance implements TickableSoundInstance {
    private final float maxVolume;
    private final float maxDistance;

    private boolean done;

    public PositionedSound(Identifier id, SoundCategory category, float maxVolume, float pitch, boolean repeat, int repeatDelay, AttenuationType attenuationType, double x, double y, double z, boolean looping, float maxDistance) {
        super(id, category, maxVolume, pitch, repeat, repeatDelay, attenuationType, x, y, z, looping);
        this.maxVolume = maxVolume;
        this.maxDistance = maxDistance;
        double d = Math.sqrt(MinecraftClient.getInstance().gameRenderer.getCamera().getPos().squaredDistanceTo(this.x, this.y, this.z));
        this.volume = (float) ((this.maxDistance - MathHelper.clamp(d, 0, this.maxDistance)) / this.maxDistance) * maxVolume;
    }

    public PositionedSound(Identifier id, SoundCategory category, float maxVolume, float pitch, boolean repeat, int repeatDelay, AttenuationType attenuationType, BlockPos pos, boolean looping, float maxDistance) {
        this(id, category, maxVolume, pitch, repeat, repeatDelay, attenuationType, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, looping, maxDistance);
    }

    @Override
    public boolean isDone() {
        return this.done;
    }

    @Override
    public void tick() {
        double d = Math.sqrt(MinecraftClient.getInstance().gameRenderer.getCamera().getPos().squaredDistanceTo(this.x, this.y, this.z));
        this.volume = (float) ((this.maxDistance - MathHelper.clamp(d, 0, this.maxDistance)) / this.maxDistance) * this.maxVolume;
    }

    public final void setDone() {
        this.done = true;
        this.repeat = false;
    }
}
