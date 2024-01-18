package tfar.dankstorage.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.network.DankPacketHandlerForge;
import tfar.dankstorage.network.util.C2SPacketHelper;
import tfar.dankstorage.utils.CommonUtils;


public class C2SMessagePickBlock implements C2SPacketHelper {

    private final ItemStack stack;

    public C2SMessagePickBlock(ItemStack stack) {
        this.stack = stack;
    }

    public C2SMessagePickBlock(FriendlyByteBuf buf) {
        stack = buf.readItem();
    }

    public static void send(ItemStack stack) {
        DankPacketHandlerForge.sendToServer(new C2SMessagePickBlock(stack));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeItem(stack);
    }

    public void handleServer(ServerPlayer player) {
        if (player.getMainHandItem().getItem() instanceof DankItem)
            CommonUtils.setPickSlot(player.level(),player.getMainHandItem(), stack);
        else if (player.getOffhandItem().getItem() instanceof DankItem)
            CommonUtils.setPickSlot(player.level(),player.getOffhandItem(), stack);
    }
}
