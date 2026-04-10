package net.crazhfty.blackopsz.events;

import net.crazhfty.blackopsz.perks.CorePerkEffects;
import net.crazhfty.blackopsz.perks.JuggernogPerk;
import net.crazhfty.blackopsz.perks.WidowsWinePerk;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class PlayerEvents {

    @SubscribeEvent
    public void onPlayerDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            WidowsWinePerk.triggerOnHit(player, event.getSource());
            if (player instanceof ServerPlayer serverPlayer) {
                JuggernogPerk.onIncomingDamage(serverPlayer, event.getSource());
            }
        }
        CorePerkEffects.onIncomingDamage(event);
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            WidowsWinePerk.tickRegen(serverPlayer);
            JuggernogPerk.tick(serverPlayer);
            CorePerkEffects.tick(serverPlayer);
        }
    }
}
