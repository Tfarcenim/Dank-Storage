package tfar.dankstorage.network;

import net.minecraft.resources.ResourceLocation;
import tfar.dankstorage.DankStorage;

public class PacketIds {
    public static final ResourceLocation set_frequency = new ResourceLocation(DankStorage.MODID, "set_frequency");
    public static final ResourceLocation request_contents = new ResourceLocation(DankStorage.MODID, "request_contents");
    public static final ResourceLocation sync_extended_slot = new ResourceLocation(DankStorage.MODID, "sync_extended_slot");
    public static final ResourceLocation sync_ghost_slot = new ResourceLocation(DankStorage.MODID, "sync_ghost_slot");
    public static final ResourceLocation initial_sync_container = new ResourceLocation(DankStorage.MODID, "initial_sync_container");
    public static final ResourceLocation sync_selected_dank_item = new ResourceLocation(DankStorage.MODID, "sync_selected_dank_item");
    public static final ResourceLocation sync_dank_inventory = new ResourceLocation(DankStorage.MODID, "sync_dank_inventory");
    public static final ResourceLocation button_action = new ResourceLocation(DankStorage.MODID, "button_action");
    public static final ResourceLocation lock_slot = new ResourceLocation(DankStorage.MODID, "lock_slot");
    public static final ResourceLocation scroll = new ResourceLocation(DankStorage.MODID, "scroll");
}
