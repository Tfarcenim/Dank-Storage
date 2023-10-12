package tfar.dankstorage.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.lwjgl.glfw.GLFW;
import tfar.dankstorage.DankStorageFabric;
import tfar.dankstorage.client.screens.DockScreen;
import tfar.dankstorage.client.screens.PortableDankStorageScreen;
import tfar.dankstorage.event.FabricEvents;
import tfar.dankstorage.network.ClientDankPacketHandler;
import tfar.dankstorage.network.server.C2SMessageTogglePickup;
import tfar.dankstorage.network.server.C2SMessageToggleUseType;

public class Client {

    public static KeyMapping CONSTRUCTION;
    public static KeyMapping LOCK_SLOT;
    public static KeyMapping PICKUP_MODE;

    public static void client() {

        HudRenderCallback.EVENT.register(FabricEvents::renderStack);

        ClientDankPacketHandler.registerClientMessages();
        ScreenRegistry.register(DankStorageFabric.dank_1_container, DockScreen::t1);
        ScreenRegistry.register(DankStorageFabric.portable_dank_1_container, PortableDankStorageScreen::t1);
        ScreenRegistry.register(DankStorageFabric.dank_2_container, DockScreen::t2);
        ScreenRegistry.register(DankStorageFabric.portable_dank_2_container, PortableDankStorageScreen::t2);
        ScreenRegistry.register(DankStorageFabric.dank_3_container, DockScreen::t3);
        ScreenRegistry.register(DankStorageFabric.portable_dank_3_container, PortableDankStorageScreen::t3);
        ScreenRegistry.register(DankStorageFabric.dank_4_container, DockScreen::t4);
        ScreenRegistry.register(DankStorageFabric.portable_dank_4_container, PortableDankStorageScreen::t4);
        ScreenRegistry.register(DankStorageFabric.dank_5_container, DockScreen::t5);
        ScreenRegistry.register(DankStorageFabric.portable_dank_5_container, PortableDankStorageScreen::t5);
        ScreenRegistry.register(DankStorageFabric.dank_6_container, DockScreen::t6);
        ScreenRegistry.register(DankStorageFabric.portable_dank_6_container, PortableDankStorageScreen::t6);
        ScreenRegistry.register(DankStorageFabric.dank_7_container, DockScreen::t7);
        ScreenRegistry.register(DankStorageFabric.portable_dank_7_container, PortableDankStorageScreen::t7);

        CONSTRUCTION = new KeyMapping("key.dankstorage.construction", GLFW.GLFW_KEY_I, "key.categories.dankstorage");
        LOCK_SLOT = new KeyMapping("key.dankstorage.lock_slot", GLFW.GLFW_KEY_LEFT_CONTROL, "key.categories.dankstorage");
        PICKUP_MODE = new KeyMapping("key.dankstorage.pickup_mode", GLFW.GLFW_KEY_O, "key.categories.dankstorage");

        KeyBindingHelper.registerKeyBinding(CONSTRUCTION);
        KeyBindingHelper.registerKeyBinding(LOCK_SLOT);
        KeyBindingHelper.registerKeyBinding(PICKUP_MODE);
        ClientTickEvents.START_CLIENT_TICK.register(Client::keyPressed);
        TooltipComponentCallback.EVENT.register(Client::tooltipImage);
    }

    public static void keyPressed(Minecraft client) {
        if (CONSTRUCTION.consumeClick()) {
            C2SMessageToggleUseType.send();
        }
        if (PICKUP_MODE.consumeClick()) {
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
