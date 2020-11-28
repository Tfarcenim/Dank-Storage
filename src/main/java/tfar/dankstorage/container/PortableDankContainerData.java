package tfar.dankstorage.container;

import net.minecraft.util.IIntArray;
import tfar.dankstorage.inventory.DankHandler;
import tfar.dankstorage.inventory.PortableDankHandler;

public class PortableDankContainerData implements IIntArray {

	public int slots;
	public DankHandler dankInventory;
	public int nbtSize;
	public int selectedSlot;

	public PortableDankContainerData(PortableDankHandler dankInventory,int selectedSlot) {
		this.slots = dankInventory.getSlots();
		this.dankInventory = dankInventory;
		this.selectedSlot = selectedSlot;
	}

	@Override
	public int get(int i) {
		if (i == slots + 1) {
			return selectedSlot;
		}
		return i == slots ? nbtSize : dankInventory.lockedSlots[i];
	}

	@Override
	public void set(int i, int value) {
		if (i == slots + 1 ) {
			this.selectedSlot = value;
		}
		if (i == slots) {
			this.nbtSize = value;
		} else {
			dankInventory.lockedSlots[i] = value;
		}
	}

	public int size() {
		return slots + 2;
	}
}
