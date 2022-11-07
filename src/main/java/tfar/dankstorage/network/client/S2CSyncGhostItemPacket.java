package tfar.dankstorage.network.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.container.AbstractDankMenu;
import tfar.dankstorage.network.util.S2CPacketHelper;
import tfar.dankstorage.utils.PacketBufferEX;

import static tfar.dankstorage.client.Client.getLocalPlayer;

public class S2CSyncGhostItemPacket implements S2CPacketHelper {

    int windowId;
    int slot;
    ItemStack stack;

    public S2CSyncGhostItemPacket(int windowId, int slot, ItemStack stack) {
        this.windowId = windowId;
        this.slot = slot;
        this.stack = stack;
    }

    public S2CSyncGhostItemPacket(FriendlyByteBuf buf) {
        windowId = buf.readInt();
        slot = buf.readInt();
        stack = PacketBufferEX.readExtendedItemStack(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(windowId);
        buf.writeInt(slot);
        PacketBufferEX.writeExtendedItemStack(buf, stack);
    }

    @Override
    public void handleClient() {
        Player player = getLocalPlayer();
        if (player != null && player.containerMenu instanceof AbstractDankMenu dankMenu && windowId == player.containerMenu.containerId) {
            dankMenu.dankInventory.setGhostItem(slot,stack.getItem());
        }
    }
}