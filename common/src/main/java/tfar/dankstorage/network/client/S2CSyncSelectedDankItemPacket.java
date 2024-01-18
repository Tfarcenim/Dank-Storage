package tfar.dankstorage.network.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.utils.PacketBufferEX;
import tfar.dankstorage.world.ClientData;

import static tfar.dankstorage.client.CommonClient.getLocalPlayer;

public class S2CSyncSelectedDankItemPacket implements S2CModPacket {

    private final ItemStack stack;

    public S2CSyncSelectedDankItemPacket(ItemStack stack) {
        this.stack = stack;
    }

    public S2CSyncSelectedDankItemPacket(FriendlyByteBuf buf) {
        stack = PacketBufferEX.readExtendedItemStack(buf);
    }

    @Override
    public void handleClient() {
        Player player = getLocalPlayer();
        if (player != null) {
            ClientData.setData(stack);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        PacketBufferEX.writeExtendedItemStack(buf,stack);
    }
}