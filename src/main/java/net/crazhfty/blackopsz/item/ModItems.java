//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package net.crazhfty.blackopsz.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("blackopsz");
    public static final DeferredItem<Item> AETHER_RAW;
    public static final DeferredItem<Item> AETHER_GEM;

    public ModItems() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    static {
        AETHER_RAW = ITEMS.register("aether_raw", () -> new Item(new Item.Properties()));
        AETHER_GEM = ITEMS.register("aether_gem", () -> new Item(new Item.Properties()));
    }
}
