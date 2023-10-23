package tfar.dankstorage.network.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.container.AbstractDankMenu;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.TxtColor;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.DankInventoryFabric;

public class C2SSetFrequencyPacket implements ServerPlayNetworking.PlayChannelHandler {

    public static void send(int id,boolean set) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(id);
        buf.writeBoolean(set);
        ClientPlayNetworking.send(DankPacketHandler.set_id, buf);
    }

    public void handle(ServerPlayer player, int frequency,boolean set) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof AbstractDankMenu dankMenu) {
            DankInventoryFabric inventory = dankMenu.dankInventory;

            int textColor = 0;

            if (frequency > Utils.INVALID) {
                if (frequency < DankStorage.maxId.getMaxId()) {
                    DankInterface targetInventory = DankStorage.getData(frequency,player.server).createInventory(frequency);

                    if (targetInventory.valid() && targetInventory.getDankStats() == inventory.dankStats) {

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

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        int id = buf.readInt();
        boolean set = buf.readBoolean();
        server.execute(() -> handle(player, id,set));
    }
}

