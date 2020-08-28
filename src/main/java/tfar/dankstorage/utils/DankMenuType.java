package tfar.dankstorage.utils;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

public class DankMenuType<T extends Container> extends ContainerType<T> {

	public final DankStats stats;

	public DankMenuType(ContainerType.IFactory<T> factory,DankStats stats) {
		super(factory);
		this.stats = stats;
	}
}
