package net.crazhfty.blackopsz.item;

import java.util.function.Supplier;
import net.crazhfty.blackopsz.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "blackopsz");

    // Items Tab
    public static final Supplier<CreativeModeTab> AETHER_ITEMS_TAB = CREATIVE_MODE_TAB.register("aether_items_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.AETHER_GEM.get()))
                    .title(Component.translatable("itemGroup.blackopsz.aether_items")) // Changed from creativetab to itemGroup
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.AETHER_GEM.get());
                        output.accept(ModItems.AETHER_RAW.get());
                        output.accept(ModItems.ELEMENTAL_115_RAW.get());
                        output.accept(ModItems.ELEMENTAL_115_SHARD.get());
                        output.accept(ModItems.AETHER_REFINER.get());
                        // 🟣 PERK ITEM
                        output.accept(ModItems.WIDOWS_WINE_AMULET.get());
                        output.accept(ModItems.JUGGERNOG_TONIC.get());
                        output.accept(ModItems.STAMIN_UP_TONIC.get());
                        output.accept(ModItems.SPEED_COLA_TONIC.get());
                        output.accept(ModItems.DOUBLE_TAP_TONIC.get());
                        output.accept(ModItems.QUICK_REVIVE_TONIC.get());
                        output.accept(ModItems.DEADSHOT_TONIC.get());
                        output.accept(ModItems.PHD_FLOPPER_TONIC.get());
                    }).build());

    // Blocks Tab
    public static final Supplier<CreativeModeTab> AETHER_BLOCKS_TAB = CREATIVE_MODE_TAB.register("aether_blocks_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModBlocks.AETHER_CRYSTAL_BLOCK.get()))
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath("blackopsz", "aether_items_tab"))
                    .title(Component.translatable("itemGroup.blackopsz.aether_blocks"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.AETHER_CRYSTAL_BLOCK.get());
                        output.accept(ModBlocks.AETHER_CRYSTAL_ORE.get());
                        output.accept(ModBlocks.ELEMENTAL_115_BLOCK.get());
                        output.accept(ModBlocks.ELEMENTAL_115_ORE.get());
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
