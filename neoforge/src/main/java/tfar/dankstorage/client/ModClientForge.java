package tfar.dankstorage.client;


import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import tfar.dankstorage.event.ForgeClientEvents;
import tfar.dankstorage.utils.KeybindAction;
import tfar.dankstorage.network.server.C2SButtonPacket;

public class ModClientForge {

    public static void client() {

        NeoForge.EVENT_BUS.addListener(ForgeClientEvents::onPickBlock);
        NeoForge.EVENT_BUS.addListener(ForgeClientEvents::onScroll);
        NeoForge.EVENT_BUS.addListener(ModClientForge::keyPressed);
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

    public static void keyPressed(ClientTickEvent client) {
        if (DankKeybinds.CONSTRUCTION.consumeClick()) {
            C2SButtonPacket.send(KeybindAction.TOGGLE_USE_TYPE);
        }
        if (DankKeybinds.PICKUP_MODE.consumeClick()) {
            C2SButtonPacket.send(KeybindAction.TOGGLE_PICKUP);
        }
    }
}
