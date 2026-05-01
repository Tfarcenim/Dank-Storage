package tfar.dankstorage.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
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
        boolean highlight = !selected.isEmpty() && ItemStack.isSameItemSameComponents(selected,itemStack);
        if (highlight) {
            extractor.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK_SPRITE, drawX, drawY, 18, 18);
        } else {
            extractor.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_BACKGROUND_SPRITE, drawX, drawY, 18, 18);
        }
        extractor.item(itemStack, drawX + 1, drawY + 1, slot);
        int count = itemStack.getCount();
        if (count > 1) {
            StackSizeRenderer.renderSizeLabel(extractor, font, drawX + 1, drawY + 1, CommonUtils.formatLargeNumber(count));
        }
    }

    private static final Identifier SLOT_HIGHLIGHT_BACK_SPRITE = Identifier.withDefaultNamespace("container/bundle/slot_highlight_back");


    private static final Identifier SLOT_BACKGROUND_SPRITE = Identifier.withDefaultNamespace("container/bundle/slot_background");

    private int gridSizeX() {
        return 9;
    }

    private int gridSizeY() {
        return items.size() / this.gridSizeX();
    }

}
