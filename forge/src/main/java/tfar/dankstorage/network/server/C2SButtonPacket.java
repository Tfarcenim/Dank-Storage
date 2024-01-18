package tfar.dankstorage.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.menu.AbstractDankMenu;
import tfar.dankstorage.network.DankPacketHandlerForge;
import tfar.dankstorage.network.util.C2SPacketHelper;
import tfar.dankstorage.utils.ButtonAction;
import tfar.dankstorage.utils.CommonUtils;

public class C2SButtonPacket implements C2SPacketHelper {

    private final ButtonAction buttonAction;

    public C2SButtonPacket(ButtonAction buttonAction) {
        this.buttonAction = buttonAction;
    }

    public C2SButtonPacket(FriendlyByteBuf buf) {
        buttonAction = ButtonAction.values()[buf.readInt()];
    }

    public static void send(ButtonAction buttonAction) {
        DankPacketHandlerForge.sendToServer(new C2SButtonPacket(buttonAction));
    }

    public void handleServer(ServerPlayer player) {
        AbstractContainerMenu container = player.containerMenu;

        if (buttonAction.requiresContainer) {
            if (container instanceof AbstractDankMenu dankContainer) {
                DankInterface inventory = dankContainer.dankInventory;
                switch (buttonAction) {
                    case LOCK_FREQUENCY -> inventory.toggleFrequencyLock();
                    case SORT -> inventory.sort();
                    case COMPRESS -> inventory.compress(player);
                }
            }
        } else {
            switch (buttonAction) {
                case TOGGLE_TAG -> CommonUtils.toggleTagMode(player);
                case TOGGLE_PICKUP -> CommonUtils.togglePickupMode(player);
                case TOGGLE_USE_TYPE -> CommonUtils.toggleUseType(player);
            }
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(buttonAction.ordinal());
    }

}
