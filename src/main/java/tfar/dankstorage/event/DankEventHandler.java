package tfar.dankstorage.event;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.utils.Utils;

import static net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel;

@Mod.EventBusSubscriber(modid = DankStorage.MODID)
public class DankEventHandler {

  @SubscribeEvent
  public static void harvestcheck(PlayerEvent.HarvestCheck e){
    BlockState state = e.getTargetBlock();
    if (!state.getRequiresTool()) {
      return;
    }
    PlayerEntity player = e.getPlayer();
    ItemStack dank = player.getHeldItemMainhand();
    if (!Utils.isConstruction(dank))return;
    ItemStack tool = Utils.getItemStackInSelectedSlot(dank);
    if (!tool.isEmpty()) {
      e.setCanHarvest(tool.canHarvestBlock(state));
    }
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
}
