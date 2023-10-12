package tfar.dankstorage.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import tfar.dankstorage.client.BigItemRenderer;
import tfar.dankstorage.client.Client;
import tfar.dankstorage.client.DualTooltip;
import tfar.dankstorage.client.NumberEditBox;
import tfar.dankstorage.client.button.SmallButton;
import tfar.dankstorage.container.AbstractDankMenu;
import tfar.dankstorage.inventory.DankSlot;
import tfar.dankstorage.network.server.*;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.DankInventory;

import javax.tools.Tool;
import java.util.List;

public abstract class AbstractDankStorageScreen<T extends AbstractDankMenu> extends AbstractContainerScreen<T> {

    protected final boolean is7;
    final ResourceLocation background;//= new ResourceLocation("textures/gui/container/shulker_box.png");
    private EditBox frequency;


    public AbstractDankStorageScreen(T container, Inventory playerinventory, Component component, ResourceLocation background) {
        super(container, playerinventory, component);
        this.background = background;
        this.imageHeight = 114 + this.menu.rows * 18;
        this.is7 = this.menu.rows > 6;
        this.inventoryLabelY = this.imageHeight - 94;
    }


    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new SmallButton(leftPos + 143, topPos + 4, 26, 12, Component.literal("Sort"), b -> {
            C2SMessageSort.send();
        }));

        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.frequency = new NumberEditBox(this.font, i + 92, j + inventoryLabelY, 56, 12, Component.translatable("dank"));
        this.frequency.setCanLoseFocus(true);
        this.frequency.setTextColor(-1);
        this.frequency.setTextColorUneditable(-1);
        this.frequency.setBordered(false);
        this.frequency.setMaxLength(10);
        this.frequency.setResponder(this::onNameChanged);
        this.frequency.setValue("");
        this.frequency.setTextColor(0xff00ff00);
        this.addWidget(this.frequency);

        Tooltip freqTooltip = new DualTooltip(
                Component.translatable("text.dankstorage.unlock_button"),
                Component.translatable("text.dankstorage.lock_button"),null,this);

        SmallButton l = new SmallButton(leftPos + 115, topPos + 4, 12, 12,
                Component.literal(""), button -> C2SMessageLockFrequency.send()) {
            @Override
            public Component getMessage() {
                return menu.dankInventory.frequencyLocked() ? Component.literal("X").withStyle(ChatFormatting.RED) :
                        Component.literal("O");
            }
        };

        l.setTooltip(freqTooltip);

        this.addRenderableWidget(l);

        Tooltip saveTooltip = Tooltip.create(buildSaveComponent());

        SmallButton s = new SmallButton(leftPos + 155, j + inventoryLabelY - 2, 12, 12,
                Component.literal("s"), b -> {
            try {
                if (menu.dankInventory.frequencyLocked()) return;
                int id1 = Integer.parseInt(frequency.getValue());
                C2SSetFrequencyPacket.send(id1, true);
            } catch (NumberFormatException e) {

            }
        });

        s.setTooltip(saveTooltip);

        this.addRenderableWidget(s);

        Tooltip compressTooltip = Tooltip.create(Component.translatable("text.dankstorage.compress_button"));

        SmallButton c = new SmallButton(leftPos + 129, topPos + 4, 12, 12,
                Component.literal("C"), button -> C2SMessageCompress.send());
        c.setTooltip(compressTooltip);

        this.addRenderableWidget(c);
    }

    private static MutableComponent buildSaveComponent() {
        return Component.translatable("text.dankstorage.save_frequency_button",
                Component.translatable("text.dankstorage.save_frequency_button.invalid",
                        Component.translatable("text.dankstorage.save_frequency_button.invalidtxt")
                                .withStyle(ChatFormatting.GRAY))
                        .withStyle(Style.EMPTY.withColor(DankInventory.TxtColor.INVALID.color)),
                Component.translatable("text.dankstorage.save_frequency_button.too_high",
                        Component.translatable("text.dankstorage.save_frequency_button.too_hightxt")
                                .withStyle(ChatFormatting.GRAY))
                        .withStyle(Style.EMPTY.withColor(DankInventory.TxtColor.TOO_HIGH.color)),
                Component.translatable("text.dankstorage.save_frequency_button.different_tier",
                        Component.translatable("text.dankstorage.save_frequency_button.different_tiertxt")
                                .withStyle(ChatFormatting.GRAY))
                        .withStyle(Style.EMPTY.withColor(DankInventory.TxtColor.DIFFERENT_TIER.color)),
                Component.translatable("text.dankstorage.save_frequency_button.good",
                        Component.translatable("text.dankstorage.save_frequency_button.goodtxt")
                                .withStyle(ChatFormatting.GRAY))
                        .withStyle(Style.EMPTY.withColor(DankInventory.TxtColor.GOOD.color))
                , Component.translatable("text.dankstorage.save_frequency_button.locked_frequency",
                Component.translatable("text.dankstorage.save_frequency_button.locked_frequencytxt")
                        .withStyle(ChatFormatting.GRAY))
                .withStyle(Style.EMPTY.withColor(DankInventory.TxtColor.LOCKED.color))
        );
    }


    private void onNameChanged(String string) {
        try {
            int i = Integer.parseInt(string);
            C2SSetFrequencyPacket.send(i, false);
        } catch (NumberFormatException e) {
            C2SSetFrequencyPacket.send(-1, false);
        }
    }

    public void renderSlot(PoseStack pPoseStack, Slot pSlot) {
        if (pSlot instanceof DankSlot) {
            itemRenderer = BigItemRenderer.INSTANCE;
        }
        super.renderSlot(pPoseStack, pSlot);
        itemRenderer = minecraft.getItemRenderer();
        int i = pSlot.x;
        int j = pSlot.y;
        if (pSlot.index < menu.dankInventory.getContainerSize() && !pSlot.hasItem() && menu.dankInventory.hasGhostItem(pSlot.index)) {
            itemRenderer.renderAndDecorateFakeItem(pPoseStack,menu.dankInventory.getGhostItem(pSlot.index), i, j);
            RenderSystem.depthFunc(516);
            GuiComponent.fill(pPoseStack, i, j, i + 16, j + 16, 0x40ffffff);
            RenderSystem.depthFunc(515);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || Minecraft.getInstance().options.keyInventory.matches(keyCode, scanCode)) {
            this.minecraft.player.closeContainer();
        }

        //slot locking takes priority over frequency changing
        boolean match = Client.LOCK_SLOT.matches(keyCode, scanCode);
        if (match) {
            if (hoveredSlot instanceof DankSlot) {
                C2SMessageLockSlot.send(hoveredSlot.index);
                return true;
            }
        }

        if (!match && (this.frequency.keyPressed(keyCode, scanCode, modifiers) || this.frequency.canConsumeInput())) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {

        RenderSystem.setShaderTexture(0, background);
        if (is7)
            blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 512);
        else
            blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight);


        for (int i = 0; i < (menu.rows * 9); i++) {
            int j = i % 9;
            int k = i / 9;
            int offsetx = 8;
            int offsety = 18;
            if (this.menu.dankInventory.hasGhostItem(i)) {
                fill(stack, leftPos + j * 18 + offsetx, topPos + k * 18 + offsety,
                        leftPos + j * 18 + offsetx + 16, topPos + k * 18 + offsety + 16, 0xFFFF0000);
            }
        }
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        RenderSystem.disableBlend();

        int color = menu.dankInventory.getTextColor();
        this.frequency.setTextColor(color);
        this.frequency.render(stack, mouseX, mouseY, partialTicks);
        this.renderTooltip(stack, mouseX, mouseY);
    }

    @Override
    public List<Component> getTooltipFromItem(ItemStack itemStack) {
        List<Component> tooltipFromItem = super.getTooltipFromItem(itemStack);
        appendDankInfo(tooltipFromItem, itemStack);
        return tooltipFromItem;
    }

    public void appendDankInfo(List<Component> tooltip, ItemStack stack) {
        if (stack.is(Utils.BLACKLISTED_STORAGE)) {
            Component component = Component.translatable("text.dankstorage.blacklisted_storage").withStyle(ChatFormatting.DARK_RED);
            tooltip.add(component);
        }
        if (stack.is(Utils.BLACKLISTED_USAGE)) {
            Component component = Component.translatable("text.dankstorage.blacklisted_usage").
                    withStyle(ChatFormatting.DARK_RED);
            tooltip.add(component);
        }
        if (hoveredSlot instanceof DankSlot) {
            Component component1 = Component.translatable("text.dankstorage.lock",
                    Client.LOCK_SLOT.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY);
            tooltip.add(component1);
            if (stack.getCount() >= 1000) {
                Component component2 = Component.translatable(
                        "text.dankstorage.exact", Component.literal(Integer.toString(stack.getCount())).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY);
                tooltip.add(component2);
            }
        }
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int i, int j) {
        super.renderLabels(poseStack, i, j);
        int id = menu.dankInventory.getFrequency();//menu.dankInventory.get(menu.rows * 9);
        int color = 0x008000;
        this.font.draw(poseStack, "ID: " + id, 62, inventoryLabelY, color);
    }
}