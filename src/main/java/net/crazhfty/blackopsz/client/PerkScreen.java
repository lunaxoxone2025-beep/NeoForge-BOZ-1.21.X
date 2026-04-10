package net.crazhfty.blackopsz.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PerkScreen extends Screen {

    public PerkScreen() {
        super(Component.literal("Perk Menu"));
    }

    @Override
    protected void init() {
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}