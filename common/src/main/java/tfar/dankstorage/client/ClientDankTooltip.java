package tfar.dankstorage.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
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
    public int getHeight() {
        return this.gridSizeY() * 18 + 4;
    }

    @Override
    public int getWidth(Font font) {
        return this.gridSizeX() * 18;
    }

    @Override
    public void renderImage(Font font, int i, int j, GuiGraphics poseStack) {
        int gridSizeX = this.gridSizeX();
        int gridSizeY = this.gridSizeY();
        int slot = 0;
        for (int y1 = 0; y1 < gridSizeY; ++y1) {
            for (int x1 = 0; x1 < gridSizeX; ++x1) {
                int q = i + x1 * 18;
                int r = j + y1 * 18;
                this.renderSlot(q, r, slot++, font, poseStack);
            }
        }
    }

    private void renderSlot(int i, int j, int slot, Font font, GuiGraphics poseStack) {
        ItemStack itemStack = this.items.get(slot);
        this.blit(poseStack, i, j,  Texture.SLOT);
        poseStack.renderItem(itemStack, i + 1, j + 1, slot);
        int count = itemStack.getCount();
        if (count > 1) {
            StackSizeRenderer.renderSizeLabelCustom(poseStack, font, i + 1, j + 1, CommonUtils.formatLargeNumber(count), Services.PLATFORM.getConfig().textSize());
        }
        if (!selected.isEmpty() && ItemStack.isSameItemSameComponents(selected,itemStack)) {
            AbstractContainerScreen.renderSlotHighlight(poseStack, i + 1, j + 1, 0);
        }
    }

    private void blit(GuiGraphics guiGraphics, int x, int y, Texture texture) {
        guiGraphics.blitSprite(texture.sprite, x, y, 0, texture.w, texture.h);
    }


    private int gridSizeX() {
        return 9;
    }

    private int gridSizeY() {
        return items.size() / this.gridSizeX();
    }

    private enum Texture {
        BLOCKED_SLOT(ResourceLocation.withDefaultNamespace("container/bundle/blocked_slot"), 18, 20),
        SLOT(ResourceLocation.withDefaultNamespace("container/bundle/slot"), 18, 20);

        public final ResourceLocation sprite;
        public final int w;
        public final int h;

        Texture(final ResourceLocation sprite, final int w, final int h) {
            this.sprite = sprite;
            this.w = w;
            this.h = h;
        }
    }
}
