package tfar.dankstorage.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SimpleContainer.class)
public interface SimpleContainerAccess {

    @Accessor NonNullList<ItemStack> getItems();

    @Accessor @Mutable void setItems(NonNullList<ItemStack> stacks);

    @Accessor @Mutable void setSize(int slots);

}
