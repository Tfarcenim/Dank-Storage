package com.tfar.dankstorage.event;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.block.DankItemBlock;
import com.tfar.dankstorage.block.DankBlock;
import com.tfar.dankstorage.container.AbstractPortableDankContainer;
import com.tfar.dankstorage.utils.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
