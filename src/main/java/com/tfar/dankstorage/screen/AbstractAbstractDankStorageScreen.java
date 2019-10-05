package com.tfar.dankstorage.screen;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.tfar.dankstorage.client.RenderItemExtended;
import com.tfar.dankstorage.container.AbstractAbstractDankContainer;
import com.tfar.dankstorage.container.AbstractDankContainer;
import com.tfar.dankstorage.container.DankContainers;
import com.tfar.dankstorage.inventory.DankSlot;
import com.tfar.dankstorage.network.CMessageSort;
import com.tfar.dankstorage.network.DankPacketHandler;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.Iterator;

public abstract class AbstractAbstractDankStorageScreen<T extends AbstractAbstractDankContainer> extends ContainerScreen<T> {

  final ResourceLocation background;//= new ResourceLocation("textures/gui/container/shulker_box.png");

  public AbstractAbstractDankStorageScreen(T container, PlayerInventory playerinventory, ITextComponent component, ResourceLocation background, int rows) {
    super(container, playerinventory, component);
    this.background = background;
    ySize += ((rows - 2) * 18);

    this.ignoreMouseUp = true;
  }

  @Override
  protected void init() {
    super.init();
    this.addButton(new SmallButton(guiLeft + 143, guiTop + 4,26,12,"Sort", b -> DankPacketHandler.INSTANCE.sendToServer(new CMessageSort())));
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    this.font.drawString(this.playerInventory.getDisplayName().getUnformattedComponentText(), 8, this.ySize - 110, 0x404040);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    minecraft.getTextureManager().bindTexture(background);
    if (!(this.container instanceof DankContainers.DankContainer7 || this.container instanceof DankContainers.PortableDankContainer7))
      blit(guiLeft, guiTop, 0, 0, xSize, ySize);
    else blit(guiLeft, guiTop, 0, -5, xSize, ySize, 256, 512);
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    this.renderBackground();
    int i = this.guiLeft;
    int j = this.guiTop;
    this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    GlStateManager.disableRescaleNormal();
    RenderHelper.disableStandardItemLighting();
    GlStateManager.disableLighting();
    GlStateManager.disableDepthTest();

    for (int k = 0; k < this.buttons.size(); ++k) {
      this.buttons.get(k).render(mouseX, mouseY, partialTicks);
    }
   // for (int l = 0; l < this.buttons.size(); ++l) {
      // this.buttons.get(l).drawLabel(this.minecraft, mouseX, mouseY);
   // }

    RenderHelper.enableGUIStandardItemLighting();
    GlStateManager.pushMatrix();
    GlStateManager.translatef((float) i, (float) j, 0.0F);
    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.enableRescaleNormal();
    this.hoveredSlot = null;
    int k = 240;
    int l = 240;
    GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, k, l);
    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

    for (int i1 = 0; i1 < this.container.inventorySlots.size(); ++i1) {
      Slot slot = this.container.inventorySlots.get(i1);

      if (slot.isEnabled()) {
        this.drawSlot(slot);
      }

      if (this.isMouseOverSlot(slot, mouseX, mouseY) && slot.isEnabled()) {
        this.hoveredSlot = slot;
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
        int j1 = slot.xPos;
        int k1 = slot.yPos;
        GlStateManager.colorMask(true, true, true, false);
        this.fillGradient(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.enableLighting();
        GlStateManager.enableDepthTest();
      }
    }

    RenderHelper.disableStandardItemLighting();
    this.drawGuiContainerForegroundLayer(mouseX, mouseY);
    RenderHelper.enableGUIStandardItemLighting();
    net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiContainerEvent.DrawForeground(this, mouseX, mouseY));
    PlayerInventory inventoryplayer = this.minecraft.player.inventory;
    ItemStack itemstack = this.draggedStack.isEmpty() ? inventoryplayer.getItemStack() : this.draggedStack;

    if (!itemstack.isEmpty()) {
      int j2 = 8;
      int k2 = this.draggedStack.isEmpty() ? 8 : 16;
      String s = null;

      if (!this.draggedStack.isEmpty() && this.isRightMouseClick) {
        itemstack = itemstack.copy();
        itemstack.setCount(MathHelper.ceil((float) itemstack.getCount() / 2.0F));
      } else if (this.dragSplitting && this.dragSplittingSlots.size() > 1) {
        itemstack = itemstack.copy();
        itemstack.setCount(this.dragSplittingRemnant);

        if (itemstack.isEmpty()) {
          s = "" + TextFormatting.YELLOW + "0";
        }
      }

      this.drawItemStack(itemstack, mouseX - i - 8, mouseY - j - k2, s);
    }

