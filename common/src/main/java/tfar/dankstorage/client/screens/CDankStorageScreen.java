package tfar.dankstorage.client.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.client.NumberEditBox;
import tfar.dankstorage.menu.CAbstractDankMenu;
import tfar.dankstorage.platform.Services;
import tfar.dankstorage.utils.CommonUtils;

public abstract class CDankStorageScreen< T extends CAbstractDankMenu> extends AbstractContainerScreen<T> {

    static final ResourceLocation background1 = new ResourceLocation(DankStorage.MODID,
            "textures/container/gui/dank1.png");
    static final ResourceLocation background2 = new ResourceLocation(DankStorage.MODID,
            "textures/container/gui/dank2.png");
    static final ResourceLocation background3 = new ResourceLocation(DankStorage.MODID,
            "textures/container/gui/dank3.png");
    static final ResourceLocation background4 = new ResourceLocation(DankStorage.MODID,
            "textures/container/gui/dank4.png");
    static final ResourceLocation background5 = new ResourceLocation(DankStorage.MODID,
            "textures/container/gui/dank5.png");
    static final ResourceLocation background6 = new ResourceLocation("textures/gui/container/generic_54.png");
    static final ResourceLocation background7 = new ResourceLocation(DankStorage.MODID,
            "textures/container/gui/dank7.png");

    final ResourceLocation background;
    EditBox frequency;
    protected final boolean is7;

    public CDankStorageScreen(T $$0, Inventory $$1, Component $$2, ResourceLocation background) {
        super($$0, $$1, $$2);
        this.imageHeight = 114 + this.menu.rows * 18;
        this.inventoryLabelY = this.imageHeight - 94;

        this.background = background;
        this.is7 = this.menu.rows > 6;
    }

    protected void initEditbox() {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.frequency = new NumberEditBox(this.font, i + 92, j + inventoryLabelY, 56, 12, CommonUtils.translatable("dank"));
        this.frequency.setCanLoseFocus(true);
        this.frequency.setTextColor(-1);
        this.frequency.setTextColorUneditable(-1);
        this.frequency.setBordered(false);
        this.frequency.setMaxLength(10);
        this.frequency.setResponder(this::onNameChanged);
        this.frequency.setValue("");
        this.frequency.setTextColor(0xff00ff00);
        this.addWidget(this.frequency);
    }

    private void onNameChanged(String string) {
        try {
            int i = Integer.parseInt(string);
            Services.PLATFORM.sendFrequencyPacket(i, false);
        } catch (NumberFormatException e) {
            Services.PLATFORM.sendFrequencyPacket(-1, false);
        }
    }

    @Override
    protected void renderBg(GuiGraphics stack, float partialTicks, int mouseX, int mouseY) {
        if (is7)
            stack.blit(background, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 512);
        else
            stack.blit(background, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    static final MutableComponent STORAGE_TXT = CommonUtils.translatable("text.dankstorage.blacklisted_storage").withStyle(ChatFormatting.DARK_RED);
    static final MutableComponent USAGE_TXT = CommonUtils.translatable("text.dankstorage.blacklisted_usage").withStyle(ChatFormatting.DARK_RED);

}
