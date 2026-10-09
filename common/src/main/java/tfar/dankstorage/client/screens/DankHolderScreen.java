package tfar.dankstorage.client.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import tfar.dankstorage.client.DualTooltip;
import tfar.dankstorage.menu.DankHolderMenu;
import tfar.dankstorage.menu.DankMenu;
import tfar.dankstorage.network.server.C2SSetFrequencyPacket;
import tfar.dankstorage.utils.TxtColor;

public class DankHolderScreen<S extends DankHolderMenu> extends AbstractContainerScreen<S> {
    public static final int DARK_GRAY = 0xff000000 | 0x404040;
    static final MutableComponent SAVE_C = buildSaveComponent();
    EditBox frequency;

    public DankHolderScreen(S menu, Inventory inventory, Component title, int imageWidth, int imageHeight) {
        super(menu, inventory, title, imageWidth, imageHeight);
    }

    private static MutableComponent buildSaveComponent() {
        return Component.translatable("text.dankstorage.save_frequency_button",
                Component.translatable("text.dankstorage.save_frequency_button.invalid",
                                Component.translatable("text.dankstorage.save_frequency_button.invalidtxt")
                                        .withStyle(ChatFormatting.GRAY))
                        .withStyle(Style.EMPTY.withColor(TxtColor.INVALID.color)),
                Component.translatable("text.dankstorage.save_frequency_button.too_high",
                                Component.translatable("text.dankstorage.save_frequency_button.too_hightxt")
                                        .withStyle(ChatFormatting.GRAY))
                        .withStyle(Style.EMPTY.withColor(TxtColor.TOO_HIGH.color)),
                Component.translatable("text.dankstorage.save_frequency_button.different_tier",
                                Component.translatable("text.dankstorage.save_frequency_button.different_tiertxt")
                                        .withStyle(ChatFormatting.GRAY))
                        .withStyle(Style.EMPTY.withColor(TxtColor.DIFFERENT_TIER.color)),
                Component.translatable("text.dankstorage.save_frequency_button.good",
                                Component.translatable("text.dankstorage.save_frequency_button.goodtxt")
                                        .withStyle(ChatFormatting.GRAY))
                        .withStyle(Style.EMPTY.withColor(TxtColor.GOOD.color))
                , Component.translatable("text.dankstorage.save_frequency_button.locked_frequency",
                                Component.translatable("text.dankstorage.save_frequency_button.locked_frequencytxt")
                                        .withStyle(ChatFormatting.GRAY))
                        .withStyle(Style.EMPTY.withColor(TxtColor.LOCKED.color))
        );
    }

    @Override
    protected void init() {
        super.init();

        Button lock = new Button.Plain(leftPos + 99, topPos + titleLabelY-2, 12, 12,
                Component.literal(""), button -> sendButtonToServer(DankMenu.ButtonAction.LOCK_FREQUENCY), DankScreen.DEFAULT_NARRATION) {
            @Override
            public Component getMessage() {
                return menu.getFreqLock() ? Component.literal("X").withStyle(ChatFormatting.RED) :
                        Component.literal("O");
            }
        };

        Tooltip freqTooltip = new DualTooltip(
                Component.translatable("text.dankstorage.unlock_button"),
                Component.translatable("text.dankstorage.lock_button"),null,this);

        lock.setTooltip(freqTooltip);
        this.addRenderableWidget(lock);

        Tooltip saveTooltip = Tooltip.create(SAVE_C);

        Button save = new Button.Plain(leftPos + imageWidth - 19, topPos + inventoryLabelY-2, 12, 12,
                Component.literal("s"), b -> {
            try {
                int id1 = Integer.parseInt(frequency.getValue());
                C2SSetFrequencyPacket.send(id1, true);
            } catch (NumberFormatException e) {

            }
        }, DankScreen.DEFAULT_NARRATION){};

        save.setTooltip(saveTooltip);

        this.addRenderableWidget(save);

        initEditbox();

    }

    private void initEditbox() {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.frequency = new EditBox(this.font, i + 88, j + inventoryLabelY, 68, 12, Component.translatable("dank"));
        this.frequency.setCanLoseFocus(true);
        this.frequency.setTextShadow(false);
        this.frequency.setTextColor(-1);
        this.frequency.setTextColorUneditable(-1);
        this.frequency.setBordered(false);
        this.frequency.setMaxLength(10);
        this.frequency.setResponder(this::onNameChanged);
        this.frequency.setValue("");
        this.frequency.setTextColor(0xff00ff00);
        this.addRenderableWidget(this.frequency);
    }

    protected void onNameChanged(String string) {
        try {
            int i = Integer.parseInt(string);
            C2SSetFrequencyPacket.send(i, false);
        } catch (NumberFormatException e) {
            C2SSetFrequencyPacket.send(-1, false);
        }
    }

    protected void sendButtonToServer(DankHolderMenu.ButtonAction action) {
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, action.ordinal());
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, DARK_GRAY, false);
        int id = menu.getLinkedFrequency();
        int color = 0xff000000 | 0x008000;
        graphics.text( font,"ID: " + id, 60, inventoryLabelY +1, color,false);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        int color = menu.getTextColor();
        this.frequency.setTextColor(color);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.isEscape() || Minecraft.getInstance().options.keyInventory.matches(event)) {
            this.minecraft.player.closeContainer();
            return true;
        }

        if (this.frequency.keyPressed(event) || this.frequency.canConsumeInput()) {
            return true;
        }
        return super.keyPressed(event);
    }
}
