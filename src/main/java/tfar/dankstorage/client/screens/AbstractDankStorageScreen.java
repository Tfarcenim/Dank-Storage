package tfar.dankstorage.client.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import tfar.dankstorage.client.BigItemRenderer;
import tfar.dankstorage.client.button.SmallButton;
import tfar.dankstorage.container.AbstractDankContainer;
import tfar.dankstorage.container.DockContainer;
import tfar.dankstorage.container.PortableDankContainer;
import tfar.dankstorage.inventory.DankSlot;
import tfar.dankstorage.network.C2SMessageLockSlot;
import tfar.dankstorage.network.CMessageSort;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.Utils;

import java.util.List;

public abstract class AbstractDankStorageScreen<T extends AbstractDankContainer> extends ContainerScreen<T> {

	final ResourceLocation background;//= new ResourceLocation("textures/gui/container/shulker_box.png");

	protected final boolean is7;

	protected final boolean isPortable;

	public AbstractDankStorageScreen(T container, PlayerInventory playerinventory, ITextComponent component, ResourceLocation background) {
		super(container, playerinventory, component);
		this.background = background;
		this.ySize = 114 + this.container.rows * 18;
		this.is7 = this.container.rows > 6;
		this.playerInventoryTitleY = this.ySize - 94;
		this.isPortable = container instanceof PortableDankContainer;
	}

	@Override
	protected void init() {
		super.init();
		this.addButton(new SmallButton(guiLeft + 143, guiTop + 4, 26, 12, new StringTextComponent("Sort"), b -> {
			DankPacketHandler.INSTANCE.sendToServer(new CMessageSort());
			Utils.sort(Minecraft.getInstance().player);
		}));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {

		minecraft.getTextureManager().bindTexture(background);
		if (is7)
			blit(stack, guiLeft, guiTop, 0, 0, xSize, ySize, 256, 512);
		else
			blit(stack, guiLeft, guiTop, 0, 0, xSize, ySize);


		for (int i = 0; i < (container.rows * 9); i++) {
			int j = i % 9;
			int k = i / 9;
			int offsetx = 8;
			int offsety = 18;
			if (container.propertyDelegate.get(i) == 1)
				fill(stack, guiLeft + j * 18 + offsetx, guiTop + k * 18 + offsety,
								guiLeft + j * 18 + offsetx + 16, guiTop + k * 18 + offsety + 16, 0xFFFF0000);

			if (isPortable && container.propertyDelegate.get(container.rows * 9 + 1) == i ) {

				fill(stack, guiLeft + j * 18 + offsetx, guiTop + k * 18 + offsety,
						guiLeft + j * 18 + offsetx + 16, guiTop + k * 18 + offsety + 16, 0xFF00FF00);
			}
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, partialTicks);
		this.renderHoveredTooltip(matrices, mouseX, mouseY);	}

	@Override
	public List<ITextComponent> getTooltipFromItem(ItemStack itemStack) {
		List<ITextComponent> tooltip = super.getTooltipFromItem(itemStack);
		appendDankInfo(tooltip,itemStack);
		return tooltip;
	}

	public void appendDankInfo(List<ITextComponent> tooltip,ItemStack stack) {
		if (stack.getItem().isIn(Utils.BLACKLISTED_STORAGE)) {
			ITextComponent component1 = new TranslationTextComponent("text.dankstorage.blacklisted_storage").mergeStyle(TextFormatting.DARK_RED);
			tooltip.add(component1);
		}
		if (stack.getItem().isIn(Utils.BLACKLISTED_USAGE)) {
			ITextComponent component1 = new TranslationTextComponent("text.dankstorage.blacklisted_usage").
							mergeStyle(TextFormatting.DARK_RED);
			tooltip.add(component1);
		}
		if (hoveredSlot instanceof DankSlot) {
			ITextComponent component2 = new TranslationTextComponent("text.dankstorage.lock",
							new StringTextComponent("ctrl").mergeStyle(TextFormatting.YELLOW)).mergeStyle(TextFormatting.GRAY);
			tooltip.add(component2);
			if (stack.getCount() >= 1000) {
				ITextComponent component3 = new TranslationTextComponent(
								"text.dankstorage.exact", new StringTextComponent(
										Integer.toString(stack.getCount())).mergeStyle(TextFormatting.AQUA)).mergeStyle(TextFormatting.GRAY);
				tooltip.add(component3);
			}
		}
	}

	@Override
	public void moveItems(MatrixStack matrices, Slot slotIn) {
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

			if (DockContainer.canAddItemToSlot(slotIn, itemstack1, true) && this.container.canDragIntoSlot(slotIn)) {
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

		this.setBlitOffset(100);
		this.itemRenderer.zLevel = 100;

		if (itemstack.isEmpty() && slotIn.isEnabled()) {
			Pair<ResourceLocation, ResourceLocation> pair = slotIn.getBackground();

			if (pair != null) {
				TextureAtlasSprite textureatlassprite = this.minecraft.getAtlasSpriteGetter(pair.getFirst()).apply(pair.getSecond());
				this.minecraft.getTextureManager().bindTexture(textureatlassprite.getAtlasTexture().getTextureLocation());
				blit(matrices, i, j, this.getBlitOffset(), 16, 16, textureatlassprite);
				flag1 = true;
			}
		}

		if (!flag1) {
			if (flag) {
				fill(matrices, i, j, i + 16, j + 16, 0x80ffffff);
			}

			RenderSystem.enableDepthTest();
			this.itemRenderer.renderItemAndEffectIntoGUI(this.minecraft.player, itemstack, i, j);
			BigItemRenderer.INSTANCE.zLevel = this.itemRenderer.zLevel;
			if (slotIn instanceof DankSlot) {
				BigItemRenderer.INSTANCE.renderItemOverlayIntoGUI(this.font, itemstack, i, j, s);
			} else {
				this.itemRenderer.renderItemOverlayIntoGUI(this.font, itemstack, i, j, s);
			}
		}

		this.itemRenderer.zLevel = 0.0F;
		this.setBlitOffset(0);
		BigItemRenderer.INSTANCE.zLevel = itemRenderer.zLevel;
	}

	@Override
	protected <W extends Widget> W addButton(W button) {
		if (!button.getClass().getName().contains("quark"))
			return super.addButton(button);
		return button;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (Screen.hasControlDown()) {
			Slot slot = this.getSelectedSlot(mouseX, mouseY);
			if (slot instanceof DankSlot) {
				DankPacketHandler.INSTANCE.sendToServer(new C2SMessageLockSlot(slot.slotNumber));
				return true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}
}