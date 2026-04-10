package net.crazhfty.blackopsz.perks;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PerkManager {
    private static final String WIDOWS_WINE_CHARGES_KEY = "widows_wine_charges";
    private static final String WIDOWS_WINE_REGEN_TICK_KEY = "widows_wine_regen_tick";

    public static PerkType getPerk(Player player, int slot) {
        String perkName = player.getPersistentData().getString("perk_slot_" + slot);
        if (perkName.isEmpty()) return null;
        try {
            return PerkType.valueOf(perkName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static void setPerk(Player player, int slot, PerkType type) {
        if (type == null) {
            clearPerk(player, slot);
        } else {
            player.getPersistentData().putString("perk_slot_" + slot, type.name());
            player.getPersistentData().putBoolean("perk_" + type.name().toLowerCase(), true);
            if (type == PerkType.WIDOWS_WINE) {
                setWidowsWineCharges(player, WidowsWinePerk.MAX_CHARGES);
                resetWidowsWineRegenTick(player, player.tickCount);
            }
            if (type == PerkType.JUGGERNOG) {
                player.getPersistentData().putInt("juggernog_plate_tick", player.tickCount);
            }
            // Sync marker to ensure the server processes the update
            player.getPersistentData().putInt("PerkUpdateTick", player.tickCount);
        }
    }

    public static void clearPerk(Player player, int slot) {
        PerkType type = getPerk(player, slot);
        if (type != null) {
            player.getPersistentData().remove("perk_" + type.name().toLowerCase());
            if (type == PerkType.WIDOWS_WINE) {
                player.getPersistentData().remove(WIDOWS_WINE_CHARGES_KEY);
                player.getPersistentData().remove(WIDOWS_WINE_REGEN_TICK_KEY);
            }
            if (type == PerkType.JUGGERNOG && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                JuggernogPerk.onUnequipped(serverPlayer);
            }
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                CorePerkEffects.onUnequipped(serverPlayer, type);
            }
        }
        player.getPersistentData().remove("perk_slot_" + slot);
        player.getPersistentData().putInt("PerkUpdateTick", player.tickCount);
    }

    public static void giveBackItem(Player player, PerkType type) {
        if (type != null) {
            ItemStack itemToGive = type.getIcon();
            if (!player.getInventory().add(itemToGive)) {
                player.drop(itemToGive, false);
            }
        }
    }

    public static boolean consumePerkItem(Player player, PerkType type) {
        if (type == null) return false;
        ItemStack target = type.getIcon();
        for (int i = 0; i < player.getInventory().items.size(); i++) {
            ItemStack stack = player.getInventory().items.get(i);
            if (!stack.isEmpty() && stack.getItem() == target.getItem()) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }

    public static boolean hasPerk(Player player, PerkType type) {
        if (type == null) return false;
        for (int i = 0; i < 4; i++) {
            String savedPerk = player.getPersistentData().getString("perk_slot_" + i);
            if (savedPerk.equalsIgnoreCase(type.name())) return true;
        }
        return player.getPersistentData().getBoolean("perk_" + type.name().toLowerCase());
    }

    public static void clearAllPerks(Player player) {
        for (int i = 0; i < 4; i++) clearPerk(player, i);
    }

    public static int getWidowsWineCharges(Player player) {
        return Math.max(0, Math.min(WidowsWinePerk.MAX_CHARGES,
                player.getPersistentData().getInt(WIDOWS_WINE_CHARGES_KEY)));
    }

    public static void setWidowsWineCharges(Player player, int charges) {
        player.getPersistentData().putInt(WIDOWS_WINE_CHARGES_KEY, Math.max(0, Math.min(WidowsWinePerk.MAX_CHARGES, charges)));
    }

    public static int getWidowsWineRegenTick(Player player) {
        return player.getPersistentData().getInt(WIDOWS_WINE_REGEN_TICK_KEY);
    }

    public static void resetWidowsWineRegenTick(Player player, int tick) {
        player.getPersistentData().putInt(WIDOWS_WINE_REGEN_TICK_KEY, tick);
    }

    public static int getMaxCharges(PerkType type) {
        if (type == PerkType.WIDOWS_WINE) return WidowsWinePerk.MAX_CHARGES;
        return 0;
    }

    public static int getCurrentCharges(Player player, PerkType type) {
        if (type == PerkType.WIDOWS_WINE) return getWidowsWineCharges(player);
        return 0;
    }

    // Useful for UI notification sounds
    public static void playNotifySound(Player player, boolean success) {
        player.level().playLocalSound(player.getX(), player.getY(), player.getZ(),
                success ? SoundEvents.EXPERIENCE_ORB_PICKUP : SoundEvents.VILLAGER_NO,
                SoundSource.PLAYERS, 0.8f, success ? 1.2f : 1.0f, false);
    }
}
