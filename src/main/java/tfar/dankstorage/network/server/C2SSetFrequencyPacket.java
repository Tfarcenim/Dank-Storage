package tfar.dankstorage.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.container.AbstractDankMenu;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.network.util.C2SPacketHelper;
import tfar.dankstorage.world.DankInventory;

public class C2SSetFrequencyPacket implements C2SPacketHelper {

    private final int frequency;
    private final boolean set;

    public C2SSetFrequencyPacket(int frequency, boolean set) {
        this.frequency = frequency;
        this.set = set;
    }

    public C2SSetFrequencyPacket(FriendlyByteBuf buf) {
        frequency = buf.readInt();
        set = buf.readBoolean();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(frequency);
        buf.writeBoolean(set);
    }

    public static void send(int id, boolean set) {
        DankPacketHandler.sendToServer(new C2SSetFrequencyPacket(id,set));
    }

    public void handleServer(ServerPlayer player) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof AbstractDankMenu dankMenu) {
            DankInventory inventory = dankMenu.dankInventory;

            int textColor;

            if (frequency > -1) {
                DankInventory targetInventory = DankStorage.instance.data.getInventory(frequency);

                if (targetInventory != null && targetInventory.dankStats == inventory.dankStats) {

                    if (targetInventory.locked) {
                     textColor = DankInventory.TxtColor.LOCKED.color;
                    } else {
                        textColor = DankInventory.TxtColor.GOOD.color;
                        if (set) {
                            dankMenu.setFrequency(frequency);
                            player.closeContainer();
                        }
                    }
                } else {
                    //orange if it doesn't exist, yellow if it does but wrong tier
                    textColor = targetInventory == null ? DankInventory.TxtColor.TOO_HIGH.color : DankInventory.TxtColor.DIFFERENT_TIER.color;
                }
            } else {
                textColor = DankInventory.TxtColor.INVALID.color;
            }
             inventory.setTextColor(textColor);
        }
    }
}

