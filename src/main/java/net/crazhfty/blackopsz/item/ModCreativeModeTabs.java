//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

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
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB;
    public static final Supplier<CreativeModeTab> AETHER_ITEMS_TAB;
    public static final Supplier<CreativeModeTab> AETHER_BLOCKS_TAB;

    public ModCreativeModeTabs() {
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }

    static {

        //items tab
        CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "blackopsz");
        AETHER_ITEMS_TAB = CREATIVE_MODE_TAB.register("aether_items_tab", () -> CreativeModeTab.builder().icon(() -> new ItemStack((ItemLike)ModItems.AETHER_GEM.get())).title(Component.translatable("creativetab.blackopsz.aether_items")).displayItems((itemDisplayParameters, output) -> {
            output.accept((ItemLike)ModItems.AETHER_GEM.get());
            output.accept((ItemLike)ModItems.AETHER_RAW.get());
            output.accept((ItemLike)ModItems.ELEMENTAL_115_RAW.get());
            output.accept((ItemLike)ModItems.ELEMENTAL_115_SHARD.get());
            output.accept((ItemLike)ModItems.AETHER_REFINER.get());
        }).build());
        // blocks tab
        AETHER_BLOCKS_TAB = CREATIVE_MODE_TAB.register("aether_blocks_tab", () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.AETHER_CRYSTAL_BLOCK)).withTabsBefore(new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("blackopsz", "aether_items_tab")}).title(Component.translatable("creativetab.blackopsz.aether_blocks")).displayItems((itemDisplayParameters, output) -> {
            output.accept((ItemLike)ModBlocks.AETHER_CRYSTAL_BLOCK.get());
            output.accept((ItemLike)ModBlocks.AETHER_CRYSTAL_ORE.get());
            output.accept((ItemLike)ModBlocks.ELEMENTAL_115_BLOCK.get());
            output.accept((ItemLike)ModBlocks.ELEMENTAL_115_ORE.get());
        }).build());
    }
}
