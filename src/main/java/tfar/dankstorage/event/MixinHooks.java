package tfar.dankstorage.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.dankstorage.DankItem;
import tfar.dankstorage.block.DockBlock;
import tfar.dankstorage.container.AbstractPortableDankContainer;
import tfar.dankstorage.utils.Utils;

import java.util.function.Consumer;

public class MixinHooks {
	public static <T extends LivingEntity> void actuallyBreakItem(int p_222118_1_, T livingEntity, Consumer<T> p_222118_3_, CallbackInfo ci) {
		ItemStack actualStack = livingEntity.getHeldItemMainhand();
		if (actualStack.getItem() instanceof DankItem && Utils.isConstruction(actualStack)) {
			Utils.getHandler(actualStack).extractItem(Utils.getSelectedSlot(actualStack),1,false);
		}
	}

	/**
	 * @param inv Player Inventory to add the item to
	 * @param incoming the itemstack being picked up
	 * @return if the item was completely picked up by the dank(s)
	 */
	public static boolean interceptItem(PlayerInventory inv,ItemStack incoming) {
		PlayerEntity player = inv.player;
		if (player.openContainer instanceof AbstractPortableDankContainer) {
			return false;
		}
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack possibleDank = inv.getStackInSlot(i);
			if (possibleDank.getItem() instanceof DankItem && DockBlock.onItemPickup(player,incoming, possibleDank)) {
				return true;
			}
		}
		return false;
	}
}
