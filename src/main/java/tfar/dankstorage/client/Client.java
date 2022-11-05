package tfar.dankstorage.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import org.lwjgl.glfw.GLFW;
import tfar.dankstorage.client.screens.DockScreen;
import tfar.dankstorage.client.screens.PortableDankStorageScreen;
import tfar.dankstorage.event.ForgeEvents;
import tfar.dankstorage.init.ModMenuTypes;
import tfar.dankstorage.network.server.C2SButtonPacket;

public class Client {

    public static final Minecraft mc = Minecraft.getInstance();
    public static KeyMapping CONSTRUCTION;
    public static KeyMapping LOCK_SLOT;
    public static KeyMapping PICKUP_MODE;

    public static void client() {

        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::renderStack);
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::onPickBlock);
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::onScroll);

        MenuScreens.register(ModMenuTypes.dank_1_container, DockScreen::t1);
        MenuScreens.register(ModMenuTypes.portable_dank_1_container, PortableDankStorageScreen::t1);
        MenuScreens.register(ModMenuTypes.dank_2_container, DockScreen::t2);
        MenuScreens.register(ModMenuTypes.portable_dank_2_container, PortableDankStorageScreen::t2);
        MenuScreens.register(ModMenuTypes.dank_3_container, DockScreen::t3);
        MenuScreens.register(ModMenuTypes.portable_dank_3_container, PortableDankStorageScreen::t3);
        MenuScreens.register(ModMenuTypes.dank_4_container, DockScreen::t4);
        MenuScreens.register(ModMenuTypes.portable_dank_4_container, PortableDankStorageScreen::t4);
        MenuScreens.register(ModMenuTypes.dank_5_container, DockScreen::t5);
        MenuScreens.register(ModMenuTypes.portable_dank_5_container, PortableDankStorageScreen::t5);
        MenuScreens.register(ModMenuTypes.dank_6_container, DockScreen::t6);
        MenuScreens.register(ModMenuTypes.portable_dank_6_container, PortableDankStorageScreen::t6);
        MenuScreens.register(ModMenuTypes.dank_7_container, DockScreen::t7);
        MenuScreens.register(ModMenuTypes.portable_dank_7_container, PortableDankStorageScreen::t7);

        CONSTRUCTION = new KeyMapping("key.dankstorage.construction", GLFW.GLFW_KEY_I, "key.categories.dankstorage");
        LOCK_SLOT = new KeyMapping("key.dankstorage.lock_slot", GLFW.GLFW_KEY_LEFT_CONTROL, "key.categories.dankstorage");
        PICKUP_MODE = new KeyMapping("key.dankstorage.pickup_mode", GLFW.GLFW_KEY_O, "key.categories.dankstorage");

        ClientRegistry.registerKeyBinding(CONSTRUCTION);
        ClientRegistry.registerKeyBinding(LOCK_SLOT);
        ClientRegistry.registerKeyBinding(PICKUP_MODE);
        MinecraftForge.EVENT_BUS.addListener(Client::keyPressed);
        MinecraftForgeClient.registerTooltipComponentFactory(DankTooltip.class, Client::tooltipImage);
    }

    public static void keyPressed(TickEvent.ClientTickEvent client) {
        if (CONSTRUCTION.consumeClick()) {
            C2SButtonPacket.send(C2SButtonPacket.Action.TOGGLE_USE_TYPE);
        }
        if (PICKUP_MODE.consumeClick()) {
            C2SButtonPacket.send(C2SButtonPacket.Action.TOGGLE_PICKUP);
        }
    }

    public static ClientTooltipComponent tooltipImage(TooltipComponent data) {
        if (data instanceof DankTooltip dankTooltip) {
            return new ClientDankTooltip(dankTooltip);
        }
        return null;
    }



  /*public static class KeyHandler {
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
      if (mc.player == null || !(mc.player.getMainHandStack().getItem() instanceof DankItem || mc.player.getOffHandStack().getItem() instanceof DankItem))
        return;
      if (CONSTRUCTION.wasPressed()) {
        DankPacketHandler.INSTANCE.sendToServer(new CMessageToggleUseType());
      }
      if (mc.options.keyPickItem.wasPressed()) {
        DankPacketHandler.INSTANCE.sendToServer(new CMessagePickBlock());
      }
    }

    public static void onMouseInput(InputEvent.MouseInputEvent event) {
      if (mc.player == null || !(mc.player.getMainHandStack().getItem() instanceof DankItem || mc.player.getOffHandStack().getItem() instanceof DankItem))
        return;
      if (CONSTRUCTION.wasPressed()) {
        DankPacketHandler.INSTANCE.sendToServer(new CMessageToggleUseType());
      }
      if (mc.options.keyPickItem.wasPressed()) {
        DankPacketHandler.INSTANCE.sendToServer(new CMessagePickBlock());
      }
    }

    public static void mousewheel(InputEvent.MouseScrollEvent e) {
      PlayerEntity player = MinecraftClient.getInstance().player;
      if (player != null && player.isInSneakingPose() && (Utils.isConstruction(player.getMainHandStack()) || Utils.isConstruction(player.getOffHandStack()))) {
        boolean right = e.getScrollDelta() < 0;
        DankPacketHandler.INSTANCE.sendToServer(new C2SMessageScrollSlot(right));
        e.setCanceled(true);
      }
    }*/

    public static Player getLocalPlayer() {
        return mc.player;
    }
}
