package com.tfar.dankstorage.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.block.DankItemBlock;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.network.*;
import com.tfar.dankstorage.screen.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
public class Client {

  public static KeyBinding TOGGLE_MODE;
  public static KeyBinding CONSTRUCTION;
  private static final Minecraft mc = Minecraft.getInstance();

  @SubscribeEvent
  public static void client(FMLClientSetupEvent e) {
    ScreenManager.registerFactory(DankStorage.Objects.dank_1_container, DankScreens.DankStorageScreen1::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_1_container, DankScreens.PortableDankStorageScreen1::new);
    ScreenManager.registerFactory(DankStorage.Objects.dank_2_container, DankScreens.DankStorageScreen2::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_2_container, DankScreens.PortableDankStorageScreen2::new);
    ScreenManager.registerFactory(DankStorage.Objects.dank_3_container, DankScreens.DankStorageScreen3::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_3_container, DankScreens.PortableDankStorageScreen3::new);
    ScreenManager.registerFactory(DankStorage.Objects.dank_4_container, DankScreens.DankStorageScreen4::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_4_container, DankScreens.PortableDankStorageScreen4::new);
    ScreenManager.registerFactory(DankStorage.Objects.dank_5_container, DankScreens.DankStorageScreen5::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_5_container, DankScreens.PortableDankStorageScreen5::new);
    ScreenManager.registerFactory(DankStorage.Objects.dank_6_container, DankScreens.DankStorageScreen6::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_6_container, DankScreens.PortableDankStorageScreen6::new);
    ScreenManager.registerFactory(DankStorage.Objects.dank_7_container, DankScreens.DankStorageScreen7::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_7_container, DankScreens.PortableDankStorageScreen7::new);

    TOGGLE_MODE = new KeyBinding("key.dankstorage.pickup", GLFW.GLFW_KEY_P, "key.categories.dankstorage");
    CONSTRUCTION = new KeyBinding("key.dankstorage.construction", GLFW.GLFW_KEY_I, "key.categories.dankstorage");

    ClientRegistry.registerKeyBinding(TOGGLE_MODE);
    ClientRegistry.registerKeyBinding(CONSTRUCTION);
  }

  @Mod.EventBusSubscriber(value = Dist.CLIENT)
  public static class KeyHandler {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
      if (mc.player == null || !(mc.player.getHeldItemMainhand().getItem() instanceof DankItemBlock || mc.player.getHeldItemOffhand().getItem() instanceof DankItemBlock))return;
      if (TOGGLE_MODE.isPressed()) {
        DankPacketHandler.INSTANCE.sendToServer(new CMessageToggle());
      }
      if (CONSTRUCTION.isPressed()) {
        DankPacketHandler.INSTANCE.sendToServer(new CMessageConstructionMode());
      }
      if (mc.gameSettings.keyBindPickBlock.isPressed()) {
        DankPacketHandler.INSTANCE.sendToServer(new CMessagePickBlock());
      }
    }

    @SubscribeEvent
    public static void mousewheel(InputEvent.MouseScrollEvent e) {
      PlayerEntity player = Minecraft.getInstance().player;
      if (player != null && player.isSneaking() && (Utils.construction(player.getHeldItemMainhand()) || Utils.construction(player.getHeldItemOffhand()))) {
        boolean right = e.getScrollDelta() < 0;
        DankPacketHandler.INSTANCE.sendToServer(new CMessageChangeSlot(right));
        e.setCanceled(true);
      }
    }

    @SubscribeEvent
    public static void onRenderTick(RenderGameOverlayEvent event) {
      PlayerEntity player = mc.player;
      if (!DankStorage.ClientConfig.preview.get() || player == null)return;
      if (!(player.openContainer instanceof PlayerContainer)) return;
      ItemStack bag = player.getHeldItemMainhand();
      if (!(bag.getItem()instanceof DankItemBlock)){
        bag = player.getHeldItemOffhand();
        if (!(bag.getItem()instanceof DankItemBlock))
        return;
      }
      PortableDankHandler handler = Utils.getHandler(bag,true);
      ItemStack toPlace = handler.getStackInSlot(Utils.getSelectedSlot(bag));
      ITextComponent s = toPlace.getDisplayName();
     // String s1 = s.getUnformattedComponentText();
      //String slot = String.valueOf(Utils.getSelectedSlot(bag));

      Integer color = toPlace.getItem().getRarity(toPlace).color.getColor();

      int c = color != null ? color : 0xFFFFFF;

      if (!toPlace.isEmpty()) {
        GlStateManager.enableRescaleNormal();
        RenderHelper.enableGUIStandardItemLighting();
        int xStart = event.getWindow().getScaledWidth()/2;
        int yStart = event.getWindow().getScaledHeight();
        final int itemX = xStart - 150;
        final int itemY = yStart - 20;

        mc.getItemRenderer().renderItemAndEffectIntoGUI(toPlace, itemX, itemY);

        mc.getItemRenderer().renderItemOverlays(mc.fontRenderer, toPlace, itemX, itemY);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
      }
      mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
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
