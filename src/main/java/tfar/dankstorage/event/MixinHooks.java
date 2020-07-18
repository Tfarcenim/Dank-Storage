package tfar.dankstorage.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.dankstorage.DankItem;
import tfar.dankstorage.block.DockBlock;
import tfar.dankstorage.container.AbstractPortableDankContainer;
import tfar.dankstorage.inventory.PortableDankHandler;
import tfar.dankstorage.utils.Mode;
import tfar.dankstorage.utils.Utils;

import java.util.ArrayList;
import java.util.List;
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
			if (possibleDank.getItem() instanceof DankItem && onItemPickup(player,incoming, possibleDank)) {
				return true;
			}
		}
		return false;
	}

	public static boolean onItemPickup(PlayerEntity player, ItemStack pickup,ItemStack dank) {

		Mode mode = Utils.getMode(dank);
		if (mode == Mode.NORMAL)return false;
		PortableDankHandler inv = Utils.getHandler(dank);
		int count = pickup.getCount();
		ItemStack rem = pickup.copy();
		boolean oredict = Utils.oredict(dank);

		//stack with existing items
		List<Integer> emptyslots = new ArrayList<>();
		for (int i = 0; i < inv.getSlots(); i++){
			if (inv.getStackInSlot(i).isEmpty()){
				emptyslots.add(i);
				continue;
			}
			rem = DockBlock.insertIntoHandler(mode,inv,i,rem,false,oredict);
			if (rem.isEmpty())break;
		}
		//only iterate empty slots
		if (!rem.isEmpty())
			for (int slot : emptyslots) {
				rem = DockBlock.insertIntoHandler(mode,inv,slot,rem,false,oredict);
				if (rem.isEmpty())break;
			}
		//leftovers
		pickup.setCount(rem.getCount());
		if (rem.getCount() != count) {
			dank.setAnimationsToGo(5);
			player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
			inv.writeItemStack();
		}
		return pickup.isEmpty();
	}
}
