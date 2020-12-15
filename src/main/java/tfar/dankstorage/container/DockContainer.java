package tfar.dankstorage.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.inventory.DankHandler;
import tfar.dankstorage.utils.DankMenuType;

public class DockContainer extends AbstractDankContainer {

	//client
	public DockContainer(ContainerType<?> type, int id, PlayerInventory playerInventory) {
		this(type, id,playerInventory,new DankHandler(((DankMenuType)type).stats),new IntArray(((DankMenuType)type).stats.slots));
	}

	//common
	public DockContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, DankHandler dankHandler, IIntArray propertyDelegate) {
		super(type, id,playerInventory, dankHandler,propertyDelegate);
		addOwnSlots(false);
		addPlayerSlots(playerInventory);
	}

	public static DockContainer dock1c(int id, PlayerInventory playerInventory) {
		return new DockContainer(DankStorage.Objects.dank_1_container, id, playerInventory);
	}

	public static DockContainer dock2c(int id, PlayerInventory playerInventory) {
		return new DockContainer(DankStorage.Objects.dank_2_container, id, playerInventory);
	}

	public static DockContainer dock3c(int id, PlayerInventory playerInventory) {
		return new DockContainer(DankStorage.Objects.dank_3_container, id, playerInventory);
	}

	public static DockContainer dock4c(int id, PlayerInventory playerInventory) {
		return new DockContainer(DankStorage.Objects.dank_4_container, id, playerInventory);
	}

	public static DockContainer dock5c(int id, PlayerInventory playerInventory) {
		return new DockContainer(DankStorage.Objects.dank_5_container, id, playerInventory);
	}

	public static DockContainer dock6c(int id, PlayerInventory playerInventory) {
		return new DockContainer(DankStorage.Objects.dank_6_container, id, playerInventory);
	}

	public static DockContainer dock7c(int id, PlayerInventory playerInventory) {
		return new DockContainer(DankStorage.Objects.dank_7_container, id, playerInventory);
	}

	///////////////////////////////////////////////////////////////////////////////////////////


	public static DockContainer dock1s(int id, PlayerInventory playerInventory,DankHandler handler, IIntArray iIntArray) {
		return new DockContainer(DankStorage.Objects.dank_1_container, id, playerInventory,handler,iIntArray);
	}

	public static DockContainer dock2s(int id, PlayerInventory playerInventory,DankHandler handler, IIntArray iIntArray) {
		return new DockContainer(DankStorage.Objects.dank_2_container, id, playerInventory,handler,iIntArray);
	}

	public static DockContainer dock3s(int id, PlayerInventory playerInventory,DankHandler handler, IIntArray iIntArray) {
		return new DockContainer(DankStorage.Objects.dank_3_container, id, playerInventory,handler,iIntArray);
	}

	public static DockContainer dock4s(int id, PlayerInventory playerInventory,DankHandler handler, IIntArray iIntArray) {
		return new DockContainer(DankStorage.Objects.dank_4_container, id, playerInventory,handler,iIntArray);
	}

	public static DockContainer dock5s(int id, PlayerInventory playerInventory,DankHandler handler, IIntArray iIntArray) {
		return new DockContainer(DankStorage.Objects.dank_5_container, id, playerInventory,handler,iIntArray);
	}

	public static DockContainer dock6s(int id, PlayerInventory playerInventory,DankHandler handler, IIntArray iIntArray) {
		return new DockContainer(DankStorage.Objects.dank_6_container, id, playerInventory,handler,iIntArray);
	}

	public static DockContainer dock7s(int id, PlayerInventory playerInventory,DankHandler handler, IIntArray iIntArray) {
		return new DockContainer(DankStorage.Objects.dank_7_container, id, playerInventory,handler,iIntArray);
	}

}

