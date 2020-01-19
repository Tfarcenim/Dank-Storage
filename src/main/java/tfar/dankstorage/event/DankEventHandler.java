package tfar.dankstorage.event;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tfar.dankstorage.DankBlock;
import tfar.dankstorage.DankItemBlock;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.container.AbstractPortableDankContainer;
import tfar.dankstorage.utils.Utils;

import static net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel;

@Mod.EventBusSubscriber(modid = DankStorage.MODID)
public class DankEventHandler {

  @SubscribeEvent
  public static void handleEntityItemPickup(EntityItemPickupEvent event) {
    PlayerEntity player = event.getPlayer();
    if (player.openContainer instanceof AbstractPortableDankContainer) {
      return;
    }
    PlayerInventory inventory = player.inventory;
    for (int i = 0; i < inventory.getSizeInventory(); i++) {
      ItemStack stack = inventory.getStackInSlot(i);
      if (stack.getItem() instanceof DankItemBlock && DankBlock.onItemPickup(event, stack)) {
        event.setCanceled(true);
        return;
      }
    }
  }

  @SubscribeEvent
  public static void harvestcheck(PlayerEvent.HarvestCheck e){
    PlayerEntity player = e.getPlayer();
    BlockState state = e.getTargetBlock();
    ItemStack dank = player.getHeldItemMainhand();
    if (!Utils.isConstruction(dank))return;
    ItemStack tool = Utils.getItemStackInSelectedSlot(dank);
    e.setCanHarvest(tool.canHarvestBlock(state));
  }

  @SubscribeEvent
  public static void breakspeed(PlayerEvent.BreakSpeed e){
    float oldspeed = e.getOriginalSpeed();
    PlayerEntity player = e.getPlayer();
    ItemStack dank = player.getHeldItemMainhand();
    if (!Utils.isConstruction(dank))return;
    ItemStack tool = Utils.getItemStackInSelectedSlot(dank);
    int i = getMaxEfficiencyLevel(tool);
    if (i > 0) e.setNewSpeed(oldspeed + i * i + 1);
  }

  public static int getMaxEfficiencyLevel(ItemStack stack) {
    return getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
  }

  @SubscribeEvent
  public static void wrench(PlayerInteractEvent.RightClickBlock event) {
    BlockPos pos = event.getPos();
    BlockState state = event.getWorld().getBlockState(pos);
    if (!(state.getBlock() instanceof DankBlock)) return;
    PlayerEntity player = event.getPlayer();
    if (!player.getHeldItem(event.getHand()).getItem().isIn(Utils.WRENCHES)) return;
    event.setCanceled(true);
    event.getWorld().getBlockState(pos).getBlock().func_225533_a_(state,event.getWorld(),pos, player, event.getHand(),null);
  }
}
