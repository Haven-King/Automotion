package hephaestus.dev.automotion.client;

import hephaestus.dev.automotion.client.model.AngledConveyorModel;
import hephaestus.dev.automotion.client.model.RailModel;
import hephaestus.dev.automotion.common.Automotion;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class AutomotionModelProvider implements ModelResourceProvider {
	private final HashMap<Identifier, UnbakedModel> models = new HashMap<>();

	public AutomotionModelProvider() {
		models.put(Automotion.newID("block/conveyor_belt_angled_test"), new AngledConveyorModel(AngledConveyorModel.ConveyorDirection.FORWARD));
		models.put(Automotion.newID("block/conveyor_belt_angled_test_disabled"), new AngledConveyorModel(AngledConveyorModel.ConveyorDirection.DISABLED));
		models.put(Automotion.newID("block/conveyor_belt_angled_test_reversed"), new AngledConveyorModel(AngledConveyorModel.ConveyorDirection.REVERSED));
		models.put(Automotion.newID("block/conveyor_belt_angled_left_rail"), new RailModel(RailModel.Side.LEFT));
		models.put(Automotion.newID("block/conveyor_belt_angled_right_rail"), new RailModel(RailModel.Side.RIGHT));

	}

	@Override
	public @Nullable UnbakedModel loadModelResource(Identifier identifier, ModelProviderContext modelProviderContext) {
		return this.models.get(identifier);
	}
}
