package tfar.dankstorage.network.client;

import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.menu.AbstractDankMenu;
import tfar.dankstorage.network.DankPacketHandler;

import java.util.List;

import static tfar.dankstorage.client.CommonClient.getLocalPlayer;

public class S2CInitialSyncContainerPacket implements S2CModPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CInitialSyncContainerPacket> STREAM_CODEC =
            StreamCodec.ofMember(S2CInitialSyncContainerPacket::write, S2CInitialSyncContainerPacket::new);


    public static final CustomPacketPayload.Type<S2CInitialSyncContainerPacket> TYPE = new CustomPacketPayload.Type<>(
            DankPacketHandler.packet(S2CInitialSyncContainerPacket.class));
    private final int stateID;
    private final int windowId;
    private final List<ItemStack> stacks;
    private final ItemStack carried;

    public S2CInitialSyncContainerPacket(int stateID,int windowId,NonNullList<ItemStack> stacks,ItemStack carried){

        this.stateID = stateID;
        this.windowId = windowId;
        this.stacks = stacks;
        this.carried = carried;
    }


    public S2CInitialSyncContainerPacket(RegistryFriendlyByteBuf buf) {
        stateID = buf.readInt();
        windowId = buf.readInt();
        carried = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
        stacks = ItemStack.OPTIONAL_LIST_STREAM_CODEC.decode(buf);
    }

    @Override
    public void handleClient() {
        Player player = getLocalPlayer();
        if (player != null && player.containerMenu instanceof AbstractDankMenu && windowId == player.containerMenu.containerId) {
            player.containerMenu.initializeContents(stateID, stacks, carried);
        }
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(stateID);
        buf.writeInt(windowId);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, carried);
        ItemStack.OPTIONAL_LIST_STREAM_CODEC.encode(buf, stacks);
    }

    @Override
    public Type<S2CInitialSyncContainerPacket> type() {
        return TYPE;
    }
}