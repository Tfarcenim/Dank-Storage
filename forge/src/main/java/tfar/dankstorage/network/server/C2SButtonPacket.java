package tfar.dankstorage.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import tfar.dankstorage.container.AbstractDankMenu;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.CommonUtils;
import tfar.dankstorage.world.DankInventoryForge;

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

        if (action.requiresContainer) {
            if (container instanceof AbstractDankMenu dankContainer) {
                DankInventoryForge inventory = dankContainer.dankInventoryForge;
                switch (action) {
                    case LOCK_FREQUENCY -> inventory.toggleFrequencyLock();
                    case SORT -> inventory.sort();
                    case COMPRESS -> inventory.compress(player);
                }
            }
        } else {
            switch (action) {
                case TOGGLE_TAG -> CommonUtils.toggleTagMode(player);
                case TOGGLE_PICKUP -> CommonUtils.togglePickupMode(player);
                case TOGGLE_USE_TYPE -> CommonUtils.toggleUseType(player);
            }
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
        LOCK_FREQUENCY(true),PICK_BLOCK(false),SORT(true),
        TOGGLE_TAG(true),TOGGLE_PICKUP(false),TOGGLE_USE_TYPE(false),COMPRESS(true);
        private final boolean requiresContainer;
        Action(boolean requiresContainer) {
            this.requiresContainer = requiresContainer;
        }
    }
}
