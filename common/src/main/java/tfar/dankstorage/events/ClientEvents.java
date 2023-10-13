package tfar.dankstorage.events;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.platform.Services;
import tfar.dankstorage.utils.CommonUtils;

public class ClientEvents {

    public static final Minecraft mc = Minecraft.getInstance();

    public static boolean onScroll(double delta) {
        Player player = mc.player;
        if (player!=null) {
            ItemStack main = player.getMainHandItem();
            ItemStack off = player.getOffhandItem();
            if (player.isCrouching() && (CommonUtils.isConstruction(main) || CommonUtils.isConstruction(off))) {
                boolean right = delta < 0;
                Services.PLATFORM.sendScrollPacket(right);
                return true;
            }
        }
        return false;
    }
}
