package dev.hephaestus.automotion.common;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class AutomotionItems {
    public static final Item CONVEYOR_BELT = register(AutomotionBlocks.CONVEYOR_BELT, new Item.Settings());
    public static final Item BASIC_ENTITY_DETECTOR = register(AutomotionBlocks.BASIC_ENTITY_DETECTOR, new Item.Settings());
    public static final Item STEEL_BLOCK = register(AutomotionBlocks.STEEL_BLOCK, new Item.Settings());
    public static final Item LIMESTONE = register(AutomotionBlocks.LIMESTONE, new Item.Settings());
    public static final Item LIMESTONE_BRICKS = register(AutomotionBlocks.LIMESTONE_BRICKS, new Item.Settings());
    public static final Item COKE_OVEN = register(AutomotionBlocks.COKE_OVEN, new Item.Settings());

    public static void init() {

    }

    private static BlockItem register(Block block, Item.Settings settings) {
        return Registry.register(Registry.ITEM, Registry.BLOCK.getId(block), new BlockItem(block, settings));
    }
}
