//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package net.crazhfty.blackopsz;

import com.mojang.logging.LogUtils;
import net.crazhfty.blackopsz.block.ModBlocks;
import net.crazhfty.blackopsz.item.ModCreativeModeTabs;
import net.crazhfty.blackopsz.item.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;
import net.crazhfty.blackopsz.events.PlayerEvents;

@Mod("blackopsz")
public class BlackOpsZ {
    public static final String MOD_ID = "blackopsz";
    public static final Logger LOGGER = LogUtils.getLogger();


    public BlackOpsZ(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);
        ModCreativeModeTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        modEventBus.addListener(this::addCreative);
        modContainer.registerConfig(Type.COMMON, Config.SPEC);
        NeoForge.EVENT_BUS.register(PlayerEvents.class);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.AETHER_RAW);
            event.accept(ModItems.AETHER_GEM);
        }

        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModBlocks.AETHER_CRYSTAL_BLOCK);
            event.accept(ModBlocks.AETHER_CRYSTAL_ORE);
        }

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @EventBusSubscriber(
            modid = "blackopsz",
            bus = Bus.MOD,
            value = {Dist.CLIENT}
    )
    static class ClientModEvents {
        ClientModEvents() {
        }

        @SubscribeEvent
        static void onClientSetup(FMLClientSetupEvent event) {
        }
    }
}
