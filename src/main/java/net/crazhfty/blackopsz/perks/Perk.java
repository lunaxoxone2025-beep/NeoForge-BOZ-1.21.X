package net.crazhfty.blackopsz.perks;

import net.minecraft.world.entity.player.Player;

public interface Perk {

    String id();

    void onTick(Player player);

    void onHurt(Player player);

}