package dev.hephaestus.automotion.common;

import dev.hephaestus.automotion.Automotion;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class AutomotionItems {
    public static final Item CONVEYOR_BELT = Registry.register(Registry.ITEM, Automotion.id("conveyor_belt"), new BlockItem(AutomotionBlocks.CONVEYOR_BELT, new Item.Settings()));
    public static final Item BASIC_ENTITY_DETECTOR = Registry.register(Registry.ITEM, Automotion.id("basic_entity_detector"), new BlockItem(AutomotionBlocks.BASIC_ENTITY_DETECTOR, new Item.Settings()));

    public static void init() {

    }
}
