package tfar.dankstorage.event;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.utils.ItemHandlerHelper;
import tfar.dankstorage.utils.PickupMode;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.DankInventory;

import java.util.ArrayList;
import java.util.List;

public class MixinHooks {

    /**
     * @param inv      Player Inventory to add the item to
     * @param incoming the itemstack being picked up
     * @return if the item was completely picked up by the dank(s)
     */
    public static boolean interceptItem(Inventory inv, ItemStack incoming) {
        Player player = inv.player;
        if (player.level.isClientSide || incoming.isEmpty()) {//thanks Hookshot
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

    public static boolean onItemPickup(Player player, ItemStack pickup, ItemStack dank) {

        PickupMode pickupMode = Utils.getPickupMode(dank);
        if (pickupMode == PickupMode.none) return false;
        DankInventory inv = Utils.getInventory(dank,player.level);

        if (inv == null) {
            DankStorage.LOGGER.warn("That's odd, the player somehow got an unassigned dank to change pickup mode");
            return false;
        }

        int count = pickup.getCount();
        boolean oredict = false;//Utils.oredict(dank);
        List<ItemStack> existing = new ArrayList<>();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                boolean exists = false;
                for (ItemStack stack1 : existing) {
                    if (areItemStacksCompatible(stack, stack1, oredict)) {
                        exists = true;
                    }
                }
                if (!exists) {
                    existing.add(stack.copy());
                }
            }
        }

        switch (pickupMode) {
            case pickup_all -> {
                for (int i = 0; i < inv.getContainerSize(); i++) {
                    allPickup(inv, i, pickup, oredict);
                    if (pickup.isEmpty()) break;
                }
            }
            case filtered_pickup -> {
                for (int i = 0; i < inv.getContainerSize(); i++) {
                    filteredPickup(inv, i, pickup, oredict, existing);
                    if (pickup.isEmpty()) break;
                }
            }
            case void_pickup -> {
                for (int i = 0; i < inv.getContainerSize(); i++) {
                    voidPickup(inv, i, pickup, oredict, existing);
                    if (pickup.isEmpty()) break;
                }
            }
        }

        //leftovers
        if (pickup.getCount() != count) {
            dank.setPopTime(5);
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
        return pickup.isEmpty();
    }

    public static void voidPickup(DankInventory inv, int slot, ItemStack toInsert, boolean oredict, List<ItemStack> filter) {
        ItemStack existing = inv.getItem(slot);

        if (doesItemStackExist(toInsert, filter, oredict) && areItemStacksCompatible(existing, toInsert, oredict)) {
            int stackLimit = inv.dankStats.stacklimit;
            int total = Math.min(toInsert.getCount() + existing.getCount(), stackLimit);
            //doesn't matter if it overflows cause it's all gone lmao
            inv.setItem(slot, ItemHandlerHelper.copyStackWithSize(existing, total));
            toInsert.setCount(0);
        }
    }

    public static void allPickup(DankInventory inv, int slot, ItemStack pickup, boolean oredict) {
        ItemStack existing = inv.getItem(slot);

        if (existing.isEmpty()) {
            int stackLimit = inv.dankStats.stacklimit;
            int total = pickup.getCount();
            int remainder = total - stackLimit;
            //no overflow
            if (remainder <= 0) {
                inv.setItem(slot, pickup.copy());
                pickup.setCount(0);
            } else {
                inv.setItem(slot, ItemHandlerHelper.copyStackWithSize(pickup, stackLimit));
                pickup.setCount(remainder);
            }
            return;
        }

        if (ItemHandlerHelper.canItemStacksStack(pickup, existing) || (oredict /*&& Utils.areItemStacksConvertible(pickup, existing)*/)) {
            int stackLimit = inv.dankStats.stacklimit;
            int total = pickup.getCount() + existing.getCount();
            int remainder = total - stackLimit;
            //no overflow
            if (remainder <= 0) {
                inv.setItem(slot, ItemHandlerHelper.copyStackWithSize(existing, total));
                pickup.setCount(0);
            } else {
                inv.setItem(slot, ItemHandlerHelper.copyStackWithSize(pickup, stackLimit));
                pickup.setCount(remainder);
            }
        }
    }

    public static void filteredPickup(DankInventory inv, int slot, ItemStack toInsert, boolean oredict, List<ItemStack> filter) {
        ItemStack existing = inv.getItem(slot);

        if (existing.isEmpty() && doesItemStackExist(toInsert, filter, oredict)) {
            int stackLimit = inv.dankStats.stacklimit;
            int total = toInsert.getCount();
            int remainder = total - stackLimit;
            //no overflow
            if (remainder <= 0) {
                inv.setItem(slot, toInsert.copy());
                toInsert.setCount(0);
            } else {
                inv.setItem(slot, ItemHandlerHelper.copyStackWithSize(toInsert, stackLimit));
                toInsert.setCount(remainder);
            }
            return;
        }

        if (doesItemStackExist(toInsert, filter, oredict) && areItemStacksCompatible(existing, toInsert, oredict)) {
            int stackLimit = inv.dankStats.stacklimit;
            int total = toInsert.getCount() + existing.getCount();
            int remainder = total - stackLimit;
            //no overflow
            if (remainder <= 0) {
                inv.setItem(slot, ItemHandlerHelper.copyStackWithSize(existing, total));
                toInsert.setCount(0);
            } else {
                inv.setItem(slot, ItemHandlerHelper.copyStackWithSize(toInsert, stackLimit));
                toInsert.setCount(remainder);
            }
        }
    }

    public static boolean areItemStacksCompatible(ItemStack stackA, ItemStack stackB, boolean oredict) {
        return oredict ? ItemStack.tagMatches(stackA, stackB) && ItemStack.isSame(stackA, stackB) /*|| Utils.areItemStacksConvertible(stackA, stackB) */:
                ItemStack.tagMatches(stackA, stackB) && ItemStack.isSame(stackA, stackB);
    }

    public static boolean doesItemStackExist(ItemStack stack, List<ItemStack> filter, boolean oredict) {
        for (ItemStack filterStack : filter) {
            if (areItemStacksCompatible(stack, filterStack, oredict)) return true;
        }
        return false;
    }
}
