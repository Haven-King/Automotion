package dev.hephaestus.automotion.common.block.entity;

import dev.hephaestus.automotion.client.sound.PositionedSound;
import dev.hephaestus.automotion.common.AutomotionBlocks;
import dev.hephaestus.automotion.common.AutomotionSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ConveyorBeltBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
    private double baseSpeed;

    @Environment(EnvType.CLIENT)
    private PositionedSound sound;

    public ConveyorBeltBlockEntity(BlockPos pos, BlockState state) {
        super(AutomotionBlocks.CONVEYOR_BELT_BLOCK_ENTITY, pos, state);
    }

    public double getSpeed() {
        return 0.03;
    }

    public BlockEntity withBaseSpeed(double speed) {
        this.baseSpeed = speed;

        return this;
    }

    private void playSound() {
        if (this.world != null && this.world.isClient && this.sound == null) {
            this.sound = new PositionedSound(
                    AutomotionSounds.CONVEYOR_BELT.getId(),
                    SoundCategory.BLOCKS,
                    0.05F,
                    1F,
                    true,
                    0,
                    SoundInstance.AttenuationType.LINEAR,
                    this.getPos(),
                    true,
                    20
            );

            MinecraftClient.getInstance().getSoundManager().play(this.sound);
        }
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        this.playSound();
    }

    @Override
    public void markRemoved() {
        super.markRemoved();

        if (this.world != null && this.world.isClient && this.sound != null) {
            this.sound.setDone();
        }
    }

    @Override
    public void cancelRemoval() {
        super.cancelRemoval();
        this.playSound();
    }

    @Override
    public void readNbt(CompoundTag tag) {
        this.baseSpeed = tag.getDouble("BaseSpeed");
    }

    @Override
    public CompoundTag writeNbt(CompoundTag tag) {
        tag.putDouble("BaseSpeed", this.baseSpeed);

        return super.writeNbt(tag);
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.baseSpeed = tag.getDouble("BaseSpeed");
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        tag.putDouble("BaseSpeed", this.baseSpeed);

        return tag;
    }
}
