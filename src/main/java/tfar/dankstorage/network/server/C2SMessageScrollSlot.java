package tfar.dankstorage.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.network.util.C2SPacketHelper;
import tfar.dankstorage.utils.Utils;

public class C2SMessageScrollSlot implements C2SPacketHelper {

    private final boolean right;

    public C2SMessageScrollSlot(boolean right) {
        this.right = right;
    }

    public C2SMessageScrollSlot(FriendlyByteBuf buf) {
        right = buf.readBoolean();
    }

    public static void send(boolean right) {
        DankPacketHandler.sendToServer(new C2SMessageScrollSlot(right));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(right);
    }

    public void handleServer(ServerPlayer player) {
        if (player.getMainHandItem().getItem() instanceof DankItem)
            Utils.changeSelectedSlot(player.getMainHandItem(), right,player);
        else if (player.getOffhandItem().getItem() instanceof DankItem)
            Utils.changeSelectedSlot(player.getOffhandItem(), right,player);
    }
}

