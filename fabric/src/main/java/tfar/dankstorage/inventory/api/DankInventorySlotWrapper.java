package tfar.dankstorage.inventory.api;

import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.world.DankInventory;

/**
 * A wrapper around a single slot of an inventory.
 * We must ensure that only one instance of this class exists for every inventory slot,
 * or the transaction logic will not work correctly.
 * This is handled by the Map in InventoryStorageImpl.
 */
public class DankInventorySlotWrapper extends SingleStackStorage {
	/**
	 * The strong reference to the InventoryStorageImpl ensures that the weak value doesn't get GC'ed when individual slots are still being accessed.
	 */
	private final DankInventory storage;
	final int slot;
	private ItemStack lastReleasedSnapshot = null;

	public DankInventorySlotWrapper(DankInventory storage, int slot) {
		this.storage = storage;
		this.slot = slot;
	}

	@Override
	protected ItemStack getStack() {
		return storage.getItem(slot);
	}

	@Override
	protected void setStack(ItemStack stack) {
		storage.setItem(slot, stack);
	}

	@Override
	protected boolean canInsert(ItemVariant itemVariant) {
		return storage.canPlaceItem(slot, itemVariant.toStack());
	}

	@Override
	public int getCapacity(ItemVariant variant) {
		return storage.getMaxStackSize();
	}

	// We override updateSnapshots to also schedule a markDirty call for the backing inventory.
	@Override
	public void updateSnapshots(TransactionContext transaction) {
		storage.setChanged();
		super.updateSnapshots(transaction);
	}

	@Override
	protected void releaseSnapshot(ItemStack snapshot) {
		lastReleasedSnapshot = snapshot;
	}

	@Override
	protected void onFinalCommit() {
		// Try to apply the change to the original stack
		ItemStack original = lastReleasedSnapshot;
		ItemStack currentStack = getStack();

		if (!original.isEmpty() && original.getItem() == currentStack.getItem()) {
			// None is empty and the items match: just update the amount and NBT, and reuse the original stack.
			original.setCount(currentStack.getCount());
			original.setTag(currentStack.hasTag() ? currentStack.getTag().copy() : null);
			setStack(original);
		} else {
			// Otherwise assume everything was taken from original so empty it.
			original.setCount(0);
		}
	}
}
