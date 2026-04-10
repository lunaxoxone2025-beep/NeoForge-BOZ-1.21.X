package net.crazhfty.blackopsz.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class Keybinds {

    public static final KeyMapping OPEN_PERK_MENU =
            new KeyMapping(
                    "key.blackopsz.perk_menu",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_P,
                    "key.categories.blackopsz"
            );
}