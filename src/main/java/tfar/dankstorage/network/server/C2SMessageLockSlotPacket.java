package tfar.dankstorage.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.dankstorage.container.AbstractDankMenu;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.network.util.C2SPacketHelper;
import tfar.dankstorage.world.DankInventory;

public class C2SMessageLockSlotPacket implements C2SPacketHelper {

    private final int slot;

    public C2SMessageLockSlotPacket(int slot) {
        this.slot = slot;
    }

    public C2SMessageLockSlotPacket(FriendlyByteBuf buf) {
        slot = buf.readInt();
    }

    public static void send(int slot) {
        DankPacketHandler.sendToServer(new C2SMessageLockSlotPacket(slot));
    }

    public void handleServer(ServerPlayer player) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof AbstractDankMenu dankContainer) {
            DankInventory inventory = dankContainer.dankInventory;
            inventory.toggleGhostItem(slot);
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(slot);
    }
}

