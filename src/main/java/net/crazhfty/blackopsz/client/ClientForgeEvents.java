package net.crazhfty.blackopsz.client;

import net.crazhfty.blackopsz.perks.PerkWheelScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class ClientForgeEvents {

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        while (Keybinds.OPEN_PERK_MENU.consumeClick()) {
            mc.setScreen(new PerkWheelScreen());
        }
    }
}