package tfar.dankstorage.event;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.events.ClientEvents;
import tfar.dankstorage.network.server.C2SButtonPacket;
import tfar.dankstorage.utils.KeybindAction;
import tfar.dankstorage.utils.CommonUtils;

public class ForgeClientEvents {

    static Minecraft mc = Minecraft.getInstance();

    public static void renderStack(RegisterGuiLayersEvent e) {
        e.registerBelow(VanillaGuiLayers.CHAT, DankStorage.id("hud"),(pGuiGraphics, pDeltaTracker) -> {
            ClientEvents.renderSelectedItem(pGuiGraphics,pDeltaTracker);
        });
    }



    public static void onPickBlock(InputEvent.InteractionKeyMappingTriggered e) {
        if (e.isPickBlock()) {

            if (CommonUtils.isHoldingDank(mc.player) && mc.hitResult != null && mc.hitResult.getType() != HitResult.Type.MISS) {
                HitResult result = mc.player.pick(mc.player.blockInteractionRange(),0,false);
                if (result instanceof BlockHitResult) {
                    C2SButtonPacket.send(KeybindAction.PICK_BLOCK);
                    e.setCanceled(true);
                }
            }
        }
    }

    public static void onScroll(InputEvent.MouseScrollingEvent e) {
        if (ClientEvents.onScroll(e.getScrollDeltaY()))e.setCanceled(true);
    }
}
