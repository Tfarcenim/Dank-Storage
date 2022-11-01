package tfar.dankstorage.client;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class DankTooltip implements TooltipComponent {
    private final NonNullList<ItemStack> items;
    private final int selected;

    public DankTooltip(NonNullList<ItemStack> nonNullList, int selected) {
        this.items = nonNullList;
        this.selected = selected;
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    public int getSelected() {
        return selected;
    }
}
