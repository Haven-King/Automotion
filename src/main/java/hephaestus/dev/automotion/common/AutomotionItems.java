package hephaestus.dev.automotion.common;

import hephaestus.dev.automotion.common.item.ConnectedBlockItem;
import hephaestus.dev.automotion.common.item.GhostBlockItem;
import hephaestus.dev.automotion.common.item.WrenchItem;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

import static hephaestus.dev.automotion.common.Automotion.newID;

public class AutomotionItems {
    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.create(newID(Automotion.MOD_ID)).icon(() -> new ItemStack(AutomotionItems.CONVEYOR_BELT)).build();

    public static final BlockItem CONVEYOR_BELT = new GhostBlockItem(AutomotionBlocks.CONVEYOR_BELT, new Item.Settings().group(ITEM_GROUP));
    public static final BlockItem GOLDEN_CONVEYOR_BELT = new GhostBlockItem(AutomotionBlocks.GOLDEN_CONVEYOR_BELT, new Item.Settings().group(ITEM_GROUP));
    public static final BlockItem DETECTOR_CONVEYOR_BELT = new GhostBlockItem(AutomotionBlocks.DETECTOR_CONVEYOR_BELT, new Item.Settings().group(ITEM_GROUP));
    public static final BlockItem HEATED_CONVEYOR_BELT = new GhostBlockItem(AutomotionBlocks.HEATED_CONVEYOR_BELT, new Item.Settings().group(ITEM_GROUP));
    public static final BlockItem GOLDEN_HOPPER = new BlockItem(AutomotionBlocks.GOLDEN_HOPPER, new Item.Settings().group(ITEM_GROUP));
    public static final BlockItem DIAMOND_HOPPER = new BlockItem(AutomotionBlocks.DIAMOND_HOPPER, new Item.Settings().group(ITEM_GROUP));
    public static final BlockItem WEAK_FAN = new ConnectedBlockItem(AutomotionBlocks.WEAK_FAN, new Item.Settings().group(ITEM_GROUP));
    public static final BlockItem STRONG_FAN = new ConnectedBlockItem(AutomotionBlocks.STRONG_FAN, new Item.Settings().group(ITEM_GROUP));

    public static final BlockItem IRON_DUCT = new BlockItem(AutomotionBlocks.IRON_DUCT, new Item.Settings().group(ITEM_GROUP));
    public static final BlockItem GLASS_DUCT = new BlockItem(AutomotionBlocks.GLASS_DUCT, new Item.Settings().group(ITEM_GROUP));
    public static final BlockItem IRON_DUCT_OPENING = new BlockItem(AutomotionBlocks.IRON_DUCT_OPENING, new Item.Settings().group(ITEM_GROUP));
    public static final BlockItem IRON_DUCT_DOOR = new BlockItem(AutomotionBlocks.IRON_DUCT_DOOR, new Item.Settings().group(ITEM_GROUP));

    public static final Item WRENCH = new WrenchItem(new Item.Settings().group(ITEM_GROUP));

    public static void init() {
        Registry.register(Registry.ITEM, newID("conveyor_belt"), CONVEYOR_BELT);
        Registry.register(Registry.ITEM, newID("golden_conveyor_belt"), GOLDEN_CONVEYOR_BELT);
        Registry.register(Registry.ITEM, newID("detector_conveyor_belt"), DETECTOR_CONVEYOR_BELT);
        Registry.register(Registry.ITEM, newID("heated_conveyor_belt"), HEATED_CONVEYOR_BELT);
        Registry.register(Registry.ITEM, newID("golden_hopper"), GOLDEN_HOPPER);
        Registry.register(Registry.ITEM, newID("diamond_hopper"), DIAMOND_HOPPER);
        Registry.register(Registry.ITEM, newID("weak_fan"), WEAK_FAN);
        Registry.register(Registry.ITEM, newID("strong_fan"), STRONG_FAN);
        Registry.register(Registry.ITEM, newID("wrench"), WRENCH);

        Registry.register(Registry.ITEM, newID("iron_duct"), IRON_DUCT);
        Registry.register(Registry.ITEM, newID("glass_duct"), GLASS_DUCT);
        Registry.register(Registry.ITEM, newID("iron_duct_opening"), IRON_DUCT_OPENING);
        Registry.register(Registry.ITEM, newID("iron_duct_door"), IRON_DUCT_DOOR);
    }
}
