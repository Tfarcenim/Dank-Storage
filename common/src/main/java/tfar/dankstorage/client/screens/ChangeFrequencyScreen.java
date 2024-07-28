package tfar.dankstorage.client.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;
import tfar.dankstorage.client.DualTooltip2;
import tfar.dankstorage.client.NumberEditBox;
import tfar.dankstorage.client.button.SmallButton;
import tfar.dankstorage.menu.AbstractDankMenu;
import tfar.dankstorage.menu.ChangeFrequencyMenu;
import tfar.dankstorage.network.server.C2SSetFrequencyPacket;
import tfar.dankstorage.utils.CommonUtils;

public class ChangeFrequencyScreen extends AbstractContainerScreen<ChangeFrequencyMenu> {

    private static final ResourceLocation DEMO_BACKGROUND_LOCATION = new ResourceLocation("textures/gui/demo_background.png");
    EditBox frequency;


    public ChangeFrequencyScreen(ChangeFrequencyMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
        imageWidth = 236;
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
                .create(leftPos + 101, topPos + 4, 12, 12, Component.empty(), (pickupModeCycleButton, pickupMode) -> sendButtonToServer(AbstractDankMenu.ButtonAction.TOGGLE_PICKUP));

        addRenderableWidget(modeCycleButton);*/

        SmallButton l = new SmallButton(leftPos + 170, j + inventoryLabelY, 12, 12,
                Component.literal(""), button -> sendButtonToServer(AbstractDankMenu.ButtonAction.LOCK_FREQUENCY)) {
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


        Tooltip saveTooltip = Tooltip.create(CDankStorageScreen.SAVE_C);

        SmallButton s = new SmallButton(leftPos + 155, j + inventoryLabelY, 12, 12,
                Component.literal("s"), b -> {
            try {
                int id1 = Integer.parseInt(frequency.getValue());
                C2SSetFrequencyPacket.send(id1, true);
            } catch (NumberFormatException e) {

            }
        });

        s.setTooltip(saveTooltip);

        this.addRenderableWidget(s);


        initEditbox();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        int color = menu.getTextColor();
        this.frequency.setTextColor(color);
        this.frequency.render(graphics, mouseX, mouseY, partialTicks);
    }

    private void onNameChanged(String string) {
        try {
            int i = Integer.parseInt(string);
            C2SSetFrequencyPacket.send(i, false);
        } catch (NumberFormatException e) {
            C2SSetFrequencyPacket.send(-1, false);
        }
    }

    private void sendButtonToServer(AbstractDankMenu.ButtonAction action) {
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, action.ordinal());
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        int id = menu.getFrequency();
        int color = 0x008000;
        int txtWidth = font.width("ID: " + id);
        graphics.drawString( font,"ID: " + id, 62 - txtWidth, inventoryLabelY +1, color,false);
        MutableComponent warning = Component.literal("Current Dank cannot open backing inventory, upgrade or switch to supported frequency");
        graphics.drawWordWrap(font,warning,5,inventoryLabelY+18,260,0x404040);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || Minecraft.getInstance().options.keyInventory.matches(keyCode, scanCode)) {
            this.minecraft.player.closeContainer();
        }

        if (this.frequency.keyPressed(keyCode, scanCode, modifiers) || this.frequency.canConsumeInput()) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }


    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        super.renderBackground(guiGraphics);
        int $$1 = (this.width - 248) / 2;
        int $$2 = (this.height - 166) / 2;
        guiGraphics.blit(DEMO_BACKGROUND_LOCATION, $$1, $$2, 0, 0, 248, 166);
    }
}
