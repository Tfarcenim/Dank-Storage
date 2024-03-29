package tfar.dankstorage.event;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.client.DankHudOverlay;
import tfar.dankstorage.network.server.C2SMessagePickBlock;
import tfar.dankstorage.utils.Utils;

public class ForgeClientEvents {

    static Minecraft mc = Minecraft.getInstance();

    public static void renderStack(RegisterGuiOverlaysEvent e) {
        e.registerBelow(VanillaGuiOverlay.CHAT_PANEL.id(), DankStorage.MODID,new DankHudOverlay());
    }



    public static void onPickBlock(InputEvent.InteractionKeyMappingTriggered e) {
        if (e.isPickBlock()) {

            if (Utils.isHoldingDank(mc.player) && mc.hitResult != null && mc.hitResult.getType() != HitResult.Type.MISS) {

                HitResult result = mc.player.pick(mc.player.getReachDistance(),0,false);

                if (result instanceof BlockHitResult blockHitResult) {
                    BlockPos pos = blockHitResult.getBlockPos();
                    BlockState state = mc.player.level.getBlockState(pos);

                    C2SMessagePickBlock.send(state.getCloneItemStack(blockHitResult,mc.player.level,pos, mc.player));
                    e.setCanceled(true);
                }
            }
        }
    }

    public static void onScroll(InputEvent.MouseScrollingEvent e) {
        if (ClientMixinEvents.onScroll(e.getScrollDelta()))e.setCanceled(true);
    }
}
