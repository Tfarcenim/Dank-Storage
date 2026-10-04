package tfar.dankstorage.inventory;

import net.minecraft.world.inventory.ContainerData;

public record LimitedContainerData(ContainerData wrapped, int max) implements ContainerData {

    @Override
    public int get(int index) {
        return wrapped.get(index);
    }

    @Override
    public void set(int index, int value) {
        wrapped.set(index, value);
    }

    @Override
    public int getCount() {
        return max;
    }
}
