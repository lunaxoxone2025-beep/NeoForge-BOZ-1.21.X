package net.crazhfty.blackopsz.client;

import net.crazhfty.blackopsz.perks.PerkManager;
import net.crazhfty.blackopsz.perks.PerkType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PerkHUDOverlay implements LayeredDraw.Layer {

    @Override
    public void render(GuiGraphics g, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.options.hideGui) return;

        int activePerks = 0;
        for (int i = 0; i < 4; i++) {
            if (PerkManager.getPerk(player, i) != null) activePerks++;
        }
        if (activePerks == 0) return;

        int cardWidth = 108;
        int cardHeight = 18;
        int gap = 4;
        int x = 8;
        int y = g.guiHeight() - (activePerks * (cardHeight + gap)) - 10;

        // We only iterate through the 4 equipment slots in the menu
        for (int i = 0; i < 4; i++) {
            PerkType type = PerkManager.getPerk(player, i);

            if (type != null) {
                // Render the perk icon currently in this slot
                ItemStack stack = type.getIcon();
                g.fill(x - 1, y - 1, x + cardWidth + 1, y + cardHeight + 1, 0x88000000);
                g.fill(x, y, x + cardWidth, y + cardHeight, 0x60151515);
                g.renderFakeItem(stack, x + 1, y + 1);

                String label = shortLabel(type);
                g.drawString(mc.font, label, x + 21, y + 5, 0xF0F0F0, false);

                int maxCharges = PerkManager.getMaxCharges(type);
                if (maxCharges > 0) {
                    int current = PerkManager.getCurrentCharges(player, type);
                    drawChargeDots(g, x + 70, y + 5, maxCharges, current);
                } else if (type == PerkType.JUGGERNOG) {
                    int absorptionHearts = Math.round(player.getAbsorptionAmount() / 2.0f);
                    String text = "+" + absorptionHearts;
                    g.drawString(mc.font, text, x + 84, y + 5, absorptionHearts > 0 ? 0xFF7E7E : 0x777777, false);
                }

                y += cardHeight + gap;
            }
        }
    }

    private void drawChargeDots(GuiGraphics g, int startX, int y, int max, int current) {
        int dotSize = 6;
        int gap = 2;
        for (int i = 0; i < max; i++) {
            int x = startX + (i * (dotSize + gap));
            int color = i < current ? 0xFF7EE7FF : 0x553C4A50;
            g.fill(x, y, x + dotSize, y + dotSize, color);
        }
    }

    private String shortLabel(PerkType type) {
        return switch (type) {
            case WIDOWS_WINE -> "Widow";
            case JUGGERNOG -> "Jugger";
            case STAMIN_UP -> "Stamin";
            case SPEED_COLA -> "Speed";
            case DOUBLE_TAP -> "Double";
            case QUICK_REVIVE -> "Quick";
            case DEADSHOT -> "Dead";
            case PHD_FLOPPER -> "PhD";
        };
    }
}
