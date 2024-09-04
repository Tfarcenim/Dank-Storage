package tfar.dankstorage.inventory.api;

import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.world.DankInventoryFabric;

import java.util.Objects;

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
	private final DankInventoryFabric storage;
	final int slot;
	private ItemStack lastReleasedSnapshot = null;

	public DankInventorySlotWrapper(DankInventoryFabric storage, int slot) {
		this.storage = storage;
		this.slot = slot;
	}

	@Override
	protected ItemStack getStack() {
		return storage.getItemDank(slot);
	}

	@Override
	protected void setStack(ItemStack stack) {
		storage.setItemDank(slot, stack);
	}

	@Override
	protected boolean canInsert(ItemVariant itemVariant) {
		return storage.canPlaceItem(slot, itemVariant.toStack());
	}

	@Override
	public int getCapacity(ItemVariant variant) {
		return storage.getMaxStackSizeSensitive(variant.toStack());
	}

	// We override updateSnapshots to also schedule a markDirty call for the backing inventory.
	@Override
	public void updateSnapshots(TransactionContext transaction) {
		storage.setDirty(false);
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
			// Components have changed, we need to copy the stack.
			if (!Objects.equals(original.getComponentsPatch(), currentStack.getComponentsPatch())) {
				// Remove all the existing components and copy the new ones on top.
				for (DataComponentType<?> type : original.getComponents().keySet()) {
					original.set(type, null);
				}

				original.applyComponents(currentStack.getComponents());
			}

			// None is empty and the items and components match: just update the amount, and reuse the original stack.
			original.setCount(currentStack.getCount());
			setStack(original);
		} else {
			// Otherwise assume everything was taken from original so empty it.
			original.setCount(0);
		}
	}
}
