package tfar.dankstorage.event;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import tfar.dankstorage.events.ClientEvents;

public class FabricEvents {
    static Minecraft mc = Minecraft.getInstance();
    public static void renderStack(GuiGraphics matrixStack, DeltaTracker v) {
        ClientEvents.renderSelectedItem(matrixStack, v);
    }
}
