package tfar.dankstorage.platform.services;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {

        return isDevelopmentEnvironment() ? "development" : "production";
    }

    void sendGhostItemSlot(ServerPlayer player, int id, int slot, ItemStack stack);
    void sendCustomSyncData(ServerPlayer player, int stateID, int containerID, NonNullList<ItemStack> stacks, ItemStack carried);
    void sendCustomSlotChange(ServerPlayer player, int id, int slot, ItemStack stack);
    void sendRequestContentsPacket(int frequency);

    void sendScrollPacket(boolean right);
    void sendFrequencyPacket(int frequency,boolean set);

}