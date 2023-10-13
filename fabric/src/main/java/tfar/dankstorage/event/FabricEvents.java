package tfar.dankstorage.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import tfar.dankstorage.events.ClientEvents;

public class FabricEvents {
    static Minecraft mc = Minecraft.getInstance();
    public static void renderStack(GuiGraphics matrixStack, float v) {
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        ClientEvents.renderSelectedItem(matrixStack, v, screenWidth, screenHeight);
    }
}
