package tfar.dankstorage.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import org.lwjgl.glfw.GLFW;
import tfar.dankstorage.client.screens.DankStorageScreen;
import tfar.dankstorage.container.DankMenu;
import tfar.dankstorage.container.DockMenu;
import tfar.dankstorage.event.ForgeClientEvents;
import tfar.dankstorage.init.ModMenuTypes;
import tfar.dankstorage.network.server.C2SButtonPacket;

public class Client {

    public static final Minecraft mc = Minecraft.getInstance();

    public static void client() {

        MinecraftForge.EVENT_BUS.addListener(ForgeClientEvents::onPickBlock);
        MinecraftForge.EVENT_BUS.addListener(ForgeClientEvents::onScroll);

        MenuScreens.register(ModMenuTypes.dank_1_container, (DockMenu container, Inventory playerinventory, Component component) -> DankStorageScreen.t1(container, playerinventory, component));
        MenuScreens.register(ModMenuTypes.portable_dank_1_container, (DankMenu container, Inventory playerinventory, Component component) -> DankStorageScreen.t1(container, playerinventory, component));
        MenuScreens.register(ModMenuTypes.dank_2_container, (DockMenu container, Inventory playerinventory, Component component) -> DankStorageScreen.t2(container, playerinventory, component));
        MenuScreens.register(ModMenuTypes.portable_dank_2_container, (DankMenu container, Inventory playerinventory, Component component) -> DankStorageScreen.t2(container, playerinventory, component));
        MenuScreens.register(ModMenuTypes.dank_3_container, (DockMenu container, Inventory playerinventory, Component component) -> DankStorageScreen.t3(container, playerinventory, component));
        MenuScreens.register(ModMenuTypes.portable_dank_3_container, (DankMenu container, Inventory playerinventory, Component component) -> DankStorageScreen.t3(container, playerinventory, component));
        MenuScreens.register(ModMenuTypes.dank_4_container, (DockMenu container, Inventory playerinventory, Component component) -> DankStorageScreen.t4(container, playerinventory, component));
        MenuScreens.register(ModMenuTypes.portable_dank_4_container, (DankMenu container, Inventory playerinventory, Component component) -> DankStorageScreen.t4(container, playerinventory, component));
        MenuScreens.register(ModMenuTypes.dank_5_container, (DockMenu container, Inventory playerinventory, Component component) -> DankStorageScreen.t5(container, playerinventory, component));
        MenuScreens.register(ModMenuTypes.portable_dank_5_container, (DankMenu container, Inventory playerinventory, Component component) -> DankStorageScreen.t5(container, playerinventory, component));
        MenuScreens.register(ModMenuTypes.dank_6_container, (DockMenu container, Inventory playerinventory, Component component) -> DankStorageScreen.t6(container, playerinventory, component));
        MenuScreens.register(ModMenuTypes.portable_dank_6_container, (DankMenu container, Inventory playerinventory, Component component) -> DankStorageScreen.t6(container, playerinventory, component));
        MenuScreens.register(ModMenuTypes.dank_7_container, (DockMenu container, Inventory playerinventory, Component component) -> DankStorageScreen.t7(container, playerinventory, component));
        MenuScreens.register(ModMenuTypes.portable_dank_7_container, (DankMenu container, Inventory playerinventory, Component component) -> DankStorageScreen.t7(container, playerinventory, component));

        MinecraftForge.EVENT_BUS.addListener(Client::keyPressed);
    }

    public static void keybinds(RegisterKeyMappingsEvent e) {
        DankKeybinds.CONSTRUCTION = new KeyMapping("key.dankstorage.construction", GLFW.GLFW_KEY_I, "key.categories.dankstorage");
        DankKeybinds.LOCK_SLOT = new KeyMapping("key.dankstorage.lock_slot", GLFW.GLFW_KEY_LEFT_CONTROL, "key.categories.dankstorage");
        DankKeybinds.PICKUP_MODE = new KeyMapping("key.dankstorage.pickup_mode", GLFW.GLFW_KEY_O, "key.categories.dankstorage");
        e.register(DankKeybinds.CONSTRUCTION);
        e.register(DankKeybinds.LOCK_SLOT);
        e.register(DankKeybinds.PICKUP_MODE);
    }

    public static void clientTool(RegisterClientTooltipComponentFactoriesEvent e) {
        e.register(DankTooltip.class, CommonClient::tooltipImage);
    }

    public static void keyPressed(TickEvent.ClientTickEvent client) {
        if (DankKeybinds.CONSTRUCTION.consumeClick()) {
            C2SButtonPacket.send(C2SButtonPacket.Action.TOGGLE_USE_TYPE);
        }
        if (DankKeybinds.PICKUP_MODE.consumeClick()) {
            C2SButtonPacket.send(C2SButtonPacket.Action.TOGGLE_PICKUP);
        }
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
