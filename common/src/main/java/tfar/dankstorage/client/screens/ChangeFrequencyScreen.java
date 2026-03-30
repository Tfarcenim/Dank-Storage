package tfar.dankstorage.client.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import tfar.dankstorage.client.DualTooltip2;
import tfar.dankstorage.client.NumberEditBox;
import tfar.dankstorage.menu.DankMenu;
import tfar.dankstorage.menu.ChangeFrequencyMenu;
import tfar.dankstorage.network.server.C2SSetFrequencyPacket;
import tfar.dankstorage.utils.CommonUtils;

public class ChangeFrequencyScreen extends AbstractContainerScreen<ChangeFrequencyMenu> {

    public static final Identifier DEMO_BACKGROUND_LOCATION = Identifier.withDefaultNamespace("textures/gui/demo_background.png");
    EditBox frequency;


    public ChangeFrequencyScreen(ChangeFrequencyMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2,236,166);
    }

    protected void initEditbox() {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.frequency = new NumberEditBox(this.font, i + 84, j + inventoryLabelY, 64, 12, CommonUtils.translatable("dank"));
        this.frequency.setCanLoseFocus(true);
        this.frequency.setTextColor(-1);
        this.frequency.setTextColorUneditable(-1);
        //this.frequency.setBordered(false);
        this.frequency.setMaxLength(10);
        this.frequency.setResponder(this::onNameChanged);
        this.frequency.setValue("");
        this.frequency.setTextColor(0xff00ff00);
        this.addWidget(this.frequency);
    }

  //  protected CycleButton<PickupMode> modeCycleButton;


    @Override
    protected void init() {
        super.init();

        int j = (this.height - this.imageHeight) / 2;

       /* modeCycleButton = CycleButton.<PickupMode>builder(pickupMode -> Component.literal("P")
                        .withStyle(Style.EMPTY.withColor(pickupMode.getColor())))
                .withValues(PickupMode.VALUES)
                .withTooltip(mode -> Tooltip.create(mode.translate()))
                .withInitialValue(PickupMode.none)
                .displayOnlyValue()
                .create(leftPos + 101, topPos + 4, 12, 12, Component.empty(), (pickupModeCycleButton, pickupMode) -> sendButtonToServer(DankMenu.ButtonAction.TOGGLE_PICKUP));

        addRenderableWidget(modeCycleButton);*/

        Button l = new Button.Plain(leftPos + 170, j + inventoryLabelY, 12, 12,
                Component.literal(""), button -> sendButtonToServer(DankMenu.ButtonAction.LOCK_FREQUENCY),DankStorageScreen.DEFAULT_NARRATION) {
            @Override
            public Component getMessage() {
                return menu.getFreqLock() ? Component.literal("X").withStyle(ChatFormatting.RED) :
                        Component.literal("O");
            }
        };

        Tooltip freqTooltip = new DualTooltip2(
                Component.translatable("text.dankstorage.unlock_button"),
                Component.translatable("text.dankstorage.lock_button"),null,this);

        l.setTooltip(freqTooltip);

        this.addRenderableWidget(l);


        Tooltip saveTooltip = Tooltip.create(DankStorageScreen.SAVE_C);

        Button s = new Button.Plain(leftPos + 155, j + inventoryLabelY, 12, 12,
                Component.literal("s"), b -> {
            try {
                int id1 = Integer.parseInt(frequency.getValue());
                C2SSetFrequencyPacket.send(id1, true);
            } catch (NumberFormatException e) {

            }
        },DankStorageScreen.DEFAULT_NARRATION){};

        s.setTooltip(saveTooltip);

        this.addRenderableWidget(s);




        initEditbox();
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractContents(graphics, mouseX, mouseY, partialTicks);
        int color = menu.getTextColor();
        this.frequency.setTextColor(color);
    }

    private void onNameChanged(String string) {
        try {
            int i = Integer.parseInt(string);
            C2SSetFrequencyPacket.send(i, false);
        } catch (NumberFormatException e) {
            C2SSetFrequencyPacket.send(-1, false);
        }
    }

    private void sendButtonToServer(DankMenu.ButtonAction action) {
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, action.ordinal());
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        int id = menu.getFrequency();
        int color = 0x008000;
        int txtWidth = font.width("ID: " + id);
        graphics.text( font,"ID: " + id, 62 - txtWidth, inventoryLabelY +1, color,false);
        MutableComponent warning = Component.translatable("text.dankstorage.tier_mismatch");
        graphics.textWithWordWrap(font,warning,5,inventoryLabelY+18,260,0x404040);
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


    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a) {
        super.extractBackground(guiGraphics, mouseX, mouseY, a);
        int $$1 = (this.width - 248) / 2;
        int $$2 = (this.height - 166) / 2;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED,DEMO_BACKGROUND_LOCATION, $$1, $$2, 0, 0, 248, 166,256,256);
    }
}
