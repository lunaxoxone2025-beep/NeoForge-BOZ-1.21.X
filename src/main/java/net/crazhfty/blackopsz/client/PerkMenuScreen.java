package net.crazhfty.blackopsz.client;

import net.crazhfty.blackopsz.perks.PerkManager;
import net.crazhfty.blackopsz.perks.PerkType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PerkMenuScreen extends Screen {

    private static final int SLOTS = 4;

    public PerkMenuScreen() {
        super(Component.literal("Perk Machine"));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Player player = minecraft.player;
        if (player == null) return false;

        int cx = width / 2;
        int cy = height / 2;
        int startX = cx - 110;
        int startY = cy - 60;

        // Currently hardcoded to Widow's Wine for testing
        PerkType perk = PerkType.WIDOWS_WINE;

        for (int i = 0; i < SLOTS; i++) {
            int x = startX;
            int y = startY + (i * 28);

            if (mouseX >= x && mouseX <= x + 220 && mouseY >= y && mouseY <= y + 22) {
                // Check if perk is already owned (Anywhere: NBT or Inventory)
                if (PerkManager.hasPerk(player, perk)) {
                    player.displayClientMessage(Component.literal("§cAlready active!"), true);
                    return true;
                }

                // Check if the specific slot is occupied
                if (PerkManager.getPerk(player, i) != null) {
                    player.displayClientMessage(Component.literal("§cSlot occupied!"), true);
                    return true;
                }

                // Assign perk and consume item
                PerkManager.setPerk(player, i, perk);
                consumePerkItem(player, perk);

                player.level().playLocalSound(
                        player.getX(), player.getY(), player.getZ(),
                        SoundEvents.EXPERIENCE_ORB_PICKUP,
                        SoundSource.PLAYERS,
                        1f, 1f, false
                );
                return true;
            }
        }
        return false;
    }

    private void consumePerkItem(Player player, PerkType perk) {
        ItemStack target = perk.getIcon();
        for (int i = 0; i < player.getInventory().items.size(); i++) {
            ItemStack stack = player.getInventory().items.get(i);
            if (!stack.isEmpty() && stack.getItem() == target.getItem()) {
                stack.shrink(1);
                break;
            }
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        // By NOT calling renderBackground or super.renderBackground, the background stays fully transparent.

        int cx = width / 2;
        int cy = height / 2;
        int startX = cx - 110;
        int startY = cy - 60;
        Player player = minecraft.player;

        // Render Title
        g.drawCenteredString(font, "§6§lPERK VENDING MACHINE", cx, startY - 30, 0xFFFFFF);

        for (int i = 0; i < SLOTS; i++) {
            int x = startX;
            int y = startY + (i * 28);
            var perk = (player != null) ? PerkManager.getPerk(player, i) : null;

            // Slot Background (Black with alpha for slight readability against the world)
            int bg = 0x88000000;
            if (mouseX >= x && mouseX <= x + 220 && mouseY >= y && mouseY <= y + 22) {
                bg = 0xCC000000; // Hover effect: Darker background
            }

            g.fill(x, y, x + 220, y + 22, bg);

            if (perk != null) {
                g.renderItem(perk.getIcon(), x + 4, y + 3);
                g.drawString(font, perk.getDisplayName().toUpperCase(), x + 28, y + 7, 0x00FF00);
            } else {
                g.drawString(font, "EMPTY SLOT", x + 28, y + 7, 0x555555);
            }
        }

        // Render tooltips and other default screen elements
        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        // Overridden and intentionally left empty to force full transparency (no blur).
    }

    @Override
    public boolean isPauseScreen() {
        return false; // The game does not pause when opening the machine
    }
}
