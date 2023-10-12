package tfar.dankstorage.network.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import tfar.dankstorage.container.AbstractDankMenu;
import tfar.dankstorage.utils.PacketBufferEX;

public class S2CSyncContainerContents implements ClientPlayNetworking.PlayChannelHandler {

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        int stateID = buf.readInt();
        int containerID = buf.readInt();

        ItemStack carried = buf.readItem();

        int i = buf.readShort();
        NonNullList<ItemStack> stacks = NonNullList.withSize(i, ItemStack.EMPTY);

        for(int j = 0; j < i; ++j) {
            stacks.set(j, PacketBufferEX.readExtendedItemStack(buf));
        }
        client.execute(() -> handle(client.player, stateID, containerID,stacks,carried));
    }

    public void handle(@Nullable LocalPlayer player,int stateID, int windowId, NonNullList<ItemStack> stacks,ItemStack carried) {
        if (player != null && player.containerMenu instanceof AbstractDankMenu && windowId == player.containerMenu.containerId) {
            player.containerMenu.initializeContents(stateID, stacks, carried);

        }
    }
}