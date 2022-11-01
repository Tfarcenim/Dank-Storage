package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.Utils;

import java.util.function.Supplier;


public class C2SMessagePickBlock {

    private ItemStack stack;

    public C2SMessagePickBlock(ItemStack stack) {
        this.stack = stack;
    }

    public C2SMessagePickBlock(FriendlyByteBuf buf) {
        stack = buf.readItem();
    }

    public static void send(ItemStack stack) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeItem(stack);
        DankPacketHandler.sendToServer(new C2SMessagePickBlock(stack));
    }

    public void encode(FriendlyByteBuf buf) {

    }

    public void handle(ServerPlayer player, ItemStack stack) {
        if (player.getMainHandItem().getItem() instanceof DankItem)
            Utils.setPickSlot(player.level,player.getMainHandItem(), stack);
        else if (player.getOffhandItem().getItem() instanceof DankItem)
            Utils.setPickSlot(player.level,player.getOffhandItem(), stack);
    }

    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf) {
        ItemStack stack = buf.readItem();
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {

    }
}
