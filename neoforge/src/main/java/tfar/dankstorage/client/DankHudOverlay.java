package tfar.dankstorage.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import tfar.dankstorage.events.ClientEvents;

public class DankHudOverlay implements IGuiOverlay {
    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        ClientEvents.renderSelectedItem(guiGraphics, partialTick, screenWidth, screenHeight);
    }
}
