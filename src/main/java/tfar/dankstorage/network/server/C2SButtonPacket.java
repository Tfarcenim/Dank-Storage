package tfar.dankstorage.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import tfar.dankstorage.container.AbstractDankMenu;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.DankInventory;

import java.util.function.Supplier;

public class C2SButtonPacket {

    private final Action action;

    public C2SButtonPacket(Action action) {
        this.action = action;
    }

    public C2SButtonPacket(FriendlyByteBuf buf) {
        action = Action.values()[buf.readInt()];
    }

    public static void send(Action action) {
        DankPacketHandler.sendToServer(new C2SButtonPacket(action));
    }

    public void handleInternal(ServerPlayer player, Action action) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof AbstractDankMenu dankContainer) {
            DankInventory inventory = dankContainer.dankInventory;
            switch (action) {
                case LOCK_FREQUENCY -> inventory.toggleFrequencyLock();
                case SORT -> inventory.sort();
                case TOGGLE_TAG -> Utils.toggleTagMode(player);
                case COMPRESS -> inventory.compress(player);
            }
        }
        switch (action) {
            case TOGGLE_PICKUP -> Utils.togglePickupMode(player);
            case TOGGLE_USE_TYPE -> Utils.toggleUseType(player);
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(action.ordinal());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        ctx.get().enqueueWork(() -> {
            if (player != null) {
                handleInternal(player,action);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public enum Action {
        LOCK_FREQUENCY,PICK_BLOCK,SORT,TOGGLE_TAG,TOGGLE_PICKUP,TOGGLE_USE_TYPE,COMPRESS
    }
}
