package tfar.dankstorage.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.dankstorage.container.AbstractDankMenu;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.network.util.C2SPacketHelper;
import tfar.dankstorage.world.DankInventoryForge;

public class C2SLockSlotPacket implements C2SPacketHelper {

    private final int slot;

    public C2SLockSlotPacket(int slot) {
        this.slot = slot;
    }

    public C2SLockSlotPacket(FriendlyByteBuf buf) {
        slot = buf.readInt();
    }

    public static void send(int slot) {
        DankPacketHandler.sendToServer(new C2SLockSlotPacket(slot));
    }

    public void handleServer(ServerPlayer player) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof AbstractDankMenu dankContainer) {
            DankInventoryForge inventory = dankContainer.dankInventory;
            inventory.toggleGhostItem(slot);
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(slot);
    }
}

