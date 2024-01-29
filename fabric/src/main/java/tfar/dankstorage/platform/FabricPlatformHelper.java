package tfar.dankstorage.platform;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.inventory.DankSlot;
import tfar.dankstorage.network.client.S2CModPacket;
import tfar.dankstorage.network.server.C2SModPacket;
import tfar.dankstorage.platform.services.IPlatformHelper;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.world.DankInventoryFabric;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }


    @Override
    public void sendToClient(S2CModPacket msg, ResourceLocation channel, ServerPlayer player) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        msg.write(buf);
        ServerPlayNetworking.send(player, channel, buf);
    }

    @Override
    public void sendToServer(C2SModPacket msg, ResourceLocation channel) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        msg.write(buf);
        ClientPlayNetworking.send(channel, buf);
    }

    @Override
    public DankInterface createInventory(DankStats stats, int frequency) {
        return new DankInventoryFabric(stats,frequency);
    }

    @Override
    public Slot createSlot(DankInterface dankInventory, int index, int xPosition, int yPosition) {
        return new DankSlot((DankInventoryFabric) dankInventory,index,xPosition,yPosition);
    }

    @Override
    public ItemStack getCloneStack(Level level, BlockPos pos, BlockState state, HitResult hitResult, Player player) {
        return state.getBlock().getCloneItemStack(level,pos,state);
    }

    @Override
    public boolean showPreview() {
        return true;
    }

    //hardcoded for now

    @Override
    public int previewX() {
        return -140;
    }

    @Override
    public int previewY() {
        return -25;
    }
}
