package tfar.dankstorage.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.ModTags;
import tfar.dankstorage.client.Client;
import tfar.dankstorage.client.DualTooltip;
import tfar.dankstorage.client.NumberEditBox;
import tfar.dankstorage.client.button.SmallButton;
import tfar.dankstorage.container.AbstractDankMenu;
import tfar.dankstorage.inventory.DankSlot;
import tfar.dankstorage.network.server.C2SButtonPacket;
import tfar.dankstorage.network.server.C2SMessageLockSlotPacket;
import tfar.dankstorage.network.server.C2SSetFrequencyPacket;
import tfar.dankstorage.utils.PickupMode;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.DankInventory;

import java.util.List;

public class DankStorageScreen<T extends AbstractDankMenu> extends AbstractContainerScreen<T> {

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
    protected final boolean is7;
    final ResourceLocation background;//= new ResourceLocation("textures/gui/container/shulker_box.png");
    private EditBox frequency;


    public DankStorageScreen(T container, Inventory playerinventory, Component component, ResourceLocation background) {
        super(container, playerinventory, component);
        this.background = background;
        this.imageHeight = 114 + this.menu.rows * 18;
        this.is7 = this.menu.rows > 6;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    public static <T extends AbstractDankMenu> DankStorageScreen<T> t1(T container, Inventory playerinventory, Component component) {
        return new DankStorageScreen<>(container, playerinventory, component, background1);
    }

    public static <T extends AbstractDankMenu> DankStorageScreen<T> t2(T container, Inventory playerinventory, Component component) {
        return new DankStorageScreen<>(container, playerinventory, component, background2);
    }

    public static <T extends AbstractDankMenu> DankStorageScreen<T> t3(T container, Inventory playerinventory, Component component) {
        return new DankStorageScreen<>(container, playerinventory, component, background3);
    }

    public static <T extends AbstractDankMenu> DankStorageScreen<T> t4(T container, Inventory playerinventory, Component component) {
        return new DankStorageScreen<>(container, playerinventory, component, background4);
    }

    public static <T extends AbstractDankMenu> DankStorageScreen<T> t5(T container, Inventory playerinventory, Component component) {
        return new DankStorageScreen<>(container, playerinventory, component, background5);
    }

    public static <T extends AbstractDankMenu> DankStorageScreen<T> t6(T container, Inventory playerinventory, Component component) {
        return new DankStorageScreen<>(container, playerinventory, component, background6);
    }

    public static <T extends AbstractDankMenu> DankStorageScreen<T> t7(T container, Inventory playerinventory, Component component) {
        return new DankStorageScreen<>(container, playerinventory, component, background7);
    }

    @Override
    protected void init() {
        super.init();

        int j = (this.height - this.imageHeight) / 2;


        this.addRenderableWidget(new SmallButton(leftPos + 143, topPos + 4, 26, 12, Component.literal("Sort"), b -> {
            C2SButtonPacket.send(C2SButtonPacket.Action.SORT);
        }));

        Tooltip freqTooltip = new DualTooltip(
                Component.translatable("text.dankstorage.unlock_button"),
                Component.translatable("text.dankstorage.lock_button"),null,this);

        SmallButton l = new SmallButton(leftPos + 115, topPos + 4, 12, 12,
                Component.literal(""), button -> C2SButtonPacket.send(C2SButtonPacket.Action.LOCK_FREQUENCY)) {
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
                Component.literal("C"), button -> C2SButtonPacket.send(C2SButtonPacket.Action.COMPRESS));
        c.setTooltip(compressTooltip);

        this.addRenderableWidget(c);
        initEditbox();
    }

    protected void initEditbox() {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.frequency = new NumberEditBox(this.font, i + 92, j + inventoryLabelY, 56, 12, Utils.translatable("dank"));
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

    private static final MutableComponent SAVE_C = buildSaveComponent();
    private static final MutableComponent PICKUP_C = buildPickupComponent();

    private static MutableComponent buildSaveComponent() {
        return Utils.translatable("text.dankstorage.save_frequency_button",
                Utils.translatable("text.dankstorage.save_frequency_button.invalid",
                                Utils.translatable("text.dankstorage.save_frequency_button.invalidtxt")
                                        .withStyle(ChatFormatting.GRAY))
                        .withStyle(Style.EMPTY.withColor(DankInventory.TxtColor.INVALID.color)),
                Utils.translatable("text.dankstorage.save_frequency_button.too_high",
                                Utils.translatable("text.dankstorage.save_frequency_button.too_hightxt")
                                        .withStyle(ChatFormatting.GRAY))
                        .withStyle(Style.EMPTY.withColor(DankInventory.TxtColor.TOO_HIGH.color)),
                Utils.translatable("text.dankstorage.save_frequency_button.different_tier",
                                Utils.translatable("text.dankstorage.save_frequency_button.different_tiertxt")
                                        .withStyle(ChatFormatting.GRAY))
                        .withStyle(Style.EMPTY.withColor(DankInventory.TxtColor.DIFFERENT_TIER.color)),
                Utils.translatable("text.dankstorage.save_frequency_button.good",
                                Utils.translatable("text.dankstorage.save_frequency_button.goodtxt")
                                        .withStyle(ChatFormatting.GRAY))
                        .withStyle(Style.EMPTY.withColor(DankInventory.TxtColor.GOOD.color))
                , Utils.translatable("text.dankstorage.save_frequency_button.locked_frequency",
                                Utils.translatable("text.dankstorage.save_frequency_button.locked_frequencytxt")
                                        .withStyle(ChatFormatting.GRAY))
                        .withStyle(Style.EMPTY.withColor(DankInventory.TxtColor.LOCKED.color))
        );
    }

    private static MutableComponent buildPickupComponent() {
        return Utils.translatable("text.dankstorage.pickup_button",
                Utils.translatable("text.dankstorage.pickup_button.none",
                                Utils.translatable("text.dankstorage.pickup_button.nonetxt")
                                        .withStyle(ChatFormatting.GRAY))
                        .withStyle(Style.EMPTY.withColor(PickupMode.none.getColor())),
                Utils.translatable("text.dankstorage.pickup_button.all",
                                Utils.translatable("text.dankstorage.pickup_button.alltxt")
                                        .withStyle(ChatFormatting.GRAY))
                        .withStyle(Style.EMPTY.withColor(PickupMode.pickup_all.getColor())),
                Utils.translatable("text.dankstorage.pickup_button.filtered",
                                Utils.translatable("text.dankstorage.pickup_button.filteredtxt")
                                        .withStyle(ChatFormatting.GRAY))
                        .withStyle(Style.EMPTY.withColor(PickupMode.filtered_pickup.getColor())),
                Utils.translatable("text.dankstorage.pickup_button.void",
                                Utils.translatable("text.dankstorage.pickup_button.voidtxt")
                                        .withStyle(ChatFormatting.GRAY))
                        .withStyle(Style.EMPTY.withColor(PickupMode.void_pickup.getColor()))
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

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || Minecraft.getInstance().options.keyInventory.matches(keyCode, scanCode)) {
            this.minecraft.player.closeContainer();
        }

        //slot locking takes priority over frequency changing
        boolean match = Client.LOCK_SLOT.matches(keyCode, scanCode);
        if (match) {
            if (hoveredSlot instanceof DankSlot) {
                C2SMessageLockSlotPacket.send(hoveredSlot.index);
                return true;
            }
        }

        if (!match && (this.frequency.keyPressed(keyCode, scanCode, modifiers) || this.frequency.canConsumeInput())) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void renderSlot(GuiGraphics pGuiGraphics, Slot pSlot) {

        if (!(pSlot instanceof DankSlot)) {
            super.renderSlot(pGuiGraphics, pSlot);
        } else {
            int i = pSlot.x;
            int j = pSlot.y;
            ItemStack itemstack = pSlot.getItem();
            boolean flag = false;
            boolean flag1 = pSlot == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
            ItemStack itemstack1 = this.menu.getCarried();
            String s = null;
            if (pSlot == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !itemstack.isEmpty()) {
                itemstack = itemstack.copyWithCount(itemstack.getCount() / 2);
            } else if (this.isQuickCrafting && this.quickCraftSlots.contains(pSlot) && !itemstack1.isEmpty()) {
                if (this.quickCraftSlots.size() == 1) {
                    return;
                }

                if (AbstractContainerMenu.canItemQuickReplace(pSlot, itemstack1, true) && this.menu.canDragTo(pSlot)) {
                    flag = true;
                    int k = Math.min(itemstack1.getMaxStackSize(), pSlot.getMaxStackSize(itemstack1));
                    int l = pSlot.getItem().isEmpty() ? 0 : pSlot.getItem().getCount();
                    int i1 = AbstractContainerMenu.getQuickCraftPlaceCount(this.quickCraftSlots, this.quickCraftingType, itemstack1) + l;
                    if (i1 > k) {
                        i1 = k;
                        s = ChatFormatting.YELLOW.toString() + k;
                    }

                    itemstack = itemstack1.copyWithCount(i1);
                } else {
                    this.quickCraftSlots.remove(pSlot);
                    this.recalculateQuickCraftRemaining();
                }
            }

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
            if (itemstack.isEmpty() && pSlot.isActive()) {
                Pair<ResourceLocation, ResourceLocation> pair = pSlot.getNoItemIcon();
                if (pair != null) {
                    TextureAtlasSprite textureatlassprite = this.minecraft.getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
                    pGuiGraphics.blit(i, j, 0, 16, 16, textureatlassprite);
                    flag1 = true;
                }
            }

            if (!flag1) {
                if (flag) {
                    pGuiGraphics.fill(i, j, i + 16, j + 16, -2130706433);
                }

                pGuiGraphics.renderItem(itemstack, i, j, pSlot.x + pSlot.y * this.imageWidth);
                pGuiGraphics.renderItemDecorations(this.font, itemstack, i, j, Client.getStringFromInt(itemstack.getCount()));
            }

            pGuiGraphics.pose().popPose();

            int i1 = pSlot.x;
            int j1 = pSlot.y;
            if (!pSlot.hasItem() && pSlot.index < menu.dankInventory.getSlots() && menu.dankInventory.hasGhostItem(pSlot.index)) {
                pGuiGraphics.renderFakeItem(menu.dankInventory.getGhostItem(pSlot.index), i1, j1);
                RenderSystem.depthFunc(516);
                pGuiGraphics.fill(i1, j1, i1 + 16, j1 + 16, 0x40ffffff);
                RenderSystem.depthFunc(515);
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics stack, float partialTicks, int mouseX, int mouseY) {
        if (is7)
            stack.blit(background, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 512);
        else
            stack.blit(background, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        renderLockedSlots(stack);
    }

    protected void renderLockedSlots(GuiGraphics stack) {
        for (int i = 0; i < menu.rows * 9; i++) {
            int j = i % 9;
            int k = i / 9;
            int offsetx = 8;
            int offsety = 18;
            if (this.menu.dankInventory.hasGhostItem(i)) {
                stack.fill(leftPos + j * 18 + offsetx, topPos + k * 18 + offsety,
                        leftPos + j * 18 + offsetx + 16, topPos + k * 18 + offsety + 16, 0xFFFF0000);
            }
        }
    }

    @Override
    public void render(GuiGraphics stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        int color = menu.dankInventory.getTextColor();
        this.frequency.setTextColor(color);
        this.frequency.render(stack, mouseX, mouseY, partialTicks);
        this.renderTooltip(stack, mouseX, mouseY);
    }

    @Override
    public List<Component> getTooltipFromContainerItem(ItemStack itemStack) {
        List<Component> tooltipFromItem = super.getTooltipFromContainerItem(itemStack);
        appendDankInfo(tooltipFromItem, itemStack);
        return tooltipFromItem;
    }

    private static final MutableComponent STORAGE_TXT = Utils.translatable("text.dankstorage.blacklisted_storage").withStyle(ChatFormatting.DARK_RED);
    private static final MutableComponent USAGE_TXT = Utils.translatable("text.dankstorage.blacklisted_usage").withStyle(ChatFormatting.DARK_RED);
    public void appendDankInfo(List<Component> tooltip, ItemStack stack) {
        if (stack.is(ModTags.BLACKLISTED_STORAGE)) tooltip.add(STORAGE_TXT);
        if (stack.is(ModTags.BLACKLISTED_USAGE)) tooltip.add(USAGE_TXT);
        if (hoveredSlot instanceof DankSlot) {
            Component component1 = Utils.translatable("text.dankstorage.lock",
                    Client.LOCK_SLOT.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY);
            tooltip.add(component1);
            if (stack.getCount() >= 1000) {
                Component component2 = Utils.translatable(
                        "text.dankstorage.exact", Utils.literal(Integer.toString(stack.getCount())).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY);
                tooltip.add(component2);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics poseStack, int i, int j) {
        RenderSystem.disableBlend();
        super.renderLabels(poseStack, i, j);
        int id = menu.dankInventory.getFrequency();//menu.dankInventory.get(menu.rows * 9);
        int color = 0x008000;
        poseStack.drawString( font,"ID: " + id, 62, inventoryLabelY, color,false);
    }
}