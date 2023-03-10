package tfar.dankstorage.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.network.util.C2SPacketHelper;
import tfar.dankstorage.utils.Utils;

import java.util.function.Supplier;


public class C2SMessagePickBlock implements C2SPacketHelper {

    private final ItemStack stack;

    public C2SMessagePickBlock(ItemStack stack) {
        this.stack = stack;
    }

    public C2SMessagePickBlock(FriendlyByteBuf buf) {
        stack = buf.readItem();
    }

    public static void send(ItemStack stack) {
        DankPacketHandler.sendToServer(new C2SMessagePickBlock(stack));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeItem(stack);
    }

    public void handleServer(ServerPlayer player) {
        if (player.getMainHandItem().getItem() instanceof DankItem)
            Utils.setPickSlot(player.level,player.getMainHandItem(), stack);
        else if (player.getOffhandItem().getItem() instanceof DankItem)
            Utils.setPickSlot(player.level,player.getOffhandItem(), stack);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {

    }
}
