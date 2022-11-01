package tfar.dankstorage.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import tfar.dankstorage.container.AbstractDankMenu;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.world.DankInventory;

import java.util.function.Supplier;

public class C2SMessageLockSlot {

    private final int slot;

    public C2SMessageLockSlot(int slot) {
        this.slot = slot;
    }

    public C2SMessageLockSlot(FriendlyByteBuf buf) {
        slot = buf.readInt();
    }

    public static void send(int slot) {
        DankPacketHandler.sendToServer(new C2SMessageLockSlot(slot));
    }

    public void handle(ServerPlayer player, int slot) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof AbstractDankMenu dankContainer) {
            DankInventory inventory = dankContainer.dankInventory;
            inventory.toggleSlotLock(slot);
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(slot);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {

    }
}

