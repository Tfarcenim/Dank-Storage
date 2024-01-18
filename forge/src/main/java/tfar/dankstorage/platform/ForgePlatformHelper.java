package tfar.dankstorage.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkDirection;
import tfar.dankstorage.DankStorageForge;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.inventory.DankSlot;
import tfar.dankstorage.network.DankPacketHandlerForge;
import tfar.dankstorage.network.IModPacket;
import tfar.dankstorage.platform.services.IPlatformHelper;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.world.DankInventoryForge;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public void sendToClient(IModPacket msg, ResourceLocation channel, ServerPlayer player) {
        DankPacketHandlerForge.INSTANCE.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    @Override
    public void sendToServer(IModPacket msg, ResourceLocation channel) {
        DankPacketHandlerForge.INSTANCE.sendToServer(msg);
    }

    @Override
    public DankInterface createInventory(DankStats stats, int frequency) {
        return new DankInventoryForge(stats,frequency);
    }

    @Override
    public Slot createSlot(DankInterface dankInventory, int index, int xPosition, int yPosition) {
        return new DankSlot((DankInventoryForge) dankInventory,index,xPosition,yPosition);
    }

    @Override
    public ItemStack getCloneStack(Level level, BlockPos pos, BlockState state, HitResult hitResult, Player player) {
        return state.getCloneItemStack(hitResult,level,pos,player);
    }

    @Override
    public boolean showPreview() {
        return DankStorageForge.ClientConfig.preview.get();
    }

    @Override
    public int previewX() {
        return DankStorageForge.ClientConfig.preview_x.get();
    }

    @Override
    public int previewY() {
        return DankStorageForge.ClientConfig.preview_y.get();
    }
}