package tfar.dankstorage.client.screens;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.menu.ChangeFrequencyMenu;

public class ChangeFrequencyScreen extends DankHolderScreen<ChangeFrequencyMenu> {

    public static final Identifier BACKGROUND_LOCATION = DankStorage.id("background");

    public ChangeFrequencyScreen(ChangeFrequencyMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2,176,166);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics,mouseX,mouseY);
        MutableComponent warning = Component.translatable("text.dankstorage.tier_mismatch");
        graphics.textWithWordWrap(font,warning,5,inventoryLabelY+18,imageWidth-4,DARK_GRAY,false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        super.extractBackground(guiGraphics, mouseX, mouseY, a);
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,BACKGROUND_LOCATION,leftPos,topPos,
                imageWidth,imageHeight);
    }
}
