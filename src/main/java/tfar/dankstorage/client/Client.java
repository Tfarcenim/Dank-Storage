package tfar.dankstorage.client;

import com.mojang.blaze3d.systems.RenderSystem;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.DankItem;
import tfar.dankstorage.client.screens.DankScreens;
import tfar.dankstorage.client.screens.PortableDankStorageScreen;
import tfar.dankstorage.inventory.PortableDankHandler;
import tfar.dankstorage.network.C2SMessageScrollSlot;
import tfar.dankstorage.network.CMessagePickBlock;
import tfar.dankstorage.network.CMessageToggleUseType;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Client {

  public static KeyBinding CONSTRUCTION;
  public static final Minecraft mc = Minecraft.getInstance();

  @SubscribeEvent
  public static void client(FMLClientSetupEvent e) {
    ScreenManager.registerFactory(DankStorage.Objects.dank_1_container, DankScreens.DankStorageScreen1::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_1_container, PortableDankStorageScreen::t1);
    ScreenManager.registerFactory(DankStorage.Objects.dank_2_container, DankScreens.DankStorageScreen2::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_2_container, PortableDankStorageScreen::t2);
    ScreenManager.registerFactory(DankStorage.Objects.dank_3_container, DankScreens.DankStorageScreen3::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_3_container, PortableDankStorageScreen::t3);
    ScreenManager.registerFactory(DankStorage.Objects.dank_4_container, DankScreens.DankStorageScreen4::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_4_container, PortableDankStorageScreen::t4);
    ScreenManager.registerFactory(DankStorage.Objects.dank_5_container, DankScreens.DankStorageScreen5::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_5_container, PortableDankStorageScreen::t5);
    ScreenManager.registerFactory(DankStorage.Objects.dank_6_container, DankScreens.DankStorageScreen6::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_6_container, PortableDankStorageScreen::t6);
    ScreenManager.registerFactory(DankStorage.Objects.dank_7_container, DankScreens.DankStorageScreen7::new);
    ScreenManager.registerFactory(DankStorage.Objects.portable_dank_7_container, PortableDankStorageScreen::t7);

    CONSTRUCTION = new KeyBinding("key.dankstorage.construction", GLFW.GLFW_KEY_I, "key.categories.dankstorage");

    ClientRegistry.registerKeyBinding(CONSTRUCTION);


  }

  @Mod.EventBusSubscriber(value = Dist.CLIENT)
  public static class KeyHandler {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
      if (mc.player == null || !(mc.player.getHeldItemMainhand().getItem() instanceof DankItem || mc.player.getHeldItemOffhand().getItem() instanceof DankItem))
        return;
      if (CONSTRUCTION.isPressed()) {
        DankPacketHandler.INSTANCE.sendToServer(new CMessageToggleUseType());
      }
      if (mc.gameSettings.keyBindPickBlock.isPressed()) {
        DankPacketHandler.INSTANCE.sendToServer(new CMessagePickBlock());
      }
    }

    @SubscribeEvent
    public static void mousewheel(InputEvent.MouseScrollEvent e) {
      PlayerEntity player = Minecraft.getInstance().player;
      if (player != null && player.isCrouching() && (Utils.isConstruction(player.getHeldItemMainhand()) || Utils.isConstruction(player.getHeldItemOffhand()))) {
        boolean right = e.getScrollDelta() < 0;
        DankPacketHandler.INSTANCE.sendToServer(new C2SMessageScrollSlot(right));
        e.setCanceled(true);
      }
    }

    @SubscribeEvent
    public static void onRenderTick(RenderGameOverlayEvent.Post event) {
      PlayerEntity player = mc.player;
      if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR || !DankStorage.ClientConfig.preview.get() || player == null)
        return;
      if (!(player.openContainer instanceof PlayerContainer)) return;
      ItemStack bag = player.getHeldItemMainhand();
      if (!(bag.getItem() instanceof DankItem)) {
        bag = player.getHeldItemOffhand();
        if (!(bag.getItem() instanceof DankItem))
          return;
      }
      int xStart = event.getWindow().getScaledWidth() / 2;
      int yStart = event.getWindow().getScaledHeight();
      if (Utils.isConstruction(bag)) {
        PortableDankHandler handler = Utils.getHandler(bag);
        ItemStack toPlace = handler.getStackInSlot(Utils.getSelectedSlot(bag));

        if (!toPlace.isEmpty()) {


          Integer color = toPlace.getItem().getRarity(toPlace).color.getColor();

          int c = color != null ? color : 0xFFFFFF;


          final int itemX = xStart - 150;
          final int itemY = yStart - 25;
          renderHotbarItem(itemX, itemY, 0, player, toPlace);
        }
      }
      final int stringX = xStart - 155;
      final int stringY = yStart - 10;
      String mode = Utils.getUseType(bag).name();
      mc.fontRenderer.drawStringWithShadow(event.getMatrixStack(),mode,stringX,stringY,0xffffff);
      mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
    }
  }

  private static void renderHotbarItem(int x, int y, float partialTicks, PlayerEntity player, ItemStack stack) {
    float f = (float) stack.getAnimationsToGo() - partialTicks;
    if (f > 0.0F) {
      RenderSystem.pushMatrix();
      float f1 = 1.0F + f / 5.0F;
      RenderSystem.translatef((float) (x + 8), (float) (y + 12), 0.0F);
      RenderSystem.scalef(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
      RenderSystem.translatef((float) (-(x + 8)), (float) (-(y + 12)), 0.0F);
    }

    mc.getItemRenderer().renderItemAndEffectIntoGUI(player, stack, x, y);
    if (f > 0.0F) {
      RenderSystem.popMatrix();
    }
    mc.getItemRenderer().renderItemOverlays(mc.fontRenderer, stack, x, y);
  }
}
