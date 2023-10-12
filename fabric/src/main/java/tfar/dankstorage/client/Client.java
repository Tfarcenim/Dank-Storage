package tfar.dankstorage.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.lwjgl.glfw.GLFW;
import tfar.dankstorage.DankStorageFabric;
import tfar.dankstorage.client.screens.DankStorageScreen;
import tfar.dankstorage.event.FabricEvents;
import tfar.dankstorage.network.ClientDankPacketHandler;
import tfar.dankstorage.network.server.C2SMessageTogglePickup;
import tfar.dankstorage.network.server.C2SMessageToggleUseType;

public class Client {

    public static void client() {

        HudRenderCallback.EVENT.register(FabricEvents::renderStack);

        ClientDankPacketHandler.registerClientMessages();
        MenuScreens.register(DankStorageFabric.dank_1_container, DankStorageScreen::t1);
        MenuScreens.register(DankStorageFabric.portable_dank_1_container, DankStorageScreen::t1);
        MenuScreens.register(DankStorageFabric.dank_2_container, DankStorageScreen::t2);
        MenuScreens.register(DankStorageFabric.portable_dank_2_container, DankStorageScreen::t2);
        MenuScreens.register(DankStorageFabric.dank_3_container, DankStorageScreen::t3);
        MenuScreens.register(DankStorageFabric.portable_dank_3_container, DankStorageScreen::t3);
        MenuScreens.register(DankStorageFabric.dank_4_container, DankStorageScreen::t4);
        MenuScreens.register(DankStorageFabric.portable_dank_4_container, DankStorageScreen::t4);
        MenuScreens.register(DankStorageFabric.dank_5_container, DankStorageScreen::t5);
        MenuScreens.register(DankStorageFabric.portable_dank_5_container, DankStorageScreen::t5);
        MenuScreens.register(DankStorageFabric.dank_6_container, DankStorageScreen::t6);
        MenuScreens.register(DankStorageFabric.portable_dank_6_container, DankStorageScreen::t6);
        MenuScreens.register(DankStorageFabric.dank_7_container, DankStorageScreen::t7);
        MenuScreens.register(DankStorageFabric.portable_dank_7_container, DankStorageScreen::t7);

        DankKeybinds.CONSTRUCTION = new KeyMapping("key.dankstorage.construction", GLFW.GLFW_KEY_I, "key.categories.dankstorage");
        DankKeybinds.LOCK_SLOT = new KeyMapping("key.dankstorage.lock_slot", GLFW.GLFW_KEY_LEFT_CONTROL, "key.categories.dankstorage");
        DankKeybinds.PICKUP_MODE = new KeyMapping("key.dankstorage.pickup_mode", GLFW.GLFW_KEY_O, "key.categories.dankstorage");

        KeyBindingHelper.registerKeyBinding(DankKeybinds.CONSTRUCTION);
        KeyBindingHelper.registerKeyBinding(DankKeybinds.LOCK_SLOT);
        KeyBindingHelper.registerKeyBinding(DankKeybinds.PICKUP_MODE);
        ClientTickEvents.START_CLIENT_TICK.register(Client::keyPressed);
        TooltipComponentCallback.EVENT.register(Client::tooltipImage);
    }

    public static void keyPressed(Minecraft client) {
        if (DankKeybinds.CONSTRUCTION.consumeClick()) {
            C2SMessageToggleUseType.send();
        }
        if (DankKeybinds.PICKUP_MODE.consumeClick()) {
            C2SMessageTogglePickup.send();
        }
    }

    public static ClientTooltipComponent tooltipImage(TooltipComponent data) {
        if (data instanceof DankTooltip dankTooltip) {
            return new ClientDankTooltip(dankTooltip);
        }
        return null;
    }
}
