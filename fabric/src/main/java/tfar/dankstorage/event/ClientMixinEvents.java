package tfar.dankstorage.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tfar.dankstorage.network.server.C2SMessageScrollSlot;
import tfar.dankstorage.utils.Utils;

public class ClientMixinEvents {

    public static final Minecraft mc = Minecraft.getInstance();
    private static final Logger LOGGER = LogManager.getLogger();

    public static boolean onScroll(MouseHandler mouse, long window, double horizontal, double vertical, double delta) {
        Player player = mc.player;
        if (player!=null) {
            ItemStack main = player.getMainHandItem();
            ItemStack off = player.getOffhandItem();
            if (player.isCrouching() && (Utils.isConstruction(main) || Utils.isConstruction(off))) {
                boolean right = delta < 0;
                C2SMessageScrollSlot.send(right);
                return true;
            }
        }
        return false;
    }

}
