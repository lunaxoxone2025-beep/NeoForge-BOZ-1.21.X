package net.crazhfty.blackopsz;

import net.crazhfty.blackopsz.block.ModBlocks;
import net.crazhfty.blackopsz.client.ClientForgeEvents;
import net.crazhfty.blackopsz.client.ClientModEvents;
import net.crazhfty.blackopsz.events.ChestLootEvents;
import net.crazhfty.blackopsz.events.PlayerEvents;
import net.crazhfty.blackopsz.item.ModCreativeModeTabs;
import net.crazhfty.blackopsz.item.ModItems;
import net.crazhfty.blackopsz.perks.PerkSyncPacket;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(BlackOpsZ.MOD_ID)
public class BlackOpsZ {
    public static final String MOD_ID = "blackopsz";

    public BlackOpsZ(IEventBus modEventBus, ModContainer modContainer) {
        ModCreativeModeTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        // Networking registration
        modEventBus.addListener(this::registerPayloadHandlers);

        // Game event registration
        NeoForge.EVENT_BUS.register(new PlayerEvents());
        NeoForge.EVENT_BUS.register(new ChestLootEvents());

        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(this::onClientSetup);
            modEventBus.register(new ClientModEvents());
        }

        modEventBus.addListener(this::addCreative);
    }

    private void registerPayloadHandlers(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MOD_ID);

        // Register perk synchronization packet
        registrar.playToServer(
                PerkSyncPacket.TYPE,
                PerkSyncPacket.CODEC,
                PerkSyncPacket::handle
        );
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(new ClientForgeEvents());
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.AETHER_RAW);
        }
    }
}
