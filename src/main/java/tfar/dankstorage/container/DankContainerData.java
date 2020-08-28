package tfar.dankstorage.container;

import net.minecraft.util.IIntArray;
import tfar.dankstorage.inventory.DankHandler;

public class DankContainerData implements IIntArray {

	public int slots;
	public DankHandler dankInventory;
	public int nbtSize;

	public DankContainerData(DankHandler dankInventory) {
		this.slots = dankInventory.getSlots();
		this.dankInventory = dankInventory;
	}

	@Override
	public int get(int i) {
		return i == slots ? nbtSize : dankInventory.lockedSlots[i];
	}

	@Override
	public void set(int i, int value) {
		if (i == slots) {
			this.nbtSize = value;
		} else {
			dankInventory.lockedSlots[i] = value;
		}
	}

	public int size() {
		return slots + 1;
	}
}
