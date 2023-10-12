package tfar.dankstorage.event;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.DankStorageFabric;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.utils.PickupMode;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.DankInventory;

public class MixinHooks {

    /**
     * @param inv      Player Inventory to add the item to
     * @param incoming the itemstack being picked up
     * @return if the item was completely picked up by the dank(s)
     */
    public static boolean interceptItem(Inventory inv, ItemStack incoming) {
        Player player = inv.player;
        if (player.level().isClientSide || incoming.isEmpty()) {//thanks Hookshot
            return false;
        }
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack possibleDank = inv.getItem(i);
            if (possibleDank.getItem() instanceof DankItem && onItemPickup(player, incoming, possibleDank)) {
                return true;
            }
        }
        return false;
    }

    public static boolean onItemPickup(Player player, ItemStack original, ItemStack dank) {

        PickupMode pickupMode = Utils.getPickupMode(dank);
        if (pickupMode == PickupMode.none) return false;
        DankInventory inv = Utils.getInventory(dank,player.level());

        if (inv == null) {
            DankStorageFabric.LOGGER.warn("That's odd, the player somehow got an unassigned dank to change pickup mode");
            return false;
        }

        boolean oredict = false;//Utils.oredict(dank);

        //use a copy to avoid accidentally mutating the original
        ItemStack rejected = pickupInv(inv,original.copy(),oredict,pickupMode);

        //leftovers
        if (original.getCount() > rejected.getCount()) {
            //it's safe to shrink the count here
            original.setCount(rejected.getCount());

            dank.setPopTime(5);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
        return original.isEmpty();
    }

    public static ItemStack pickupInv(DankInventory inv, ItemStack toInsert, boolean oredict, PickupMode mode) {
        for (int i = 0; i < inv.getContainerSize();i++) {
            ItemStack existing = inv.getItem(i);
            if (existing.isEmpty()) {
                //watch out for ghosts
                ItemStack ghost = inv.getGhostItem(i);
                //this slot isn't locked
                if (ghost.isEmpty()) {
                    //only add items if on pickup all mode, filtered and void can't add to completely empty slots
                    if  (mode == PickupMode.pickup_all) {
                        inv.setItem(i,toInsert);
                        //we are done
                        return ItemStack.EMPTY;
                    }
                } else {
                    boolean compatible = areItemStacksCompatible(ghost,toInsert,oredict);
                    //respect the ghost items
                    if (!compatible) continue;
                    inv.setItem(i,toInsert);
                    //we are done
                    return ItemStack.EMPTY;
                }
            } else {
                boolean compatible = areItemStacksCompatible(toInsert,existing,oredict);
                if (compatible) {
                    int limit = inv.getMaxStackSize();

                    boolean full = limit <= existing.getCount();

                    if (full) {
                        if (mode == PickupMode.pickup_all || mode == PickupMode.filtered_pickup) {
                            //move to the next slot, nothing should be done
                            continue;
                        } else {
                            //void the item and return
                            return ItemStack.EMPTY;
                        }
                    }

                    int existingCount = existing.getCount();
                    boolean aboveLimit = limit < toInsert.getCount() + existingCount;
                    if (aboveLimit) {
                        //set the existing item to the max size
                        existing.setCount(limit);
                        int remainder = toInsert.getCount() + existingCount - limit;
                        if (mode == PickupMode.void_pickup) {
                            //void overflow and return
                            return ItemStack.EMPTY;
                        } else {
                            //shrink the item and continue
                            toInsert.shrink(remainder);
                        }
                    } else {
                        existing.grow(toInsert.getCount());
                        //everything has been added, return now
                        return ItemStack.EMPTY;
                    }
                }
            }
        }
        return toInsert;
    }

    //checks if the items can be combined regardless of pickup mode
    public static boolean areItemStacksCompatible(ItemStack stackA, ItemStack stackB, boolean oredict) {

        boolean sameItemSameTags = ItemStack.isSameItemSameTags(stackA, stackB);

        return oredict ? doItemStacksShareTags(stackA, stackB) || sameItemSameTags : sameItemSameTags;
    }

    public static boolean doItemStacksShareTags(ItemStack a,ItemStack b) {
        return false;
    }
}
