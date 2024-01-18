package tfar.dankstorage.network.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.client.CommonClient;
import tfar.dankstorage.menu.AbstractDankMenu;
import tfar.dankstorage.network.S2CModPacket;
import tfar.dankstorage.utils.PacketBufferEX;

public class S2CSendGhostSlotPacket implements S2CModPacket {

    int windowId;
    int slot;
    ItemStack stack;

    public S2CSendGhostSlotPacket(int windowId, int slot, ItemStack stack) {
        this.windowId = windowId;
        this.slot = slot;
        this.stack = stack;
    }

    public S2CSendGhostSlotPacket(FriendlyByteBuf buf) {
        windowId = buf.readInt();
        slot = buf.readInt();
        stack = PacketBufferEX.readExtendedItemStack(buf);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(windowId);
        buf.writeInt(slot);
        PacketBufferEX.writeExtendedItemStack(buf, stack);
    }

    @Override
    public void handleClient() {
        Player player = CommonClient.getLocalPlayer();
        if (player != null && player.containerMenu instanceof AbstractDankMenu dankMenu && windowId == player.containerMenu.containerId) {
            dankMenu.dankInventory.setGhostItem(slot,stack.getItem());
        }
    }
}