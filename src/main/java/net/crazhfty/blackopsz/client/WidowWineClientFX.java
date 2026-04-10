package net.crazhfty.blackopsz.client;

public class WidowWineClientFX {

    private static int flashTicks = 0;

    public static void triggerFlash() {
        flashTicks = 6;
    }

    public static void tick() {
        if (flashTicks > 0) {
            flashTicks--;
        }
    }

    public static float getFlash() {
        return flashTicks > 0 ? 1.0F : 0.0F;
    }
}