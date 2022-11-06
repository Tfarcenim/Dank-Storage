package tfar.dankstorage.event;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.client.DankHudOverlay;
import tfar.dankstorage.utils.Utils;

public class ForgeClientEvents {

    static Minecraft mc = Minecraft.getInstance();

    public static void renderStack(RegisterGuiOverlaysEvent e) {
        e.registerBelow(VanillaGuiOverlay.CHAT_PANEL.id(), DankStorage.MODID,new DankHudOverlay());
    }



    public static void onPickBlock(InputEvent.InteractionKeyMappingTriggered e) {
        if (e.isPickBlock()) {

            if (Utils.isHoldingDank(mc.player) && mc.hitResult != null && mc.hitResult.getType() != HitResult.Type.MISS) {
                //C2SMessagePickBlock.send(mc.picked);
                e.setCanceled(true);
            }
        }
    }

    public static void onScroll(InputEvent.MouseScrollingEvent e) {
        if (ClientMixinEvents.onScroll(e.getScrollDelta()))e.setCanceled(true);
    }
}
