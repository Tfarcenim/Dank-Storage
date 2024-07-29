package tfar.dankstorage.client;

import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import tfar.dankstorage.event.ForgeClientEvents;
import tfar.dankstorage.utils.KeybindAction;
import tfar.dankstorage.network.server.C2SButtonPacket;

public class ModClientForge {

    public static void client() {

        MinecraftForge.EVENT_BUS.addListener(ForgeClientEvents::onPickBlock);
        MinecraftForge.EVENT_BUS.addListener(ForgeClientEvents::onScroll);
        MinecraftForge.EVENT_BUS.addListener(ModClientForge::keyPressed);
        CommonClient.setup();
    }

    public static void keybinds(RegisterKeyMappingsEvent e) {
        e.register(DankKeybinds.CONSTRUCTION);
        e.register(DankKeybinds.LOCK_SLOT);
        e.register(DankKeybinds.PICKUP_MODE);
    }

    public static void clientTool(RegisterClientTooltipComponentFactoriesEvent e) {
        e.register(DankTooltip.class, CommonClient::tooltipImage);
    }

    public static void keyPressed(TickEvent.ClientTickEvent client) {
        if (DankKeybinds.CONSTRUCTION.consumeClick()) {
            C2SButtonPacket.send(KeybindAction.TOGGLE_USE_TYPE);
        }
        if (DankKeybinds.PICKUP_MODE.consumeClick()) {
            C2SButtonPacket.send(KeybindAction.TOGGLE_PICKUP);
        }
    }
}
