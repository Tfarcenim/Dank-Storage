package tfar.dankstorage.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ClientDankTooltip implements ClientTooltipComponent {
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/gui/container/bundle.png");
    private static final int MARGIN_Y = 4;
    private static final int BORDER_WIDTH = 1;
    private static final int TEX_SIZE = 128;
    private static final int SLOT_SIZE_X = 18;
    private static final int SLOT_SIZE_Y = 18;
    private final NonNullList<ItemStack> items;
    private final int selected;

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
    public void renderImage(Font font, int i, int j, PoseStack poseStack, ItemRenderer itemRenderer, int k) {
        int gridSizeX = this.gridSizeX();
        int gridSizeY = this.gridSizeY();
        int slot = 0;
        for (int y1 = 0; y1 < gridSizeY; ++y1) {
            for (int x1 = 0; x1 < gridSizeX; ++x1) {
                int q = i + x1 * 18;
                int r = j + y1 * 18;
                this.renderSlot(q, r, slot++, font, poseStack, itemRenderer, k);
            }
        }
    }

    private void renderSlot(int i, int j, int slot, Font font, PoseStack poseStack, ItemRenderer itemRenderer, int l) {
        if (slot >= this.items.size()) {
            this.blit(poseStack, i, j, l, Texture.BLOCKED_SLOT);
            return;
        }
        ItemStack itemStack = this.items.get(slot);
        this.blit(poseStack, i, j, l, Texture.SLOT);
        itemRenderer.renderAndDecorateItem(itemStack, i + 1, j + 1, slot);
        itemRenderer.renderGuiItemDecorations(font, itemStack, i + 1, j + 1);
        if (slot == selected) {
            AbstractContainerScreen.renderSlotHighlight(poseStack, i + 1, j + 1, l);
        }
    }

    private void blit(PoseStack poseStack, int i, int j, int k, Texture texture) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
        GuiComponent.blit(poseStack, i, j, k, texture.x, texture.y, texture.w, texture.h, 128, 128);
    }

    private int gridSizeX() {
        return 9;
    }

    private int gridSizeY() {
        return items.size() / this.gridSizeX();
    }

    enum Texture {
        SLOT(0, 0, 18, 18),
        BLOCKED_SLOT(0, 40, 18, 18);

        public final int x;
        public final int y;
        public final int w;
        public final int h;

        Texture(int j, int k, int l, int m) {
            this.x = j;
            this.y = k;
            this.w = l;
            this.h = m;
        }
    }
}
