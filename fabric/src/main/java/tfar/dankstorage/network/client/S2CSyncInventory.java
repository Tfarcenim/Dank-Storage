package tfar.dankstorage.network.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import tfar.dankstorage.utils.PacketBufferEX;
import tfar.dankstorage.world.ClientData;

import java.util.List;

public class S2CSyncInventory implements ClientPlayNetworking.PlayChannelHandler {

    public void handle(@Nullable LocalPlayer player, List<ItemStack> stacks) {
        if (player != null) {
            ClientData.setList(stacks);
        }
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        List<ItemStack> stacks = PacketBufferEX.readList(buf);
        client.execute(() -> handle(client.player, stacks));
    }
}