package tfar.dankstorage.world;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.transferapi.IItemResource;
import tfar.dankstorage.utils.DankStats;

public class DankInventoryForge extends DankInventory implements ResourceHandler<ItemResource> {


    public DankInventoryForge(DankStats stats, DankSavedData data) {
        super(stats,data);
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public ItemResource getResource(int i) {
        return ItemResource.of(getItemDank(i));
    }

    @Override
    public long getAmountAsLong(int index) {
        return getItemDank(index).getCount();
    }

    @Override
    public int insert(int slot, @NotNull ItemResource resource, int amount, TransactionContext context) {
        if (!inBounds(slot)) {
            warnOutOfBounds(slot);
            return 0;
        }
        return insertStackNew(slot,convertTo(resource),amount);
    }

    public static IItemResource convertTo(ItemResource resource) {
        return new IItemResource(resource.toStack());
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext context) {
        if (!inBounds(index)) {
            warnOutOfBounds(index);
            return 0;
        }
        return extractStackNew(index,convertTo(resource),amount);
    }

    @Override
    public long getCapacityAsLong(int slot,ItemResource itemResource) {
        if (!inBounds(slot)) {
            warnOutOfBounds(slot);
            return 0;
        }
        return capacity;
    }

    @Override
    public boolean isValid(int i, ItemResource itemResource) {
        return canPlaceItem(i,itemResource.toStack());
    }
}
