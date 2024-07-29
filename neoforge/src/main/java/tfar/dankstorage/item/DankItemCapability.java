package tfar.dankstorage.item;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import tfar.dankstorage.utils.CommonUtils;
import tfar.dankstorage.utils.DankStats;
import tfar.dankstorage.world.DankInventoryForge;

public class DankItemCapability  {


    public static DankInventoryForge lookup(ItemStack stack) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        //this can be called clientside, functional storage does so for some reason
        //this should be replaced with a proper inventory at some point
        if (server != null) {
            return (DankInventoryForge) CommonUtils.getBagInventory(stack, server.overworld());
        }
        else {
            return new DankInventoryForge(DankStats.zero, -1);
        }
    }
}
