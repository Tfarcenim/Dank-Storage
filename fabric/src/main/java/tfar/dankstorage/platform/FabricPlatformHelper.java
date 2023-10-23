package tfar.dankstorage.platform;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.network.server.*;
import tfar.dankstorage.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import tfar.dankstorage.utils.ButtonAction;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.utils.Utils;
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
    public void sendGhostItemSlot(ServerPlayer player, int id, int slot, ItemStack stack) {
        DankPacketHandler.sendGhostItem(player, id, slot, stack);
    }

    @Override
    public void sendCustomSyncData(ServerPlayer player, int stateID, int containerID, NonNullList<ItemStack> stacks, ItemStack carried) {
        DankPacketHandler.sendSyncContainer(player, stateID, containerID, stacks, carried);
    }

    @Override
    public void sendCustomSlotChange(ServerPlayer player, int id, int slot, ItemStack stack) {
        DankPacketHandler.sendSyncSlot(player, id, slot, stack);
    }

    @Override
    public void sendRequestContentsPacket(int frequency) {
        C2SRequestContentsPacket.send(frequency);
    }

    @Override
    public void sendScrollPacket(boolean right) {
        C2SMessageScrollSlot.send(right);
    }

    @Override
    public void sendFrequencyPacket(int frequency, boolean set) {
        C2SSetFrequencyPacket.send(frequency, set);
    }

    @Override
    public void sendLockSlotPacket(int index) {
        C2SMessageLockSlot.send(index);
    }

    @Override
    public void sendButtonPacket(ButtonAction action) {
        C2SButtonPacket.send(action);
    }

    @Override
    public DankInterface createInventory(DankStats stats, int frequency) {
        return new DankInventoryFabric(stats,frequency);
    }

    @Override
    public DankInterface getInventoryCommon(ItemStack bag, Level level) {
        return Utils.getInventory(bag,level);
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
