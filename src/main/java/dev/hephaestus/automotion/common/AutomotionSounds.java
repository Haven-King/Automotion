package dev.hephaestus.automotion.common;

import dev.hephaestus.automotion.Automotion;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;

public class AutomotionSounds {
    public static final SoundEvent CONVEYOR_BELT = Registry.register(Registry.SOUND_EVENT, Automotion.id("conveyor_belt"), new SoundEvent(Automotion.id("conveyor_belt")));

    public static void init() {

    }
}
