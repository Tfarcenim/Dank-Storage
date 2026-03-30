package tfar.dankstorage.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.platform.Services;
import tfar.dankstorage.utils.CommonUtils;

public class ClientDankTooltip implements ClientTooltipComponent {
    private static final int MARGIN_Y = 4;
    private static final int BORDER_WIDTH = 1;
    private static final int TEX_SIZE = 128;
    private static final int SLOT_SIZE_X = 18;
    private static final int SLOT_SIZE_Y = 18;
    private final NonNullList<ItemStack> items;
    private final ItemStack selected;

    public ClientDankTooltip(DankTooltip bundleTooltip) {
        this.items = bundleTooltip.getItems();
        this.selected = bundleTooltip.getSelected();
    }

    @Override
    public int getHeight(Font f) {
        return this.gridSizeY() * 18 + 4;
    }

    @Override
    public int getWidth(Font font) {
        return this.gridSizeX() * 18;
    }

    @Override
    public void extractImage(Font font, int x, int y,int w,int h, GuiGraphicsExtractor extractor) {
        int gridSizeX = this.gridSizeX();
        int gridSizeY = this.gridSizeY();
        int slot = 0;
        for (int y1 = 0; y1 < gridSizeY; ++y1) {
            for (int x1 = 0; x1 < gridSizeX; ++x1) {
                int q = x + x1 * 18;
                int r = y + y1 * 18;
                this.extractSlot(q, r, slot++, font, extractor);
            }
        }
    }

    private void extractSlot(int drawX, int drawY, int slot, Font font, GuiGraphicsExtractor extractor) {
        ItemStack itemStack = this.items.get(slot);
        this.blit(extractor, drawX, drawY,  Texture.SLOT);
        extractor.item(itemStack, drawX + 1, drawY + 1, slot);
        extractor.itemDecorations(font,itemStack, drawX + 1, drawY + 1);
        int count = itemStack.getCount();
        if (count > 1) {
            StackSizeRenderer.renderSizeLabel(extractor, font, drawX + 1, drawY + 1, CommonUtils.formatLargeNumber(count));
        }
        if (!selected.isEmpty() && ItemStack.isSameItemSameComponents(selected,itemStack)) {
            extractor.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT_SPRITE, drawX, drawY, 24, 24);
           // AbstractContainerScreen.renderSlotHighlight(extractor, drawX + 1, drawY + 1, 0);
        }
    }

    private static final Identifier SLOT_HIGHLIGHT_FRONT_SPRITE = Identifier.withDefaultNamespace("container/bundle/slot_highlight_front");


    private void blit(GuiGraphicsExtractor guiGraphics, int x, int y, Texture texture) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,texture.sprite, x, y, texture.w, texture.h);
    }


    private int gridSizeX() {
        return 9;
    }

    private int gridSizeY() {
        return items.size() / this.gridSizeX();
    }

    private enum Texture {
        BLOCKED_SLOT(Identifier.withDefaultNamespace("container/bundle/blocked_slot"), 18, 20),
        SLOT(Identifier.withDefaultNamespace("container/bundle/slot"), 18, 20);

        public final Identifier sprite;
        public final int w;
        public final int h;

        Texture(final Identifier sprite, final int w, final int h) {
            this.sprite = sprite;
            this.w = w;
            this.h = h;
        }
    }
}