    if (!this.returningStack.isEmpty()) {
      float f = (float) (System.currentTimeMillis() - this.returningStackTime) / 100.0F;

      if (f >= 1.0F) {
        f = 1.0F;
        this.returningStack = ItemStack.EMPTY;
      }

      int l2 = (int) (this.returningStackDestSlot.xPos - this.touchUpX);
      int i3 = (int) (this.returningStackDestSlot.yPos - this.touchUpY);
      int l1 = (int) (this.touchUpX + (int) ((float) l2 * f));
      int i2 = (int) (this.touchUpY + (int) ((float) i3 * f));
      this.drawItemStack(this.returningStack, l1, i2, null);
    }

    GlStateManager.popMatrix();
    GlStateManager.enableLighting();
    GlStateManager.enableDepthTest();
    RenderHelper.enableStandardItemLighting();

    this.renderHoveredToolTip(mouseX, mouseY);
  }

  //todo: blacklist tooltip?
  @Override
  protected void renderHoveredToolTip(int p_191948_1_, int p_191948_2_) {
    super.renderHoveredToolTip(p_191948_1_, p_191948_2_);
  }

  private void drawItemStack(ItemStack stack, int x, int y, String altText) {
    GlStateManager.translatef(0.0F, 0.0F, 32.0F);
    this.blitOffset = 200;
    this.itemRenderer.zLevel = 200.0F;
    RenderItemExtended.INSTANCE.setZLevel(this.itemRenderer.zLevel);
    net.minecraft.client.gui.FontRenderer fonts = stack.getItem().getFontRenderer(stack);
    if (fonts == null) fonts = font;
    this.itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
    this.itemRenderer.renderItemOverlayIntoGUI(fonts, stack, x, y - (this.draggedStack.isEmpty() ? 0 : 8), altText);
    this.blitOffset = 0;
    this.itemRenderer.zLevel = 0.0F;
    RenderItemExtended.INSTANCE.setZLevel(this.itemRenderer.zLevel);
  }

  private void drawSlot(Slot slotIn) {
    int i = slotIn.xPos;
    int j = slotIn.yPos;
    ItemStack itemstack = slotIn.getStack();
    boolean flag = false;
    boolean flag1 = slotIn == this.clickedSlot && !this.draggedStack.isEmpty() && !this.isRightMouseClick;
    ItemStack itemstack1 = this.minecraft.player.inventory.getItemStack();
    String s = null;

    if (slotIn == this.clickedSlot && !this.draggedStack.isEmpty() && this.isRightMouseClick && !itemstack.isEmpty()) {
      itemstack = itemstack.copy();
      itemstack.setCount(itemstack.getCount() / 2);
    } else if (this.dragSplitting && this.dragSplittingSlots.contains(slotIn) && !itemstack1.isEmpty()) {
      if (this.dragSplittingSlots.size() == 1) {
        return;
      }

      if (AbstractDankContainer.canAddItemToSlot(slotIn, itemstack1, true) && this.container.canDragIntoSlot(slotIn)) {
        itemstack = itemstack1.copy();
        flag = true;
        Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack, slotIn.getStack().isEmpty() ? 0 : slotIn.getStack().getCount());
        int k = slotIn.getItemStackLimit(itemstack);

        if (itemstack.getCount() > k) {
          s = TextFormatting.YELLOW.toString() + k;
          itemstack.setCount(k);
        }
      } else {
        this.dragSplittingSlots.remove(slotIn);
        this.updateDragSplitting();
      }
    }

    this.blitOffset = 100;
    this.itemRenderer.zLevel = 100;

    if (itemstack.isEmpty() && slotIn.isEnabled()) {
      TextureAtlasSprite textureatlassprite = slotIn.getBackgroundSprite();

      if (textureatlassprite != null) {
        GlStateManager.disableLighting();
        this.minecraft.getTextureManager().bindTexture(slotIn.getBackgroundLocation());
        AbstractGui.blit(i, j, blitOffset, 16, 16, textureatlassprite);
        GlStateManager.enableLighting();
        flag1 = true;
      }
    }

    if (!flag1) {
      if (flag) {
        fill(i, j, i + 16, j + 16, 0x80ffffff);
      }

      GlStateManager.enableDepthTest();
      this.itemRenderer.renderItemAndEffectIntoGUI(this.minecraft.player, itemstack, i, j);
      RenderItemExtended.INSTANCE.setZLevel(this.itemRenderer.zLevel);
      if (slotIn instanceof DankSlot) {
        RenderItemExtended.INSTANCE.renderItemOverlayIntoGUI(this.font, itemstack, i, j, s);
      } else {
        this.itemRenderer.renderItemOverlayIntoGUI(this.font, itemstack, i, j, s);
      }
    }

    this.itemRenderer.zLevel = 0.0F;
    this.blitOffset = 0;
    RenderItemExtended.INSTANCE.setZLevel(this.itemRenderer.zLevel);
  }

  private void updateDragSplitting() {
    ItemStack itemstack = this.minecraft.player.inventory.getItemStack();

    if (!itemstack.isEmpty() && this.dragSplitting) {
      if (this.dragSplittingLimit == 2) {
        this.dragSplittingRemnant = itemstack.getMaxStackSize();
      } else {
        this.dragSplittingRemnant = itemstack.getCount();

        for (Slot slot : this.dragSplittingSlots) {
          ItemStack itemstack1 = itemstack.copy();
          ItemStack itemstack2 = slot.getStack();
          int i = itemstack2.isEmpty() ? 0 : itemstack2.getCount();
          Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack1, i);
          //int j = Math.min(itemstack1.getMaxStackSize(), slot.getItemStackLimit(itemstack1));
          int j = slot.getItemStackLimit(itemstack1);

          if (itemstack1.getCount() > j) {
            itemstack1.setCount(j);
          }

          this.dragSplittingRemnant -= itemstack1.getCount() - i;
        }
      }
    }
  }

  private Slot getSlotAtPosition(double x, double y) {
    for (int i = 0; i < this.container.inventorySlots.size(); ++i) {
      Slot slot = this.container.inventorySlots.get(i);

      if (this.isMouseOverSlot(slot, (int) x, (int) y) && slot.isEnabled()) {
        return slot;
      }
    }

    return null;
  }

  public boolean mouseclicked(double mouseX, double mouseY, int mouseButton) {
    Iterator var6 = this.children().iterator();

    IGuiEventListener lvt_7_1_;
    do {
      if (!var6.hasNext()) {
        return false;
      }

      lvt_7_1_ = (IGuiEventListener) var6.next();
    } while (!lvt_7_1_.mouseClicked(mouseX, mouseY, mouseButton));

    this.setFocused(lvt_7_1_);
    if (mouseButton == 0) {
      this.setDragging(true);
    }
    return true;
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

    if (mouseclicked(mouseX, mouseY, mouseButton)) return true;

    InputMappings.Input mouseKey = InputMappings.Type.MOUSE.getOrMakeInput(mouseButton);
    boolean flag = this.minecraft.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseKey);
    Slot slot = this.getSlotAtPosition(mouseX, mouseY);
    long i = System.currentTimeMillis();
    this.doubleClick = this.lastClickSlot == slot && i - this.lastClickTime < 250L && this.lastClickButton == mouseButton;
    this.ignoreMouseUp = false;

    if (mouseButton == 0 || mouseButton == 1 || flag) {
      int j = this.guiLeft;
      int k = this.guiTop;
      boolean flag1 = this.hasClickedOutside(mouseX, mouseY, j, k, mouseButton);
      if (slot != null) flag1 = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
      int l = -1;

      if (slot != null) {
        l = slot.slotNumber;
      }

      if (flag1) {
        l = -999;
      }

      if (this.minecraft.gameSettings.touchscreen && flag1 && this.minecraft.player.inventory.getItemStack().isEmpty()) {
        this.minecraft.displayGuiScreen(null);
        return false;
      }

      if (l != -1) {
        if (this.minecraft.gameSettings.touchscreen) {
          if (slot != null && slot.getHasStack()) {
            this.clickedSlot = slot;
            this.draggedStack = ItemStack.EMPTY;
            this.isRightMouseClick = mouseButton == 1;
          } else {
            this.clickedSlot = null;
          }
        } else if (!this.dragSplitting) {
          if (this.minecraft.player.inventory.getItemStack().isEmpty()) {
            if (this.minecraft.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseKey)) {
              this.handleMouseClick(slot, l, mouseButton, ClickType.CLONE);
            } else {
              boolean flag2 = l != -999 && Screen.hasShiftDown();
              ClickType clicktype = ClickType.PICKUP;

              if (flag2) {
                this.shiftClickedSlot = slot.getHasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
                clicktype = ClickType.QUICK_MOVE;
              } else if (l == -999) {
                clicktype = ClickType.THROW;
              }

              this.handleMouseClick(slot, l, mouseButton, clicktype);
            }

            this.ignoreMouseUp = true;
          } else {
            this.dragSplitting = true;
            this.dragSplittingButton = mouseButton;
            this.dragSplittingSlots.clear();

            if (mouseButton == 0) {
              this.dragSplittingLimit = 0;
            } else if (mouseButton == 1) {
              this.dragSplittingLimit = 1;
            } else if (this.minecraft.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseKey)) {
              this.dragSplittingLimit = 2;
            }
          }
        }
      }
    }

    this.lastClickSlot = slot;
    this.lastClickTime = i;
    this.lastClickButton = mouseButton;
    return true;
  }


  @Override
  public boolean mouseDragged(double mouseX, double mouseY, int clickedMouseButton, double timeSinceLastClick, double param1) {
    Slot slot = this.getSlotAtPosition(mouseX, mouseY);
    ItemStack itemstack = this.minecraft.player.inventory.getItemStack();

    if (this.clickedSlot != null && this.minecraft.gameSettings.touchscreen) {
      if (clickedMouseButton == 0 || clickedMouseButton == 1) {
        if (this.draggedStack.isEmpty()) {
          if (slot != this.clickedSlot && !this.clickedSlot.getStack().isEmpty()) {
            this.draggedStack = this.clickedSlot.getStack().copy();
          }
        } else if (this.draggedStack.getCount() > 1 && slot != null && AbstractDankContainer.canAddItemToSlot(slot, this.draggedStack, false)) {
          long i = System.currentTimeMillis();

          if (this.currentDragTargetSlot == slot) {
            if (i - this.dragItemDropDelay > 500L) {
              this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, ClickType.PICKUP);
              this.handleMouseClick(slot, slot.slotNumber, 1, ClickType.PICKUP);
              this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, ClickType.PICKUP);
              this.dragItemDropDelay = i + 750L;
              this.draggedStack.shrink(1);
            }
          } else {
            this.currentDragTargetSlot = slot;
            this.dragItemDropDelay = i;
          }
        }
      }
    } else if (this.dragSplitting && slot != null && !itemstack.isEmpty() && (itemstack.getCount() > this.dragSplittingSlots.size() || this.dragSplittingLimit == 2) && AbstractDankContainer.canAddItemToSlot(slot, itemstack, true) && slot.isItemValid(itemstack) && this.container.canDragIntoSlot(slot)) {
      this.dragSplittingSlots.add(slot);
      this.updateDragSplitting();
    }
    return true;
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int state) {
    Slot slot = this.getSelectedSlot(mouseX, mouseY);

    if (slot != null && state == 0) {
      //  this.selectedButton.mouseReleased(mouseX, mouseY);
      //  this.selectedButton = null;
    }
    InputMappings.Input mouseKey = InputMappings.Type.MOUSE.getOrMakeInput(state);

    Slot slot1 = this.getSlotAtPosition(mouseX, mouseY);
    int i = this.guiLeft;
    int j = this.guiTop;
    boolean flag = this.hasClickedOutside(mouseX, mouseY, i, j, state);
    if (slot1 != null) flag = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
    int k = -1;

    if (slot1 != null) {
      k = slot1.slotNumber;
    }

    if (flag) {
      k = -999;
    }

    if (this.doubleClick && slot1 != null && state == 0 && this.container.canMergeSlot(ItemStack.EMPTY, slot1)) {
      if (Screen.hasShiftDown()) {
        if (!this.shiftClickedSlot.isEmpty()) {
          for (Slot slot2 : this.container.inventorySlots) {
            if (slot2 != null && slot2.canTakeStack(this.minecraft.player) && slot2.getHasStack() && slot2.isSameInventory(slot1) && AbstractDankContainer.canAddItemToSlot(slot2, this.shiftClickedSlot, true)) {
              this.handleMouseClick(slot2, slot2.slotNumber, state, ClickType.QUICK_MOVE);
            }
          }
        }
      } else {
        this.handleMouseClick(slot1, k, state, ClickType.PICKUP_ALL);
      }

      this.doubleClick = false;
      this.lastClickTime = 0L;
    } else {
      if (this.dragSplitting && this.dragSplittingButton != state) {
        this.dragSplitting = false;
        this.dragSplittingSlots.clear();
        this.ignoreMouseUp = true;
        return true;
      }

      if (this.ignoreMouseUp) {
        this.ignoreMouseUp = false;
        return true;
      }

      if (this.clickedSlot != null && this.minecraft.gameSettings.touchscreen) {
        if (state == 0 || state == 1) {
          if (this.draggedStack.isEmpty() && slot1 != this.clickedSlot) {
            this.draggedStack = this.clickedSlot.getStack();
          }

          boolean flag2 = AbstractDankContainer.canAddItemToSlot(slot1, this.draggedStack, false);

          if (k != -1 && !this.draggedStack.isEmpty() && flag2) {
            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, state, ClickType.PICKUP);
            this.handleMouseClick(slot1, k, 0, ClickType.PICKUP);

            if (this.minecraft.player.inventory.getItemStack().isEmpty()) {
              this.returningStack = ItemStack.EMPTY;
            } else {
              this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, state, ClickType.PICKUP);
              this.touchUpX = (int) (mouseX - i);
              this.touchUpY = (int) (mouseY - j);
              this.returningStackDestSlot = this.clickedSlot;
              this.returningStack = this.draggedStack;
              this.returningStackTime = System.currentTimeMillis();
            }
          } else if (!this.draggedStack.isEmpty()) {
            this.touchUpX = (int) (mouseX - i);
            this.touchUpY = (int) (mouseY - j);
            this.returningStackDestSlot = this.clickedSlot;
            this.returningStack = this.draggedStack;
            this.returningStackTime = System.currentTimeMillis();
          }

          this.draggedStack = ItemStack.EMPTY;
          this.clickedSlot = null;
        }
      } else if (this.dragSplitting && !this.dragSplittingSlots.isEmpty()) {
        this.handleMouseClick(null, -999, Container.getQuickcraftMask(0, this.dragSplittingLimit), ClickType.QUICK_CRAFT);

        for (Slot slot2 : this.dragSplittingSlots) {
          this.handleMouseClick(slot2, slot2.slotNumber, Container.getQuickcraftMask(1, this.dragSplittingLimit), ClickType.QUICK_CRAFT);
        }

        this.handleMouseClick(null, -999, Container.getQuickcraftMask(2, this.dragSplittingLimit), ClickType.QUICK_CRAFT);
      } else if (!this.minecraft.player.inventory.getItemStack().isEmpty()) {
        if (this.minecraft.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseKey)) {
          this.handleMouseClick(slot1, k, state, ClickType.CLONE);
        } else {
          boolean flag1 = k != -999 && Screen.hasShiftDown();

          if (flag1) {
            this.shiftClickedSlot = slot1 != null && slot1.getHasStack() ? slot1.getStack().copy() : ItemStack.EMPTY;
          }

          this.handleMouseClick(slot1, k, state, flag1 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
        }
      }
    }

    if (this.minecraft.player.inventory.getItemStack().isEmpty()) {
      this.lastClickTime = 0L;
    }

    this.dragSplitting = false;
    return true;
  }

  private boolean isMouseOverSlot(Slot slotIn, double mouseX, double mouseY) {
    return this.isPointInRegion(slotIn.xPos, slotIn.yPos, 16, 16, mouseX, mouseY);
  }

  private Slot getSelectedSlot(double p_195360_1_, double p_195360_3_) {
    for (int i = 0; i < this.container.inventorySlots.size(); ++i) {
      Slot slot = this.container.inventorySlots.get(i);
      if (this.isMouseOverSlot(slot, p_195360_1_, p_195360_3_) && slot.isEnabled()) {
        return slot;
      }
    }

    return null;
  }

  @Override
  public boolean keyPressed(int key1, int key2, int r5) {
    return super.keyPressed(key1, key2, r5);
  }

  @Override
  protected boolean func_195363_d(int key1, int key2) {
    return super.func_195363_d(key1, key2);
  }
}