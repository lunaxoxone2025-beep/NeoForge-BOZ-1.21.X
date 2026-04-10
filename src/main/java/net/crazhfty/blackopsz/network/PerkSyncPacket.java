package net.crazhfty.blackopsz.perks;

import net.crazhfty.blackopsz.BlackOpsZ;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PerkSyncPacket(int slot, String perkName, boolean isEquipping) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PerkSyncPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(BlackOpsZ.MOD_ID, "perk_sync"));

    public static final StreamCodec<FriendlyByteBuf, PerkSyncPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, PerkSyncPacket::slot,
            ByteBufCodecs.STRING_UTF8, PerkSyncPacket::perkName,
            ByteBufCodecs.BOOL, PerkSyncPacket::isEquipping,
            PerkSyncPacket::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                if (slot < 0 || slot >= 4) return;

                PerkType type = null;
                if (perkName != null && !perkName.isEmpty()) {
                    try {
                        type = PerkType.valueOf(perkName);
                    } catch (Exception ignored) {}
                }

                if (isEquipping && type != null) {
                    if (PerkManager.getPerk(player, slot) != null) return;
                    if (PerkManager.hasPerk(player, type)) {
                        player.displayClientMessage(Component.translatable("perk.blackopsz.already_equipped"), true);
                        return;
                    }
                    if (!PerkManager.consumePerkItem(player, type)) {
                        player.displayClientMessage(Component.translatable("perk.blackopsz.no_item"), true);
                        return;
                    }
                    PerkManager.setPerk(player, slot, type);
                    PerkManager.playNotifySound(player, true);
                    player.displayClientMessage(Component.translatable("perk.blackopsz.equipped"), true);
                } else {
                    PerkType current = PerkManager.getPerk(player, slot);
                    if (current == null) return;
                    PerkManager.clearPerk(player, slot);
                    PerkManager.giveBackItem(player, current);
                    PerkManager.playNotifySound(player, true);
                    player.displayClientMessage(Component.translatable("perk.blackopsz.unequipped"), true);
                }
            }
        });
    }
}
