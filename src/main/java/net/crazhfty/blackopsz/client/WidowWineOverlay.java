package net.crazhfty.blackopsz.client;

import net.crazhfty.blackopsz.perks.PerkManager;
import net.crazhfty.blackopsz.perks.PerkType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

public class WidowWineOverlay {

    @SubscribeEvent
    public void onRenderOverlay(RenderGuiLayerEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        // If the player has the perk active, render the icon on the HUD
        if (PerkManager.hasPerk(mc.player, PerkType.WIDOWS_WINE)) {
            GuiGraphics guiGraphics = event.getGuiGraphics();

            // Position: bottom-left corner, slightly above the hotbar
            int x = 10;
            int y = guiGraphics.guiHeight() - 40;

            // Render the amulet (icon defined in PerkType)
            guiGraphics.renderFakeItem(PerkType.WIDOWS_WINE.getIcon(), x, y);
        }
    }

    // Legacy methods kept for compatibility but no visual use
    public static void triggerFlash() {}
}
