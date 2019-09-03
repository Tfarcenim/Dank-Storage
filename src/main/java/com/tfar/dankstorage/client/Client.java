package com.tfar.dankstorage.client;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.block.DankItemBlock;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.network.*;
import com.tfar.dankstorage.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;


@Mod.EventBusSubscriber(value = Side.CLIENT)
public class Client {

  public static KeyBinding AUTO_PICKUP;
  public static KeyBinding AUTO_VOID;
  public static KeyBinding CONSTRUCTION;
  private static final Minecraft mc = Minecraft.getMinecraft();

  @SubscribeEvent
  public static void client(ModelRegistryEvent e) {

    AUTO_PICKUP = new KeyBinding("key.dankstorage.pickup", Keyboard.KEY_P, "key.categories.dankstorage");
    AUTO_VOID = new KeyBinding("key.dankstorage.void", Keyboard.KEY_O, "key.categories.dankstorage");
    CONSTRUCTION = new KeyBinding("key.dankstorage.construction", Keyboard.KEY_I, "key.categories.dankstorage");

    ClientRegistry.registerKeyBinding(AUTO_PICKUP);
    ClientRegistry.registerKeyBinding(AUTO_VOID);
    ClientRegistry.registerKeyBinding(CONSTRUCTION);
    for (Block block : DankStorage.RegistryEvents.MOD_BLOCKS)
      registerModelLocation(Item.getItemFromBlock(block));
  }

  private static void registerModelLocation(Item item) {
    ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
  }

  @Mod.EventBusSubscriber(value = Side.CLIENT)
  public static class KeyHandler {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
      if (mc.player == null || !(mc.player.getHeldItemMainhand().getItem() instanceof DankItemBlock)) return;
      if (AUTO_PICKUP.isPressed()) {
        DankPacketHandler.INSTANCE.sendToServer(new CMessageToggleAutoPickup());
      }
      if (AUTO_VOID.isPressed()) {
        DankPacketHandler.INSTANCE.sendToServer(new CMessageToggleAutoVoid());
      }
      if (CONSTRUCTION.isPressed()) {
        DankPacketHandler.INSTANCE.sendToServer(new CMessageToggleConstruction());
      }
      if (mc.gameSettings.keyBindPickBlock.isPressed()) {
        DankPacketHandler.INSTANCE.sendToServer(new CMessagePickBlock());
      }
    }
  }

    @SubscribeEvent
    public static void mousewheel(MouseEvent e) {
      EntityPlayer player = mc.player;
      if (player.getHeldItemMainhand().getItem() instanceof DankItemBlock && player.isSneaking() && e.getDwheel() != 0) {
        boolean right = e.getDwheel() < 0;
        DankPacketHandler.INSTANCE.sendToServer(new CMessageChangeSlot(right));
        e.setCanceled(true);
      }
    }

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
      EntityPlayer player = mc.player;
      if (player == null) return;
      if (!(player.openContainer instanceof ContainerPlayer)) return;
      ItemStack bag = player.getHeldItemMainhand();
      if (!(bag.getItem() instanceof DankItemBlock)) return;
      PortableDankHandler handler = Utils.getHandler(bag);
      ItemStack toPlace = handler.getStackInSlot(Utils.getSelectedSlot(bag));
      String s = toPlace.getDisplayName();
      // String s1 = s.getUnformattedComponentText();
      //String slot = String.valueOf(Utils.getSelectedSlot(bag));

      int count = toPlace.getCount();

      if (!toPlace.isEmpty()) {
        GlStateManager.enableRescaleNormal();
        RenderHelper.enableGUIStandardItemLighting();
        int itemX = 0;
        int itemY = 0;
        float pickupAnimation = toPlace.getAnimationsToGo() - 1;
        if (pickupAnimation > 0.0F) {
          GlStateManager.pushMatrix();
          float scale = 1 + pickupAnimation / 5;
          GlStateManager.translate(itemX + 8, itemY + 12, 0);
          GlStateManager.scale(1 / scale, scale + 1 / 2f, 1);
          GlStateManager.translate(-(itemX + 8), -(itemY + 12), 0);
        }
        mc.getRenderItem().renderItemAndEffectIntoGUI(toPlace, itemX, itemY);
        mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer,toPlace, itemX, itemY,null);
        if (pickupAnimation > 0.0F)
          mc.getRenderItem().renderItemOverlays(mc.fontRenderer, toPlace, itemX, itemY);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        //GlStateManager.popMatrix();
      }
    }

    private static FontRenderer fontRenderer;

    public static void drawLineOffsetStringOnHUD(String string, int xOffset, int yOffset, int color, int lineOffset) {
      drawStringOnHUD(string, xOffset, yOffset, color, lineOffset);
    }

    public static void drawStringOnHUD(String string, int xOffset, int yOffset, int color, int lineOffset) {
      yOffset += lineOffset * 9;
      if (fontRenderer == null) fontRenderer = mc.fontRenderer;
      fontRenderer.drawString(string, 2 + xOffset, 2 + yOffset, color);
    }
  }
