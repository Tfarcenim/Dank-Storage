package tfar.dankstorage.inventory;

import net.minecraft.world.inventory.ContainerData;

public class LimitedContainerData implements ContainerData {

    private final ContainerData wrapped;
    private final int max;

    public LimitedContainerData(ContainerData wrapped, int max) {
        this.wrapped = wrapped;

        this.max = max;
    }

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

    public ContainerData getWrapped() {
        return wrapped;
    }
}
