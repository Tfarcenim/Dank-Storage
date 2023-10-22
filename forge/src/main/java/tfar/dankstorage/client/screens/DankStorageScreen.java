package tfar.dankstorage.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import tfar.dankstorage.ModTags;
import tfar.dankstorage.client.*;
import tfar.dankstorage.client.button.SmallButton;
import tfar.dankstorage.container.AbstractDankMenu;
import tfar.dankstorage.inventory.DankSlot;
import tfar.dankstorage.network.server.C2SButtonPacket;
import tfar.dankstorage.network.server.C2SLockSlotPacket;
import tfar.dankstorage.network.server.C2SSetFrequencyPacket;
import tfar.dankstorage.utils.CommonUtils;
import tfar.dankstorage.utils.Utils;

import java.util.List;

public class DankStorageScreen<T extends AbstractDankMenu> extends CDankStorageScreen<T> {

    public DankStorageScreen(T container, Inventory playerinventory, Component component, ResourceLocation background) {
        super(container, playerinventory, component,background);
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

        Tooltip saveTooltip = Tooltip.create(SAVE_C);

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
                pGuiGraphics.renderItemDecorations(this.font, itemstack, i, j, CommonUtils.formatLargeNumber(itemstack.getCount()));
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
        super.renderBg(stack, partialTicks, mouseX, mouseY);
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


    public void appendDankInfo(List<Component> tooltip, ItemStack stack) {
        if (stack.is(ModTags.BLACKLISTED_STORAGE)) tooltip.add(STORAGE_TXT);
        if (stack.is(ModTags.BLACKLISTED_USAGE)) tooltip.add(USAGE_TXT);
        if (hoveredSlot instanceof DankSlot) {
            Component component1 = Utils.translatable("text.dankstorage.lock",
                    DankKeybinds.LOCK_SLOT.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY);
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