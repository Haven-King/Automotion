package dev.hephaestus.automotion.client.model;

import com.mojang.datafixers.util.Pair;
import dev.hephaestus.automotion.Automotion;
import dev.monarkhes.myron.api.Myron;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConveyorBeltModel implements FabricBakedModel, UnbakedModel, BakedModel {
    private static final Direction[] HORIZONTAL = new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    private static final Map<Direction, Integer> ROTATIONS = new HashMap<>();

    private static final Collection<SpriteIdentifier> TEXTURE_DEPENDENCIES = new HashSet<>();

    private static final ModelPart BELT_MOVING_FRONT = new ModelPart(Automotion.id("block/conveyor/belt_moving/belt_front"));
    private static final ModelPart BELT_MOVING_FRONT_LEFT = new ModelPart(Automotion.id("block/conveyor/belt_moving/belt_front_left"));
    private static final ModelPart BELT_MOVING_FRONT_RIGHT = new ModelPart(Automotion.id("block/conveyor/belt_moving/belt_front_right"));
    private static final ModelPart BELT_MOVING_BACK = new ModelPart(Automotion.id("block/conveyor/belt_moving/belt_back"));
    private static final ModelPart BELT_MOVING_BACK_LEFT = new ModelPart(Automotion.id("block/conveyor/belt_moving/belt_back_left"));
    private static final ModelPart BELT_MOVING_BACK_RIGHT = new ModelPart(Automotion.id("block/conveyor/belt_moving/belt_back_right"));
    private static final ModelPart BELT_MOVING_MIDDLE_FRONT = new ModelPart(Automotion.id("block/conveyor/belt_moving/belt_middle_front"));
    private static final ModelPart BELT_MOVING_MIDDLE_FRONT_LEFT = new ModelPart(Automotion.id("block/conveyor/belt_moving/belt_middle_front_left"));
    private static final ModelPart BELT_MOVING_MIDDLE_FRONT_RIGHT = new ModelPart(Automotion.id("block/conveyor/belt_moving/belt_middle_front_right"));
    private static final ModelPart BELT_MOVING_MIDDLE_BACK = new ModelPart(Automotion.id("block/conveyor/belt_moving/belt_middle_back"));
    private static final ModelPart BELT_MOVING_MIDDLE_BACK_LEFT = new ModelPart(Automotion.id("block/conveyor/belt_moving/belt_middle_back_left"));
    private static final ModelPart BELT_MOVING_MIDDLE_BACK_RIGHT = new ModelPart(Automotion.id("block/conveyor/belt_moving/belt_middle_back_right"));

    private static final ModelPart BELT_STILL_FRONT = new ModelPart(Automotion.id("block/conveyor/belt_still/belt_front"));
    private static final ModelPart BELT_STILL_FRONT_LEFT = new ModelPart(Automotion.id("block/conveyor/belt_still/belt_front_left"));
    private static final ModelPart BELT_STILL_FRONT_RIGHT = new ModelPart(Automotion.id("block/conveyor/belt_still/belt_front_right"));
    private static final ModelPart BELT_STILL_BACK = new ModelPart(Automotion.id("block/conveyor/belt_still/belt_back"));
    private static final ModelPart BELT_STILL_BACK_LEFT = new ModelPart(Automotion.id("block/conveyor/belt_still/belt_back_left"));
    private static final ModelPart BELT_STILL_BACK_RIGHT = new ModelPart(Automotion.id("block/conveyor/belt_still/belt_back_right"));
    private static final ModelPart BELT_STILL_MIDDLE_FRONT = new ModelPart(Automotion.id("block/conveyor/belt_still/belt_middle_front"));
    private static final ModelPart BELT_STILL_MIDDLE_FRONT_LEFT = new ModelPart(Automotion.id("block/conveyor/belt_still/belt_middle_front_left"));
    private static final ModelPart BELT_STILL_MIDDLE_FRONT_RIGHT = new ModelPart(Automotion.id("block/conveyor/belt_still/belt_middle_front_right"));
    private static final ModelPart BELT_STILL_MIDDLE_BACK = new ModelPart(Automotion.id("block/conveyor/belt_still/belt_middle_back"));
    private static final ModelPart BELT_STILL_MIDDLE_BACK_LEFT = new ModelPart(Automotion.id("block/conveyor/belt_still/belt_middle_back_left"));
    private static final ModelPart BELT_STILL_MIDDLE_BACK_RIGHT = new ModelPart(Automotion.id("block/conveyor/belt_still/belt_middle_back_right"));


    private static final ModelPart LEG_FRONT_LEFT = new ModelPart(Automotion.id("block/conveyor/leg_left"));
    private static final ModelPart LEG_FRONT_RIGHT = new ModelPart(Automotion.id("block/conveyor/leg_right"));
    private static final ModelPart LEG_BACK_LEFT = new ModelPart(Automotion.id("block/conveyor/leg_right"), 180);
    private static final ModelPart LEG_BACK_RIGHT = new ModelPart(Automotion.id("block/conveyor/leg_left"), 180);
    private static final ModelPart LEG_MIDDLE_LEFT = new ModelPart(Automotion.id("block/conveyor/leg_middle_left"));
    private static final ModelPart LEG_MIDDLE_RIGHT = new ModelPart(Automotion.id("block/conveyor/leg_middle_right"));
    private static final ModelPart ROLLER_FRONT = new ModelPart(Automotion.id("block/conveyor/roller"));
    private static final ModelPart ROLLER_BACK = new ModelPart(Automotion.id("block/conveyor/roller"), 180);
    private static final ModelPart ROLLER_MIDDLE = new ModelPart(Automotion.id("block/conveyor/middle_roller"));
    private static final ModelPart SUPPORT_END_FRONT = new ModelPart(Automotion.id("block/conveyor/support_end"));
    private static final ModelPart SUPPORT_END_BACK = new ModelPart(Automotion.id("block/conveyor/support_end"), 180);
    private static final ModelPart SUPPORT_MIDDLE_FRONT = new ModelPart(Automotion.id("block/conveyor/support_middle"));
    private static final ModelPart SUPPORT_MIDDLE_BACK = new ModelPart(Automotion.id("block/conveyor/support_middle"), 180);

    static {
        ROTATIONS.put(Direction.NORTH, 0);
        ROTATIONS.put(Direction.SOUTH, 180);
        ROTATIONS.put(Direction.EAST, 90);
        ROTATIONS.put(Direction.WEST, 270);

        TEXTURE_DEPENDENCIES.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Automotion.id("block/conveyor_belt")));
        TEXTURE_DEPENDENCIES.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Automotion.id("block/steel_block")));
        TEXTURE_DEPENDENCIES.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("block/iron_block")));
    }

    private final Model model = new Model(
            BELT_MOVING_FRONT,
            BELT_MOVING_BACK,
            BELT_MOVING_BACK_LEFT,
            BELT_MOVING_BACK_RIGHT,
            BELT_MOVING_MIDDLE_FRONT,
            BELT_MOVING_MIDDLE_BACK,
            BELT_MOVING_FRONT_LEFT,
            BELT_MOVING_FRONT_RIGHT,
            BELT_MOVING_BACK_LEFT,
            BELT_MOVING_BACK_RIGHT,
            BELT_MOVING_MIDDLE_FRONT_LEFT,
            BELT_MOVING_MIDDLE_FRONT_RIGHT,
            BELT_MOVING_MIDDLE_BACK_LEFT,
            BELT_MOVING_MIDDLE_BACK_RIGHT,

            BELT_STILL_FRONT,
            BELT_STILL_BACK,
            BELT_STILL_BACK_LEFT,
            BELT_STILL_BACK_RIGHT,
            BELT_STILL_MIDDLE_FRONT,
            BELT_STILL_MIDDLE_BACK,
            BELT_STILL_FRONT_LEFT,
            BELT_STILL_FRONT_RIGHT,
            BELT_STILL_BACK_LEFT,
            BELT_STILL_BACK_RIGHT,
            BELT_STILL_MIDDLE_FRONT_LEFT,
            BELT_STILL_MIDDLE_FRONT_RIGHT,
            BELT_STILL_MIDDLE_BACK_LEFT,
            BELT_STILL_MIDDLE_BACK_RIGHT,


            LEG_FRONT_LEFT,
            LEG_FRONT_RIGHT,
            LEG_BACK_LEFT,
            LEG_BACK_RIGHT,
            LEG_MIDDLE_LEFT,
            LEG_MIDDLE_RIGHT,
            ROLLER_FRONT,
            ROLLER_BACK,
            ROLLER_MIDDLE,
            SUPPORT_END_FRONT,
            SUPPORT_END_BACK,
            SUPPORT_MIDDLE_FRONT,
            SUPPORT_MIDDLE_BACK
    );

    private final Map<Key, Collection<Mesh>> meshes = new HashMap<>();

    private Sprite sprite;

    @Nullable
    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        this.model.bake(textureGetter);
        this.sprite = textureGetter.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Automotion.id("block/steel_block")));

        return this;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        Consumer<Mesh> consumer = context.meshConsumer();

        Direction facing = state.get(Properties.HORIZONTAL_FACING);
        Block block = state.getBlock();

        Direction leftDir = facing.rotateYCounterclockwise();
        Direction rightDir = facing.rotateYClockwise();

        BlockPos.Mutable mut = new BlockPos.Mutable();

        BlockState leftNeighbor = blockView.getBlockState(mut.set(pos).move(leftDir));
        BlockState rightNeighbor = blockView.getBlockState(mut.set(pos).offset(rightDir));
        BlockState forwardNeighbor = blockView.getBlockState(mut.set(pos).offset(facing));
        BlockState backNeighbor = blockView.getBlockState(mut.set(pos).offset(facing.getOpposite()));
        BlockState downNeighbor = blockView.getBlockState(mut.set(pos).down());
        BlockState leftForwardNeighbor = blockView.getBlockState(mut.set(pos).offset(facing).offset(leftDir));
        BlockState rightForwardNeighbor = blockView.getBlockState(mut.set(pos).offset(facing).offset(rightDir));
        BlockState leftBackNeighbor = blockView.getBlockState(mut.set(pos).offset(facing.getOpposite()).offset(leftDir));
        BlockState rightBackNeighbor = blockView.getBlockState(mut.set(pos).offset(facing.getOpposite()).offset(rightDir));

        boolean left = leftNeighbor.getBlock() == block && leftNeighbor.get(Properties.HORIZONTAL_FACING) == facing;
        boolean right = rightNeighbor.getBlock() == block && rightNeighbor.get(Properties.HORIZONTAL_FACING) == facing;
        boolean forward = forwardNeighbor.getBlock() == block && forwardNeighbor.get(Properties.HORIZONTAL_FACING) == facing;
        boolean back = backNeighbor.getBlock() == block && backNeighbor.get(Properties.HORIZONTAL_FACING) == facing;
        boolean down = Block.isFaceFullSquare(downNeighbor.getCollisionShape(blockView, mut.set(pos).down(), ShapeContext.absent()), Direction.UP);
        boolean leftForward = leftForwardNeighbor.getBlock() == block && leftForwardNeighbor.get(Properties.HORIZONTAL_FACING) == facing;
        boolean rightForward = rightForwardNeighbor.getBlock() == block && rightForwardNeighbor.get(Properties.HORIZONTAL_FACING) == facing;
        boolean leftBack = leftBackNeighbor.getBlock() == block && leftBackNeighbor.get(Properties.HORIZONTAL_FACING) == facing;
        boolean rightBack = rightBackNeighbor.getBlock() == block && rightBackNeighbor.get(Properties.HORIZONTAL_FACING) == facing;

        boolean enabled = state.get(Properties.ENABLED);

        Collection<Mesh> meshes = this.meshes.computeIfAbsent(Key.key(facing, left, right, forward, back, down, leftForward, rightForward, leftBack, rightBack, enabled), key -> {
            Collection<Mesh> parts = new HashSet<>();

            if (forward) {
                parts.add(this.model.get(enabled ? BELT_MOVING_MIDDLE_FRONT : BELT_STILL_MIDDLE_FRONT, facing));
                parts.add(this.model.get(SUPPORT_MIDDLE_FRONT, facing));
            } else {
                parts.add(this.model.get(enabled ? BELT_MOVING_FRONT : BELT_STILL_FRONT, facing));
                parts.add(this.model.get(ROLLER_FRONT, facing));
                parts.add(this.model.get(SUPPORT_END_FRONT, facing));
            }

            if (back) {
                parts.add(this.model.get(enabled ? BELT_MOVING_MIDDLE_BACK : BELT_STILL_MIDDLE_BACK, facing));
                parts.add(this.model.get(SUPPORT_MIDDLE_BACK, facing));
            } else {
                parts.add(this.model.get(enabled ? BELT_MOVING_BACK : BELT_STILL_BACK, facing));
                parts.add(this.model.get(ROLLER_BACK, facing));
                parts.add(this.model.get(SUPPORT_END_BACK, facing));
            }

            if (left) {
                parts.add(this.model.get(forward && leftForward
                        ? enabled ? BELT_MOVING_MIDDLE_FRONT_LEFT : BELT_STILL_MIDDLE_FRONT_LEFT
                        : enabled ? BELT_MOVING_FRONT_LEFT : BELT_STILL_FRONT_LEFT, facing));

                parts.add(this.model.get(back && leftBack
                        ? enabled ? BELT_MOVING_MIDDLE_BACK_LEFT : BELT_STILL_MIDDLE_BACK_LEFT
                        : enabled ? BELT_MOVING_BACK_LEFT : BELT_STILL_BACK_LEFT, facing));
            }

            if (right) {
                parts.add(this.model.get(forward && rightForward
                        ? enabled ? BELT_MOVING_MIDDLE_FRONT_RIGHT : BELT_STILL_MIDDLE_FRONT_RIGHT
                        : enabled ? BELT_MOVING_FRONT_RIGHT : BELT_STILL_FRONT_RIGHT, facing));

                parts.add(this.model.get(back && rightBack
                        ? enabled ? BELT_MOVING_MIDDLE_BACK_RIGHT : BELT_STILL_MIDDLE_BACK_RIGHT
                        : enabled ? BELT_MOVING_BACK_RIGHT : BELT_STILL_BACK_RIGHT, facing));
            }

            if (down && forward && back) {
                parts.add(this.model.get(ROLLER_MIDDLE, facing));
            }

            if (down) {
                if (!left) {
                    if (forward && back) {
                        parts.add(this.model.get(LEG_MIDDLE_LEFT, facing));
                    } else {
                        if (!forward) {
                            parts.add(this.model.get(LEG_FRONT_LEFT, facing));
                        }

                        if (!back) {
                            parts.add(this.model.get(LEG_BACK_LEFT, facing));
                        }
                    }
                }

                if (!right) {
                    if (forward && back) {
                        parts.add(this.model.get(LEG_MIDDLE_RIGHT, facing));
                    } else {
                        if (!forward) {
                            parts.add(this.model.get(LEG_FRONT_RIGHT, facing));
                        }

                        if (!back) {
                            parts.add(this.model.get(LEG_BACK_RIGHT, facing));
                        }
                    }
                }
            }

            return parts;
        });

        for (Mesh mesh : meshes) {
            consumer.accept(mesh);
        }
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        Consumer<Mesh> consumer = context.meshConsumer();

        consumer.accept(this.model.get(BELT_STILL_FRONT, Direction.NORTH));
        consumer.accept(this.model.get(BELT_STILL_BACK, Direction.NORTH));
        consumer.accept(this.model.get(LEG_FRONT_LEFT, Direction.NORTH));
        consumer.accept(this.model.get(LEG_FRONT_RIGHT, Direction.NORTH));
        consumer.accept(this.model.get(LEG_BACK_LEFT, Direction.NORTH));
        consumer.accept(this.model.get(LEG_BACK_RIGHT, Direction.NORTH));
        consumer.accept(this.model.get(ROLLER_BACK, Direction.NORTH));
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return Collections.emptyList();
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
        return TEXTURE_DEPENDENCIES;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean hasDepth() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getSprite() {
        return this.sprite;
    }

    @Override
    public ModelTransformation getTransformation() {
        return ModelTransformation.NONE;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }

    private static class Model {
        private final Map<ModelPart, Map<Direction, Mesh>> meshes = new HashMap<>();

        private Model(ModelPart... parts) {
            for (ModelPart part : parts) {
                this.meshes.put(part, new HashMap<>());
            }
        }

        private void bake(Function<SpriteIdentifier, Sprite> textureGetter) {
            for (ModelPart part : this.meshes.keySet()) {
                for (Direction direction : HORIZONTAL) {
                    ModelRotation rotation = ModelRotation.get(0, ROTATIONS.get(direction) + part.rotation);
                    meshes.get(part).put(direction, Myron.load(part.identifier, textureGetter, rotation, true));
                }
            }
        }

        public Mesh get(ModelPart part, Direction direction) {
            Map<Direction, Mesh> meshes = this.meshes.get(part);
            return meshes.get(direction);
        }
    }

    private static class ModelPart {
        private final Identifier identifier;
        private final int rotation;

        private ModelPart(Identifier identifier, int rotation) {
            this.identifier = identifier;
            this.rotation = rotation;
        }

        private ModelPart(Identifier identifier) {
            this(identifier, 0);
        }
    }

    private static final class Key {
        private final int code;

        private Key(Direction direction, boolean... bits) {
            int key = ((direction.getId() << bits.length));

            for (int i = 0; i < bits.length; ++i) {
                if (bits[i]) key |= 0b1 << i;
            }

            this.code = key;
        }

        @Override
        public int hashCode() {
            return code;
        }

        private static Key key(Direction direction, boolean... bits) {
            return new Key(direction, bits);
        }
    }
}
