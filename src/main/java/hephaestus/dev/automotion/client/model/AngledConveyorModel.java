package hephaestus.dev.automotion.client.model;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public class AngledConveyorModel extends AutomotionModel {
	private static final SpriteIdentifier[] SPRITE_IDENTIFIERS = new SpriteIdentifier[] {
			new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("minecraft:block/iron_block")),
			new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("automotion:block/conveyor_belt_reel")),
			new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("minecraft:block/black_wool")),
	};

	private final ConveyorDirection direction;

	public AngledConveyorModel(ConveyorDirection direction) {
		this.direction = direction;
		SPRITES = new Sprite[3];
	}

	@Override
	public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
		return Arrays.asList(SPRITE_IDENTIFIERS);
	}

	@Override
	public @Nullable BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
		for (int i = 0; i < SPRITES.length; ++i) {
			SPRITES[i] = textureGetter.apply(SPRITE_IDENTIFIERS[i]);
		}

		Renderer renderer = RendererAccess.INSTANCE.getRenderer();
		MeshBuilder builder = renderer.meshBuilder();
		QuadEmitter emitter = builder.getEmitter();

		emitter.pos(0, 0, 2.99F/16F, 0).spriteColor(0, 0, -1).sprite(0, 0, 0, this.direction.isReversed() ? 0 : 16);
		emitter.pos(1, 0, 18.99F/16F, 1).spriteColor(1, 0, -1).sprite(1, 0, 0, this.direction.isReversed() ? 16 : 0);
		emitter.pos(2, 1, 18.99F/16F, 1).spriteColor(2, 0, -1).sprite(2, 0, 16, this.direction.isReversed() ? 16 : 0);
		emitter.pos(3, 1, 2.99F/16F, 0).spriteColor(3, 0, -1).sprite(3, 0, 16, this.direction.isReversed() ? 0 : 16);

		emitter.spriteBake(0, SPRITES[this.direction.sprite()], 0);
		emitter.emit();

		emitter.pos(0, 0, 0, 0.001F/16F).spriteColor(0, 0, -1).sprite(0, 0, 0, this.direction.isReversed() ? 0 : 3);
		emitter.pos(1, 0, 2.99F/16F, 0.001F/16F).spriteColor(1, 0, -1).sprite(1, 0, 0, this.direction.isReversed() ? 3 : 0);
		emitter.pos(2, 1, 2.99F/16F, 0.001F/16F).spriteColor(2, 0, -1).sprite(2, 0, 16, this.direction.isReversed() ? 3 : 0);
		emitter.pos(3, 1, 0, 0.001F/16F).spriteColor(3, 0, -1).sprite(3, 0, 16, this.direction.isReversed() ? 0 : 3);

		emitter.spriteBake(0, SPRITES[this.direction.sprite()], 0);
		emitter.emit();

		emitter.pos(0, 0, 18.99F/16F, 15.99F/16F).spriteColor(0, 0, -1).sprite(0, 0, 0, this.direction.isReversed() ? 0 : 3);
		emitter.pos(1, 0, 1F, 15.99F/16F).spriteColor(1, 0, -1).sprite(1, 0, 0, this.direction.isReversed() ? 3 : 0);
		emitter.pos(2, 1, 1F, 15.99F/16F).spriteColor(2, 0, -1).sprite(2, 0, 16, this.direction.isReversed() ? 3 : 0);
		emitter.pos(3, 1, 18.99F/16F, 15.99F/16F).spriteColor(3, 0, -1).sprite(3, 0, 16, this.direction.isReversed() ? 0 : 3);

		emitter.spriteBake(0, SPRITES[this.direction.sprite()], 0);
		emitter.emit();

		emitter.pos(0, 0F, 0F, 0F).spriteColor(0, 0, -1).sprite(0, 0, 0, 0);
		emitter.pos(1, 1F, 0F, 0F).spriteColor(1, 0, -1).sprite(1, 0, 16, 0);
		emitter.pos(2, 1F, 1F, 1F).spriteColor(2, 0, -1).sprite(2, 0, 16, 16);
		emitter.pos(3, 0F, 1F, 1F).spriteColor(3, 0, -1).sprite(3, 0, 0, 16);

		emitter.spriteBake(0, SPRITES[0], 0);
		emitter.emit();

		mesh = builder.build();

		return this;
	}

	public enum ConveyorDirection {
		FORWARD,
		REVERSED,
		DISABLED;

		private static int i = 0;

		public int sprite() {
			return this == DISABLED ? 2 : 1;
		}

		public boolean isReversed() {
			return this == REVERSED;
		}
	}
}
