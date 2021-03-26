package dev.hephaestus.automotion.mixin.entity;

import dev.hephaestus.automotion.common.block.ConveyorBeltBlock;
import dev.hephaestus.automotion.common.util.EntityExtensions;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity implements EntityExtensions {
    @Shadow public World world;
    @Shadow public abstract Box getBoundingBox();

    @Shadow public abstract Vec3d getPos();

    @Unique private boolean beingConveyed = false;

    @Inject(method = "baseTick", at = @At("TAIL"))
    private void moveFromConveyorBelts(CallbackInfo ci) {
        Box box = this.getBoundingBox();
        BlockPos blockPos = new BlockPos(box.minX + 0.001D, box.minY - 0.001D, box.minZ + 0.001D);
        BlockPos blockPos2 = new BlockPos(box.maxX - 0.001D, box.minY + 0.001D, box.maxZ - 0.001D);
        double y = this.getPos().getY();

        if (this.world.isRegionLoaded(blockPos, blockPos2) && y > blockPos.getY() + 14.75/16D && y < blockPos.getY() + 15.25/16D) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();

            for(int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
                for(int j = blockPos.getY(); j <= blockPos2.getY(); ++j) {
                    if (y > j + 14/16D && y < j + 15.25/16D) {
                        for (int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
                            mutable.set(i, j, k);
                            BlockState blockState = this.world.getBlockState(mutable);

                            if (blockState.getBlock() instanceof ConveyorBeltBlock && blockState.get(Properties.ENABLED)) {
                                try {
                                    ((ConveyorBeltBlock) blockState.getBlock()).collide(mutable, (Entity) (Object) this);
                                } catch (Throwable var12) {
                                    CrashReport crashReport = CrashReport.create(var12, "Colliding entity with block");
                                    CrashReportSection crashReportSection = crashReport.addElement("Block being collided with");
                                    CrashReportSection.addBlockInfo(crashReportSection, this.world, mutable, blockState);
                                    throw new CrashException(crashReport);
                                }
                            }
                        }
                    }
                }
            }
        }

        this.beingConveyed = ConveyorBeltBlock.move((Entity) (Object) this);
    }

    @Override
    public boolean isBeingConveyed() {
        return this.beingConveyed;
    }
}
