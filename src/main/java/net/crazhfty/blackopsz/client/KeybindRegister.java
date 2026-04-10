package net.crazhfty.blackopsz.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = "blackopsz", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class KeybindRegister {

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(Keybinds.OPEN_PERK_MENU);
    }
}