package tfar.dankstorage.inventory;

import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.utils.DankStats;

public interface DankInterface extends ContainerData {

    ItemStack getGhostItem(int slot);
    DankStats getDankStats();
}
