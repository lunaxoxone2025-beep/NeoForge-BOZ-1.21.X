package net.crazhfty.blackopsz.client;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

public class ClientModEvents {

    @SubscribeEvent
    public void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(
                ResourceLocation.fromNamespaceAndPath("blackopsz", "perk_hud"),
                new PerkHUDOverlay()
        );
    }
}