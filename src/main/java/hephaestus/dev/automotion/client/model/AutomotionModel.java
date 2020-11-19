package hephaestus.dev.automotion.client.model;

import hephaestus.dev.automotion.common.block.conveyors.ConveyorBelt;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public abstract class AutomotionModel implements UnbakedModel, BakedModel, FabricBakedModel {
	protected Sprite[] SPRITES;
	protected Mesh mesh;

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitItemQuads(ItemStack itemStack, Supplier<Random> supplier, RenderContext renderContext) {

	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
//		List<BakedQuad> quads = new ArrayList<>();
//
//		this.mesh.forEach(consumer -> {
//			quads.add(consumer.toBakedQuad(0, SPRITES[0], false));
//		});
//
//		return quads;
		return Collections.emptyList();
	}

	@Override
	public void emitBlockQuads(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos, Supplier<Random> supplier, RenderContext renderContext) {
		if (blockState.getProperties().contains(Properties.HOPPER_FACING)) {
			Quaternion rotate = Vector3f.POSITIVE_Y.getDegreesQuaternion(angle(blockState));
			RenderContext.QuadTransform transform = mv -> {
				Vector3f tmp = new Vector3f();

				for (int i = 0; i < 4; i++) {
					// Transform the position (center of rotation is 0.5, 0.5, 0.5)
					mv.copyPos(i, tmp);
					tmp.add(-0.5f, -0.5f, -0.5f);
					tmp.rotate(rotate);
					tmp.add(0.5f, 0.5f, 0.5f);
					mv.pos(i, tmp);

					// Transform the normal
					if (mv.hasNormal(i)) {
						mv.copyNormal(i, tmp);
						tmp.rotate(rotate);
						mv.normal(i, tmp);
					}
				}

				mv.nominalFace(blockState.get(Properties.HOPPER_FACING));
				return true;
			};

			renderContext.pushTransform(transform);
			renderContext.meshConsumer().accept(mesh);
			renderContext.popTransform();
		} else {
			renderContext.meshConsumer().accept(mesh);
		}
	}

	@Override
	public boolean useAmbientOcclusion() {
		return true;
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
		return SPRITES[0];
	}

	@Override
	public ModelTransformation getTransformation() {
		return null;
	}

	@Override
	public ModelOverrideList getOverrides() {
		return null;
	}

	@Override
	public Collection<Identifier> getModelDependencies() {
		return Collections.emptyList();
	}

	protected static float angle(BlockState state) {
		float r = state.get(ConveyorBelt.ANGLE) == ConveyorBelt.Angle.UP ? 180 : 0;

		switch (state.get(Properties.HOPPER_FACING)) {
			case NORTH: return r;
			case EAST: return r + 270;
			case SOUTH: return r + 180;
			case WEST: return r + 90;
			default: throw new RuntimeException();
		}
	}
}
