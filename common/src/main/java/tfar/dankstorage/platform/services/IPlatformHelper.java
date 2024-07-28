package tfar.dankstorage.platform.services;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.network.IModPacket;
import tfar.dankstorage.network.client.S2CModPacket;
import tfar.dankstorage.network.server.C2SModPacket;
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

    void sendToClient(S2CModPacket msg, ResourceLocation channel, ServerPlayer player);
    void sendToServer(C2SModPacket msg, ResourceLocation channel);

    ItemStack getCloneStack(Level level, BlockPos pos, BlockState state, HitResult hitResult, Player player);


    DankInterface createInventory(DankStats stats,int frequency);
    boolean showPreview();
    int previewX();
    int previewY();

    Slot createSlot(DankInterface dankInventory, int index, int xPosition, int yPosition);

    <T extends Registry<? extends F>,F> void registerAll(Class<?> clazz, T registry, Class<? extends F> filter);

}