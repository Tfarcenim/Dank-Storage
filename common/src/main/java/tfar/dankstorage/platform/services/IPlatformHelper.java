package tfar.dankstorage.platform.services;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.network.IModPacket;
import tfar.dankstorage.utils.DankStats;

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

    void sendRequestContentsPacket(int frequency);

    void sendScrollPacket(boolean right);
    void sendFrequencyPacket(int frequency,boolean set);
    void sendLockSlotPacket(int index);

    void sendToClient(IModPacket msg, ResourceLocation channel, ServerPlayer player);
    void sendToServer(IModPacket msg, ResourceLocation channel);


    DankInterface createInventory(DankStats stats,int frequency);
    boolean showPreview();
    int previewX();
    int previewY();

    Slot createSlot(DankInterface dankInventory, int index, int xPosition, int yPosition);

}