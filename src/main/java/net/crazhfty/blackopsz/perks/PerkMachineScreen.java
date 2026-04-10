package net.crazhfty.blackopsz.client;

import net.crazhfty.blackopsz.perks.PerkManager;
import net.crazhfty.blackopsz.perks.PerkType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class PerkMachineScreen extends Screen {

    private PerkType dragging = null;

    public PerkMachineScreen() {
        super(Component.literal("Perk Machine"));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Player player = this.minecraft.player;
        if (player == null) return false;

        PerkType[] perks = PerkType.values();
        int startX = width / 2 - 120;
        int startY = height / 2 - 80;

        // Logic to pick a perk from the list
        for (int i = 0; i < perks.length; i++) {
            int x = startX + (i * 40);
            int y = startY;

            if (mouseX >= x && mouseX <= x + 32 && mouseY >= y && mouseY <= y + 32) {
                dragging = perks[i];
                return true;
            }
        }

        // Logic to drop the dragged perk into a slot
        if (dragging != null) {
            int slotStartX = width / 2 - 80;
            int slotStartY = height / 2;

            for (int s = 0; s < 4; s++) {
                int x = slotStartX;
                int y = slotStartY + (s * 30);

                if (mouseX >= x && mouseX <= x + 160 && mouseY >= y && mouseY <= y + 24) {
                    if (PerkManager.hasPerk(player, dragging)) {
                        player.displayClientMessage(Component.literal("§cAlready equipped!"), true);
                        dragging = null;
                        return true;
                    }

                    PerkManager.setPerk(player, s, dragging);
                    player.playSound(net.minecraft.sounds.SoundEvents.ITEM_PICKUP, 1f, 1f);
                    dragging = null;
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        // REMOVED: this.renderBackground to clear the blur/tint effect

        Player player = this.minecraft.player;
        if (player == null) return;

        int cx = width / 2;
        g.drawCenteredString(font, "§6PERK MACHINE", cx, 20, 0xFFFFFF);

        // Rendering the list of available perks
        PerkType[] perks = PerkType.values();
        int startX = cx - 120;
        int startY = 60;

        for (int i = 0; i < perks.length; i++) {
            g.renderItem(perks[i].getIcon(), startX + (i * 40), startY);
        }

        // Rendering the 4 perk slots
        int slotX = cx - 80;
        int slotY = 140;

        for (int s = 0; s < 4; s++) {
            PerkType perk = PerkManager.getPerk(player, s);
            g.fill(slotX, slotY + (s * 30), slotX + 160, slotY + (s * 30) + 24, 0x55000000);

            if (perk != null) {
                g.renderItem(perk.getIcon(), slotX + 5, slotY + (s * 30) + 4);
                g.drawString(font, perk.getDisplayName(), slotX + 30, slotY + (s * 30) + 8, 0x00FF00);
            } else {
                g.drawString(font, "--- EMPTY ---", slotX + 30, slotY + (s * 30) + 8, 0x777777);
            }
        }

        // Render the item currently being dragged
        if (dragging != null) {
            g.renderItem(dragging.getIcon(), mouseX - 8, mouseY - 8);
        }
        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}