package tfar.dankstorage.network.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import tfar.dankstorage.container.AbstractDankMenu;
import tfar.dankstorage.utils.PacketBufferEX;

public class S2CSyncGhostItemPacket implements ClientPlayNetworking.PlayChannelHandler {

    public void handle(@Nullable LocalPlayer player, int windowId, int slot, ItemStack stack) {
        if (player != null && player.containerMenu instanceof AbstractDankMenu dankMenu && windowId == player.containerMenu.containerId) {
            dankMenu.dankInventory.setGhostItem(slot,stack.getItem());
        }
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        int windowId = buf.readInt();
        int slot = buf.readInt();
        ItemStack stack = PacketBufferEX.readExtendedItemStack(buf);
        client.execute(() -> handle(client.player, windowId, slot, stack));
    }
}