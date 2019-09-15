package com.tfar.dankstorage.event;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.block.DankItemBlock;
import com.tfar.dankstorage.block.DankBlock;
import com.tfar.dankstorage.container.AbstractPortableDankContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
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

}
