package net.crazhfty.blackopsz.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;


public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems("blackopsz");

    public static final DeferredItem<Item> AETHER_RAW = ITEMS.register("aether_raw",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> AETHER_GEM = ITEMS.register("aether_gem",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> ELEMENTAL_115_RAW = ITEMS.register("elemental_115_raw",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> ELEMENTAL_115_SHARD = ITEMS.register("elemental_115_shard",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> ELEMENTAL_SHARD = ITEMS.register("elemental_shard",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> AETHER_REFINER = ITEMS.register("aether_refiner",
            () -> new RefinerItem(new Item.Properties()
                    .stacksTo(1)
                    .durability(16)));

    // 🟣 WIDOW'S WINE (PERK ITEM)
    public static final DeferredItem<Item> WIDOWS_WINE_AMULET = ITEMS.register("widows_wine_amulet",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static class RefinerItem extends Item {
        public RefinerItem(Properties properties) {
            super(properties);
        }

        @Override
        public boolean hasCraftingRemainingItem(ItemStack stack) {
            return true;
        }

        @Override
        public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
            ItemStack copy = itemStack.copy();
            copy.setDamageValue(itemStack.getDamageValue() + 1);

            if (copy.getDamageValue() >= copy.getMaxDamage()) {
                return ItemStack.EMPTY;
            }
            return copy;
        }
    }
}