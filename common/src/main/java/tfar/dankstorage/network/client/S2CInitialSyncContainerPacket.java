package tfar.dankstorage.network.client;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.menu.AbstractDankMenu;
import tfar.dankstorage.utils.PacketBufferEX;

import static tfar.dankstorage.client.CommonClient.getLocalPlayer;

public class S2CInitialSyncContainerPacket implements S2CModPacket {

    private final int stateID;
    private final int windowId;
    private final NonNullList<ItemStack> stacks;
    private final ItemStack carried;

    public S2CInitialSyncContainerPacket(int stateID, int windowId, NonNullList<ItemStack> stacks, ItemStack carried) {
        this.stateID = stateID;
        this.windowId = windowId;
        this.stacks = stacks;
        this.carried = carried;
    }

    public S2CInitialSyncContainerPacket(FriendlyByteBuf buf) {
        stateID = buf.readInt();
        windowId = buf.readInt();
        carried = buf.readItem();
        int i = buf.readShort();
        stacks = NonNullList.withSize(i, ItemStack.EMPTY);
        for(int j = 0; j < i; ++j) {
            stacks.set(j, PacketBufferEX.readExtendedItemStack(buf));
        }
    }

    @Override
    public void handleClient() {
        Player player = getLocalPlayer();
        if (player != null && player.containerMenu instanceof AbstractDankMenu && windowId == player.containerMenu.containerId) {
            player.containerMenu.initializeContents(stateID, stacks, carried);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(stateID);
        buf.writeInt(windowId);
        buf.writeItem(carried);
        buf.writeShort(stacks.size());
        for (ItemStack stack : stacks) {
            PacketBufferEX.writeExtendedItemStack(buf, stack);
        }
    }
}