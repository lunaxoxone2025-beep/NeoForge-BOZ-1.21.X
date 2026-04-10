package net.crazhfty.blackopsz.client;

import net.crazhfty.blackopsz.perks.PerkWheelScreen; // <-- DEBE APUNTAR A .perks
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

public class ClientEvents {

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        while (Keybinds.OPEN_PERK_MENU.consumeClick()) {
            mc.setScreen(new PerkWheelScreen());
        }
    }

    @SubscribeEvent
    public void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(
                ResourceLocation.fromNamespaceAndPath("blackopsz", "perk_hud"),
                new PerkHUDOverlay()
        );
    }
}