package com.tfar.dankstorage.client;

import com.tfar.dankstorage.block.DankItemBlock;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.network.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
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
  }

  @Mod.EventBusSubscriber(value = Side.CLIENT)
  public static class KeyHandler {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
      if (mc.player == null || !(mc.player.getHeldItemMainhand().getItem() instanceof DankItemBlock))return;
      if (AUTO_PICKUP.isPressed()) {
        DankPacketHandler.INSTANCE.sendToServer(new MessageToggleAutoPickup());
      }
      if (AUTO_VOID.isPressed()) {
        DankPacketHandler.INSTANCE.sendToServer(new MessageToggleAutoVoid());
      }
      if (CONSTRUCTION.isPressed()) {
        DankPacketHandler.INSTANCE.sendToServer(new MessageToggleConstruction());
      }
      if (mc.gameSettings.keyBindPickBlock.isPressed()) {
        DankPacketHandler.INSTANCE.sendToServer(new MessagePickBlock());
      }
    }

    @SubscribeEvent
    public static void mousewheel(InputEvent.MouseInputEvent e) {
      EntityPlayer player = mc.player;
      if (player.getHeldItemMainhand().getItem() instanceof DankItemBlock && player.isSneaking()) {
        boolean right = e.getScrollDelta() < 0;
        DankPacketHandler.INSTANCE.sendToServer(new MessageChangeSlot(right));
        e.setCanceled(true);
      }
    }

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
      EntityPlayer player = mc.player;
      if (player == null)return;
      if (!(player.openContainer instanceof ContainerPlayer)) return;
      ItemStack bag = player.getHeldItemMainhand();
      if (!(bag.getItem()instanceof DankItemBlock))return;
      PortableDankHandler handler = Utils.getHandler(bag);
      ItemStack toPlace = handler.getStackInSlot(Utils.getSelectedSlot(bag));
      String s = toPlace.getDisplayName();
     // String s1 = s.getUnformattedComponentText();
      //String slot = String.valueOf(Utils.getSelectedSlot(bag));

      int c = toPlace.getItem().getRarity(toPlace).color.getColorIndex();

      if (!toPlace.isEmpty()) {
        GlStateManager.enableRescaleNormal();
        RenderHelper.enableGUIStandardItemLighting();
        int xStart = mc.getScaledWidth()/2;
        int yStart = mc.mainWindow.getScaledWidth()/2;
        final int itemX = xStart - 175;
        final int itemY = yStart + 20;

        mc.getItemRenderer().renderOverlays(toPlace, itemX, itemY);

        mc.getItemRenderer().renderItemOverlays(mc.fontRenderer, toPlace, itemX, itemY);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
      }

      //drawLineOffsetStringOnHUD(s1,0, 0, c, 0);
     // drawLineOffsetStringOnHUD(slot,0, 0, c, 3);
    //  drawLineOffsetStringOnHUD(String.valueOf(Utils.construction(bag)),0, 0, c, 25);
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
