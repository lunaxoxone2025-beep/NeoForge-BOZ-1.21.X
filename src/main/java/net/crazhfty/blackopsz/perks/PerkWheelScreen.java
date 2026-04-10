package net.crazhfty.blackopsz.perks;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PerkWheelScreen extends Screen {
    private static final int SLOT_COUNT = 4;
    private static final int SLOT_WIDTH = 200;
    private static final int SLOT_HEIGHT = 24;
    private static final int SLOT_GAP = 8;
    private static PerkType lastSelectedPerk;

    private PerkType selectedPerk;
    private List<PerkInventoryEntry> cachedInventoryPerks = List.of();

    public PerkWheelScreen() {
        super(Component.translatable("menu.blackopsz.perk_loadout"));
        this.selectedPerk = lastSelectedPerk;
    }

    @Override
    protected void init() {
        if (selectedPerk == null) {
            selectedPerk = lastSelectedPerk;
        }
        super.init();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Player player = this.minecraft.player;
        if (player == null) return false;

        for (PerkInventoryEntry entry : cachedInventoryPerks) {
            if (entry.contains(mouseX, mouseY)) {
                selectedPerk = entry.type();
                lastSelectedPerk = selectedPerk;
                return true;
            }
        }

        int startX = width / 2 - (SLOT_WIDTH / 2);
        int startY = height / 2 - 74;
        for (int i = 0; i < SLOT_COUNT; i++) {
            int x = startX;
            int y = startY + (i * (SLOT_HEIGHT + SLOT_GAP));

            if (mouseX >= x && mouseX <= x + SLOT_WIDTH && mouseY >= y && mouseY <= y + SLOT_HEIGHT) {
                PerkType current = PerkManager.getPerk(player, i);
                if (current != null) {
                    PacketDistributor.sendToServer(new PerkSyncPacket(i, "", false));
                    return true;
                }

                if (selectedPerk == null) {
                    PerkManager.playNotifySound(player, false);
                    player.displayClientMessage(Component.translatable("perk.blackopsz.select_perk"), true);
                    return true;
                }

                if (PerkManager.hasPerk(player, selectedPerk)) {
                    PerkManager.playNotifySound(player, false);
                    player.displayClientMessage(Component.translatable("perk.blackopsz.already_equipped"), true);
                    return true;
                }

                PacketDistributor.sendToServer(new PerkSyncPacket(i, selectedPerk.name(), true));
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        Player player = this.minecraft.player;
        if (player == null) return;

        int cx = width / 2;
        int startX = cx - (SLOT_WIDTH / 2);
        int startY = height / 2 - 74;

        cachedInventoryPerks = getInventoryPerks(player);

        g.drawCenteredString(font, "§6§lPERK LOADOUT", cx, startY - 24, 0xFFFFFF);
        g.drawCenteredString(font, Component.translatable("menu.blackopsz.perk_loadout.help"), cx, startY - 12, 0xB0B0B0);

        for (int i = 0; i < SLOT_COUNT; i++) {
            int x = startX;
            int y = startY + (i * (SLOT_HEIGHT + SLOT_GAP));
            PerkType equipped = PerkManager.getPerk(player, i);
            boolean hover = mouseX >= x && mouseX <= x + SLOT_WIDTH && mouseY >= y && mouseY <= y + SLOT_HEIGHT;

            int border = hover ? 0xFFE0B34A : 0xFF444444;
            int bg = hover ? 0xB0202020 : 0x90151515;
            g.fill(x - 1, y - 1, x + SLOT_WIDTH + 1, y + SLOT_HEIGHT + 1, border);
            g.fill(x, y, x + SLOT_WIDTH, y + SLOT_HEIGHT, bg);

            g.drawString(font, "Slot " + (i + 1), x + 6, y + 8, 0x9E9E9E, false);

            if (equipped != null) {
                g.renderItem(equipped.getIcon(), x + 56, y + 4);
                g.drawString(font, equipped.getDisplayName(), x + 80, y + 8, 0x8AE3FF, false);
            } else {
                g.drawString(font, Component.translatable("perk.blackopsz.empty_slot"), x + 56, y + 8, 0x777777, false);
            }
        }

        int inventoryY = startY + (SLOT_COUNT * (SLOT_HEIGHT + SLOT_GAP)) + 12;
        g.drawString(font, Component.translatable("menu.blackopsz.perk_loadout.available"), startX, inventoryY, 0xD8D8D8, false);

        if (cachedInventoryPerks.isEmpty()) {
            g.drawString(font, Component.translatable("perk.blackopsz.no_perks_inventory"), startX, inventoryY + 14, 0x999999, false);
        } else {
            for (PerkInventoryEntry entry : cachedInventoryPerks) {
                int border = entry.type() == selectedPerk ? 0xFF58D5FF : 0xFF505050;
                g.fill(entry.x() - 1, entry.y() - 1, entry.x() + entry.w() + 1, entry.y() + entry.h() + 1, border);
                g.fill(entry.x(), entry.y(), entry.x() + entry.w(), entry.y() + entry.h(), 0xB01A1A1A);
                g.renderItem(entry.type().getIcon(), entry.x() + 3, entry.y() + 3);
                g.drawString(font, "x" + entry.count(), entry.x() + 23, entry.y() + 8, 0xFFFFFF, false);
            }
        }

        if (selectedPerk != null) {
            g.drawCenteredString(font,
                    Component.translatable("perk.blackopsz.selected", selectedPerk.getDisplayName()),
                    cx, inventoryY + 34, 0x7EE3FF);
        }

        PerkInventoryEntry hoveredEntry = null;
        for (PerkInventoryEntry entry : cachedInventoryPerks) {
            if (entry.contains(mouseX, mouseY)) {
                hoveredEntry = entry;
                break;
            }
        }
        if (hoveredEntry != null) {
            g.renderTooltip(font, getPerkTooltip(player, hoveredEntry.type(), hoveredEntry.count(), true), Optional.empty(), mouseX, mouseY);
        } else {
            for (int i = 0; i < SLOT_COUNT; i++) {
                int x = startX;
                int y = startY + (i * (SLOT_HEIGHT + SLOT_GAP));
                if (mouseX >= x && mouseX <= x + SLOT_WIDTH && mouseY >= y && mouseY <= y + SLOT_HEIGHT) {
                    PerkType equipped = PerkManager.getPerk(player, i);
                    if (equipped != null) {
                        g.renderTooltip(font, getPerkTooltip(player, equipped, 0, false), Optional.empty(), mouseX, mouseY);
                    } else {
                        g.renderTooltip(font, List.of(Component.translatable("perk.blackopsz.empty_slot_tooltip")), Optional.empty(), mouseX, mouseY);
                    }
                    break;
                }
            }
        }

        super.render(g, mouseX, mouseY, partialTick);
    }

    private List<Component> getPerkTooltip(Player player, PerkType perk, int inventoryCount, boolean inventoryEntry) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal("§b" + perk.getDisplayName()));
        if (inventoryEntry) {
            lines.add(Component.translatable("perk.blackopsz.tooltip.inventory_count", inventoryCount));
            lines.add(Component.translatable("perk.blackopsz.tooltip.click_select"));
        } else {
            lines.add(Component.translatable("perk.blackopsz.tooltip.click_unequip"));
        }

        if (perk == PerkType.WIDOWS_WINE) {
            lines.add(Component.translatable("perk.blackopsz.tooltip.widows.trigger"));
            lines.add(Component.translatable("perk.blackopsz.tooltip.widows.charges", PerkManager.getWidowsWineCharges(player), WidowsWinePerk.MAX_CHARGES));
            lines.add(Component.translatable("perk.blackopsz.tooltip.widows.regen", WidowsWinePerk.getRegenIntervalTicks() / 20));
            lines.add(Component.translatable("perk.blackopsz.tooltip.widows.radius", String.format("%.1f", WidowsWinePerk.getTrapRadius())));
            lines.add(Component.translatable("perk.blackopsz.tooltip.widows.duration", WidowsWinePerk.getTrapDurationTicks() / 20));
            lines.add(Component.translatable("perk.blackopsz.tooltip.widows.cooldown", WidowsWinePerk.getCooldownTimeMs() / 1000.0));
        } else if (perk == PerkType.JUGGERNOG) {
            lines.add(Component.translatable("perk.blackopsz.tooltip.jugger.passive"));
            lines.add(Component.translatable("perk.blackopsz.tooltip.jugger.plate", JuggernogPerk.getPlateIntervalTicks() / 20, JuggernogPerk.getPlateDurationTicks() / 20));
            lines.add(Component.translatable("perk.blackopsz.tooltip.jugger.last_stand", JuggernogPerk.getLastStandDurationTicks() / 20, JuggernogPerk.getLastStandCooldownTicks() / 20));
        } else if (perk == PerkType.STAMIN_UP) {
            lines.add(Component.translatable("perk.blackopsz.tooltip.stamin_up"));
        } else if (perk == PerkType.SPEED_COLA) {
            lines.add(Component.translatable("perk.blackopsz.tooltip.speed_cola"));
        } else if (perk == PerkType.DOUBLE_TAP) {
            lines.add(Component.translatable("perk.blackopsz.tooltip.double_tap"));
        } else if (perk == PerkType.QUICK_REVIVE) {
            lines.add(Component.translatable("perk.blackopsz.tooltip.quick_revive"));
        } else if (perk == PerkType.DEADSHOT) {
            lines.add(Component.translatable("perk.blackopsz.tooltip.deadshot"));
        } else if (perk == PerkType.PHD_FLOPPER) {
            lines.add(Component.translatable("perk.blackopsz.tooltip.phd_flopper"));
        }
        return lines;
    }

    private List<PerkInventoryEntry> getInventoryPerks(Player player) {
        Map<PerkType, Integer> counts = new EnumMap<>(PerkType.class);
        for (PerkType perk : PerkType.values()) {
            counts.put(perk, 0);
        }

        Inventory inventory = player.getInventory();
        for (ItemStack stack : inventory.items) {
            if (stack.isEmpty()) continue;
            for (PerkType perk : PerkType.values()) {
                if (stack.getItem() == perk.getIcon().getItem()) {
                    counts.put(perk, counts.get(perk) + stack.getCount());
                    break;
                }
            }
        }

        List<PerkInventoryEntry> entries = new ArrayList<>();
        int startX = width / 2 - (SLOT_WIDTH / 2);
        int y = height / 2 - 74 + (SLOT_COUNT * (SLOT_HEIGHT + SLOT_GAP)) + 26;
        int index = 0;
        for (PerkType perk : PerkType.values()) {
            int count = counts.get(perk);
            if (count <= 0) continue;

            int x = startX + (index * 64);
            entries.add(new PerkInventoryEntry(perk, count, x, y, 56, 22));
            index++;
        }
        return entries;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
    }

    private record PerkInventoryEntry(PerkType type, int count, int x, int y, int w, int h) {
        boolean contains(double mouseX, double mouseY) {
            return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
        }
    }
}
