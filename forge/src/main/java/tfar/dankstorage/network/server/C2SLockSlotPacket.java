package tfar.dankstorage.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.menu.AbstractDankMenu;
import tfar.dankstorage.network.DankPacketHandlerForge;
import tfar.dankstorage.network.util.C2SPacketHelper;

public class C2SLockSlotPacket implements C2SPacketHelper {

    private final int slot;

    public C2SLockSlotPacket(int slot) {
        this.slot = slot;
    }

    public C2SLockSlotPacket(FriendlyByteBuf buf) {
        slot = buf.readInt();
    }

    public static void send(int slot) {
        DankPacketHandlerForge.sendToServer(new C2SLockSlotPacket(slot));
    }

    public void handleServer(ServerPlayer player) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof AbstractDankMenu dankContainer) {
            DankInterface inventory = dankContainer.dankInventory;
            inventory.toggleGhostItem(slot);
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(slot);
    }
}

