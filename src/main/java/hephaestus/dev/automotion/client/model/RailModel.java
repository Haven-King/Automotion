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

public class RailModel extends AutomotionModel {
	private static final SpriteIdentifier[] SPRITE_IDENTIFIERS = new SpriteIdentifier[] {
			new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("minecraft:block/iron_block"))
	};

	private final Side side;

	public RailModel(Side side) {
		this.side = side;
		SPRITES = new Sprite[1];
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

		switch (this.side) {
			case LEFT:
				emitter.pos(0, 1, 3/16F, 0).spriteColor(0, 0, -1).sprite(0, 0, 0, 13);
				emitter.pos(1, 1, 0, 0).spriteColor(1, 0, -1).sprite(1, 0, 0, 16);
				emitter.pos(2, 15/16F, 0, 0).spriteColor(2, 0, -1).sprite(2, 0, 1, 13);
				emitter.pos(3, 15/16F, 3/16F, 0).spriteColor(3, 0, -1).sprite(3, 0, 1, 16);

				emitter.spriteBake(0, SPRITES[0], 0);
				emitter.emit();

				emitter.pos(0, 15/16F, 19/16F, 1).spriteColor(0, 0, -1).sprite(0, 0, 15, 13);
				emitter.pos(1, 15/16F, 1, 1).spriteColor(1, 0, -1).sprite(1, 0, 15, 16);
				emitter.pos(2, 1, 1, 1).spriteColor(2, 0, -1).sprite(2, 0, 16, 16);
				emitter.pos(3, 1, 19/16F, 1).spriteColor(3, 0, -1).sprite(3, 0, 16, 13);

				emitter.spriteBake(0, SPRITES[0], 0);
				emitter.emit();

				emitter.pos(0, 1, 19/16F, 1).spriteColor(0, 0, -1).sprite(0, 0, 0, 0);
				emitter.pos(1, 1, 3/16F, 0).spriteColor(1, 0, -1).sprite(1, 0, 0, 16);
				emitter.pos(2, 15/16F, 3/16F, 0).spriteColor(2, 0, -1).sprite(2, 0, 1, 16);
				emitter.pos(3, 15/16F, 19/16F, 1).spriteColor(3, 0, -1).sprite(3, 0, 1, 0);

				emitter.spriteBake(0, SPRITES[0], 0);
				emitter.emit();

				emitter.pos(0, 1, 19/16F, 1).spriteColor(0, 0, -1).sprite(0, 0, 0, 0);
				emitter.pos(1, 1, 17/16F, 1).spriteColor(1, 0, -1).sprite(1, 0, 0, 2);
				emitter.pos(2, 1, 1/16F, 0).spriteColor(2, 0, -1).sprite(2, 0, 16, 2);
				emitter.pos(3, 1, 3/16F, 0).spriteColor(3, 0, -1).sprite(3, 0, 16, 0);

				emitter.spriteBake(0, SPRITES[0], 0);
				emitter.emit();

				emitter.pos(0, 1, 17/16F, 1).spriteColor(0, 0, -1).sprite(0, 0, 0, 16);
				emitter.pos(1, 1, 1, 1).spriteColor(1, 0, -1).sprite(1, 0, 0, 15);
				emitter.pos(2, 1, 0, 0).spriteColor(2, 0, -1).sprite(2, 0, 16, 15);
				emitter.pos(3, 1, 1/16F, 0).spriteColor(3, 0, -1).sprite(3, 0, 16, 16);

				emitter.spriteBake(0, SPRITES[0], 0);
				emitter.emit();

				break;
			case RIGHT:
				emitter.pos(0, 1/16F, 3/16F, 0).spriteColor(0, 0, -1).sprite(0, 0, 15, 13);
				emitter.pos(1, 1/16F, 0, 0).spriteColor(1, 0, -1).sprite(1, 0, 15, 16);
				emitter.pos(2, 0, 0, 0).spriteColor(2, 0, -1).sprite(2, 0, 16, 16);
				emitter.pos(3, 0, 3/16F, 0).spriteColor(3, 0, -1).sprite(3, 0, 16, 13);

				emitter.spriteBake(0, SPRITES[0], 0);
				emitter.emit();

				emitter.pos(0, 0, 19/16F, 1).spriteColor(0, 0, -1).sprite(0, 0, 0, 13);
				emitter.pos(1, 0, 1, 1).spriteColor(1, 0, -1).sprite(1, 0, 0, 16);
				emitter.pos(2, 1/16F, 1, 1).spriteColor(2, 0, -1).sprite(2, 0, 1, 13);
				emitter.pos(3, 1/16F, 19/16F, 1).spriteColor(3, 0, -1).sprite(3, 0, 1, 16);

				emitter.spriteBake(0, SPRITES[0], 0);
				emitter.emit();

				emitter.pos(0, 1/16F, 19/16F, 1).spriteColor(0, 0, -1).sprite(0, 0, 15, 0);
				emitter.pos(1, 1/16F, 3/16F, 0).spriteColor(1, 0, -1).sprite(1, 0, 15, 16);
				emitter.pos(2, 0, 3/16F, 0).spriteColor(2, 0, -1).sprite(2, 0, 16, 16);
				emitter.pos(3, 0, 19/16F, 1).spriteColor(3, 0, -1).sprite(3, 0, 16, 0);

				emitter.spriteBake(0, SPRITES[0], 0);
				emitter.emit();

				emitter.pos(0, 0, 3/16F, 0).spriteColor(0, 0, -1).sprite(0, 0, 0, 0);
				emitter.pos(1, 0, 1/16F, 0).spriteColor(1, 0, -1).sprite(1, 0, 0, 2);
				emitter.pos(2, 0, 17/16F, 1).spriteColor(2, 0, -1).sprite(2, 0, 16, 2);
				emitter.pos(3, 0, 19/16F, 1).spriteColor(3, 0, -1).sprite(3, 0, 16, 0);

				emitter.spriteBake(0, SPRITES[0], 0);
				emitter.emit();

				emitter.pos(0, 0, 1/16F, 0).spriteColor(0, 0, -1).sprite(0, 0, 0, 16);
				emitter.pos(1, 0, 0, 0).spriteColor(1, 0, -1).sprite(1, 0, 0, 15);
				emitter.pos(2, 0, 1, 1).spriteColor(2, 0, -1).sprite(2, 0, 16, 15);
				emitter.pos(3, 0, 17/16F, 1).spriteColor(3, 0, -1).sprite(3, 0, 16, 16);

				emitter.spriteBake(0, SPRITES[0], 0);
				emitter.emit();

				break;
		}

		this.mesh = builder.build();

		return this;
	}

	public enum Side {
		LEFT,
		RIGHT
	}
}
