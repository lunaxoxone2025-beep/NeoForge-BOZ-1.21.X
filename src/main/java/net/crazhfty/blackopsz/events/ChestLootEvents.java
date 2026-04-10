package net.crazhfty.blackopsz.events;

import net.crazhfty.blackopsz.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;

public class ChestLootEvents {

    @SubscribeEvent
    public void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation tableId = event.getName();
        if (!tableId.getPath().startsWith("chests/")) return;

        LootPool pool = LootPool.lootPool()
                .name("blackopsz_perk_injection")
                .setRolls(ConstantValue.exactly(1))
                .when(LootItemRandomChanceCondition.randomChance(0.12f))
                .add(LootItem.lootTableItem(ModItems.WIDOWS_WINE_AMULET.get()).setWeight(9))
                .add(LootItem.lootTableItem(ModItems.JUGGERNOG_TONIC.get()).setWeight(9))
                .add(LootItem.lootTableItem(ModItems.STAMIN_UP_TONIC.get()).setWeight(7))
                .add(LootItem.lootTableItem(ModItems.SPEED_COLA_TONIC.get()).setWeight(7))
                .add(LootItem.lootTableItem(ModItems.DOUBLE_TAP_TONIC.get()).setWeight(7))
                .add(LootItem.lootTableItem(ModItems.QUICK_REVIVE_TONIC.get()).setWeight(7))
                .add(LootItem.lootTableItem(ModItems.DEADSHOT_TONIC.get()).setWeight(6))
                .add(LootItem.lootTableItem(ModItems.PHD_FLOPPER_TONIC.get()).setWeight(6))
                .build();

        event.getTable().addPool(pool);
    }
}
