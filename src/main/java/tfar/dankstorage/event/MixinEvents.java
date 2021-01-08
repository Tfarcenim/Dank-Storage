package tfar.dankstorage.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShootableItem;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.container.PortableDankContainer;
import tfar.dankstorage.ducks.UseDankStorage;
import tfar.dankstorage.inventory.DankHandler;
import tfar.dankstorage.inventory.PortableDankHandler;
import tfar.dankstorage.utils.Mode;
import tfar.dankstorage.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class MixinEvents {
	public static <T extends LivingEntity> void actuallyBreakItem(int p_222118_1_, T livingEntity, Consumer<T> p_222118_3_, CallbackInfo ci) {
		ItemStack actualStack = livingEntity.getHeldItemMainhand();
		if (actualStack.getItem() instanceof DankItem && Utils.isConstruction(actualStack)) {
			Utils.getHandler(actualStack).extractItem(Utils.getSelectedSlot(actualStack), 1, false);
		}
	}

	/**
	 * @param inv      Player Inventory to add the item to
	 * @param incoming the itemstack being picked up
	 * @return if the item was completely picked up by the dank(s)
	 */
	public static boolean interceptItem(PlayerInventory inv, ItemStack incoming) {
		PlayerEntity player = inv.player;
		if (player.openContainer instanceof PortableDankContainer) {
			return false;
		}
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack possibleDank = inv.getStackInSlot(i);
			if (possibleDank.getItem() instanceof DankItem && onItemPickup(player, incoming, possibleDank)) {
				return true;
			}
		}
		return false;
	}

	public static void shrinkAmmo(ItemStack bow, World worldIn, LivingEntity entityLiving, int timeLeft) {
		if (entityLiving instanceof PlayerEntity && !worldIn.isRemote) {
			PlayerEntity player = (PlayerEntity) entityLiving;
			Predicate<ItemStack> predicate = ((ShootableItem) bow.getItem()).getInventoryAmmoPredicate();
			if (((UseDankStorage) player).useDankStorage() && !player.abilities.isCreativeMode) {
				ItemStack dank = getDankStorage(player);
				if (!dank.isEmpty()) {
					DankHandler handler = Utils.getHandler(dank);
					for (int i = 0; i < handler.getSlots(); i++) {
						ItemStack stack = handler.getStackInSlot(i);
						if (predicate.test(stack) && stack.getItem() instanceof ArrowItem && !((ArrowItem)stack.getItem()).isInfinite(stack,bow,(PlayerEntity)entityLiving)) {
							handler.extractItem(i, 1, false);
							break;
						}
					}
				}
			}
		}
	}


	private static ItemStack getDankStorage(PlayerEntity player) {
		return IntStream.range(0, player.inventory.getSizeInventory()).mapToObj(player.inventory::getStackInSlot).filter(stack -> stack.getItem() instanceof DankItem).findFirst().orElse(ItemStack.EMPTY);
	}

	public static boolean onItemPickup(PlayerEntity player, ItemStack pickup, ItemStack dank) {

		Mode mode = Utils.getMode(dank);
		if (mode == Mode.normal) return false;
		PortableDankHandler inv = Utils.getHandler(dank);
		int count = pickup.getCount();
		boolean oredict = Utils.oredict(dank);
		List<ItemStack> existing = new ArrayList<>();
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) {

			} else {
				boolean exists = false;
				for (ItemStack stack1 : existing) {
					if (areItemStacksCompatible(stack, stack1, oredict)) {
						exists = true;
					}
				}
				if (!exists) {
					existing.add(stack.copy());
				}
			}
		}

		switch (mode) {
			case pickup_all: {
				for (int i = 0; i < inv.getSlots(); i++) {
					allPickup(inv, i, pickup, false, oredict);
				}
			}
			break;

			case filtered_pickup: {
				for (int i = 0; i < inv.getSlots(); i++) {
					filteredPickup(inv, i, pickup, false, oredict, existing);
				}
			}
			break;

			case void_pickup: {
				for (int i = 0; i < inv.getSlots(); i++) {
					voidPickup(inv, i, pickup, false, oredict, existing);
				}
			}
			break;
		}

		//leftovers
		pickup.setCount(pickup.getCount());
		if (pickup.getCount() != count) {
			dank.setAnimationsToGo(5);
			player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
			inv.save();
		}
		return pickup.isEmpty();
	}

	public static void voidPickup(PortableDankHandler inv, int slot, ItemStack toInsert, boolean simulate, boolean oredict, List<ItemStack> filter) {
		ItemStack existing = inv.getStackInSlot(slot);

		if (doesItemStackExist(toInsert, filter, oredict) && areItemStacksCompatible(existing, toInsert, oredict)) {
			int stackLimit = inv.stacklimit;
			int total = toInsert.getCount() + existing.getCount();
			//doesn't matter if it overflows cause it's all gone lmao
			if (!simulate) {
				inv.getContents().set(slot, ItemHandlerHelper.copyStackWithSize(existing, Math.min(total, stackLimit)));
				toInsert.setCount(0);
			}
		}
	}

	public static void allPickup(PortableDankHandler inv, int slot, ItemStack toInsert, boolean simulate, boolean oredict) {
		ItemStack existing = inv.getStackInSlot(slot);

		if (existing.isEmpty()) {
			int stackLimit = inv.stacklimit;
			int total = toInsert.getCount();
			int remainder = total - stackLimit;
			//no overflow
			if (remainder <= 0) {
				if (!simulate) inv.getContents().set(slot, toInsert.copy());
				toInsert.setCount(0);
			} else {
				if (!simulate) inv.getContents().set(slot, ItemHandlerHelper.copyStackWithSize(toInsert, stackLimit));
				toInsert.setCount(remainder);
			}
			return;
		}

		if (ItemHandlerHelper.canItemStacksStack(toInsert, existing) || (oredict && Utils.areItemStacksConvertible(toInsert, existing))) {
			int stackLimit = inv.stacklimit;
			int total = toInsert.getCount() + existing.getCount();
			int remainder = total - stackLimit;
			//no overflow
			if (remainder <= 0) {
				if (!simulate) inv.getContents().set(slot, ItemHandlerHelper.copyStackWithSize(existing, total));
				toInsert.setCount(0);
			} else {
				if (!simulate) inv.getContents().set(slot, ItemHandlerHelper.copyStackWithSize(toInsert, stackLimit));
				toInsert.setCount(remainder);
			}
		}
	}

	public static void filteredPickup(PortableDankHandler inv, int slot, ItemStack toInsert, boolean simulate, boolean oredict, List<ItemStack> filter) {
		ItemStack existing = inv.getStackInSlot(slot);

		if (existing.isEmpty() && doesItemStackExist(toInsert, filter, oredict)) {
			int stackLimit = inv.stacklimit;
			int total = toInsert.getCount();
			int remainder = total - stackLimit;
			//no overflow
			if (remainder <= 0) {
				if (!simulate) inv.getContents().set(slot, toInsert.copy());
				toInsert.setCount(0);
			} else {
				if (!simulate) inv.getContents().set(slot, ItemHandlerHelper.copyStackWithSize(toInsert, stackLimit));
				toInsert.setCount(remainder);
			}
			return;
		}

		if (doesItemStackExist(toInsert, filter, oredict) && areItemStacksCompatible(existing, toInsert, oredict)) {
			int stackLimit = inv.stacklimit;
			int total = toInsert.getCount() + existing.getCount();
			int remainder = total - stackLimit;
			//no overflow
			if (remainder <= 0) {
				if (!simulate) inv.getContents().set(slot, ItemHandlerHelper.copyStackWithSize(existing, total));
				toInsert.setCount(0);
			} else {
				if (!simulate) inv.getContents().set(slot, ItemHandlerHelper.copyStackWithSize(toInsert, stackLimit));
				toInsert.setCount(remainder);
			}
		}
	}

	public static boolean areItemStacksCompatible(ItemStack stackA, ItemStack stackB, boolean oredict) {
		return oredict ? ItemStack.areItemStackTagsEqual(stackA, stackB) && ItemStack.areItemsEqual(stackA, stackB) || Utils.areItemStacksConvertible(stackA, stackB) :
						ItemStack.areItemStackTagsEqual(stackA, stackB) && ItemStack.areItemsEqual(stackA, stackB);
	}

	public static boolean doesItemStackExist(ItemStack stack, List<ItemStack> filter, boolean oredict) {
		for (ItemStack filterStack : filter) {
			if (areItemStacksCompatible(stack, filterStack, oredict)) return true;
		}
		return false;
	}

    public static ItemStack findAmmo(PlayerEntity player, ItemStack bow) {
      if (bow.getItem() instanceof ShootableItem) {
        Predicate<ItemStack> predicate = ((ShootableItem) bow.getItem()).getInventoryAmmoPredicate();

        ItemStack dank = getDankStorage(player);

        return dank.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .map(iItemHandler -> IntStream.range(0, iItemHandler.getSlots())
                        .mapToObj(slot -> iItemHandler.extractItem(slot,1,true))
                        .filter(predicate).findFirst()).orElse(Optional.of(ItemStack.EMPTY)).orElse(ItemStack.EMPTY);
      }
      return ItemStack.EMPTY;
    }
}
