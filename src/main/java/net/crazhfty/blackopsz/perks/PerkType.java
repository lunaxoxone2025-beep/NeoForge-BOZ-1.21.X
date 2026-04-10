package net.crazhfty.blackopsz.perks;

import net.crazhfty.blackopsz.item.ModItems;
import net.minecraft.world.item.ItemStack;
import java.util.function.Supplier;

public enum PerkType {
    STAMIN_UP(
            "stamin_up",
            "Stamin-Up",
            () -> new ItemStack(ModItems.STAMIN_UP_TONIC.get())
    ),
    SPEED_COLA(
            "speed_cola",
            "Speed Cola",
            () -> new ItemStack(ModItems.SPEED_COLA_TONIC.get())
    ),
    DOUBLE_TAP(
            "double_tap",
            "Double Tap",
            () -> new ItemStack(ModItems.DOUBLE_TAP_TONIC.get())
    ),
    QUICK_REVIVE(
            "quick_revive",
            "Quick Revive",
            () -> new ItemStack(ModItems.QUICK_REVIVE_TONIC.get())
    ),
    DEADSHOT(
            "deadshot",
            "Deadshot Daiquiri",
            () -> new ItemStack(ModItems.DEADSHOT_TONIC.get())
    ),
    PHD_FLOPPER(
            "phd_flopper",
            "PhD Flopper",
            () -> new ItemStack(ModItems.PHD_FLOPPER_TONIC.get())
    ),
    JUGGERNOG(
            "juggernog",
            "Jugger-Nog",
            () -> new ItemStack(ModItems.JUGGERNOG_TONIC.get())
    ),
    WIDOWS_WINE(
            "widows_wine",
            "Widow's Wine", // English display text
            () -> new ItemStack(ModItems.WIDOWS_WINE_AMULET.get())
    );

    private final String id;
    private final String displayName;
    private final Supplier<ItemStack> icon;

    PerkType(String id, String displayName, Supplier<ItemStack> icon) {
        this.id = id;
        this.displayName = displayName;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ItemStack getIcon() {
        return icon.get().copy();
    }

    public static PerkType fromId(String id) {
        for (PerkType p : values()) {
            if (p.id.equals(id)) return p;
        }
        return null;
    }
}
