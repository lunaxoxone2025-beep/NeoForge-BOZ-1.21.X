package net.crazhfty.blackopsz.events;

import net.crazhfty.blackopsz.perks.WidowsWinePerk;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class PlayerEvents {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        WidowsWinePerk.tick(player);
    }

    @SubscribeEvent
    public static void onHurt(LivingDamageEvent.Post event) {

        if (!(event.getEntity() instanceof Player player)) return;

        WidowsWinePerk.onPlayerHurt(player);
    }
}