package tfar.dankstorage.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.menu.AbstractDankMenu;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.network.util.C2SPacketHelper;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.utils.TxtColor;

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
        DankPacketHandler.sendToServer(new C2SSetFrequencyPacket(id, set));
    }

    public void handleServer(ServerPlayer player) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof AbstractDankMenu dankMenu) {
            DankInterface inventory = dankMenu.dankInventory;

            int textColor = 0;

            if (frequency > Utils.INVALID) {
                if (frequency < DankStorage.maxId.getMaxId()) {
                    DankInterface targetInventory = DankStorage.getData(frequency,player.server).createInventory(frequency);

                    if (targetInventory.valid() && targetInventory.getDankStats() == inventory.getDankStats()) {

                        if (targetInventory.frequencyLocked()) {
                            textColor = TxtColor.LOCKED.color;
                        } else {
                            textColor = TxtColor.GOOD.color;
                            if (set) {
                                dankMenu.setFrequency(frequency);
                                player.closeContainer();
                            }
                        }
                    } else {
                        textColor = TxtColor.DIFFERENT_TIER.color;
                    }
                } else {
                    //orange if it doesn't exist, yellow if it does but wrong tier
                    textColor = TxtColor.TOO_HIGH.color ;
                }
            } else {
                textColor = TxtColor.INVALID.color;
            }
            inventory.setTextColor(textColor);
        }
    }
}

