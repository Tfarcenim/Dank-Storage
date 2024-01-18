package tfar.dankstorage.network.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.network.util.S2CPacketHelper;
import tfar.dankstorage.utils.PacketBufferEX;
import tfar.dankstorage.world.ClientData;

import static tfar.dankstorage.client.CommonClient.getLocalPlayer;

public class S2CSyncSelectedItemPacket implements S2CPacketHelper {

    private final ItemStack stack;

    public S2CSyncSelectedItemPacket(ItemStack stack) {
        this.stack = stack;
    }

    public S2CSyncSelectedItemPacket(FriendlyByteBuf buf) {
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
    public void encode(FriendlyByteBuf buf) {
        PacketBufferEX.writeExtendedItemStack(buf,stack);
    }
}