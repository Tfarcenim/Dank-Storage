package com.tfar.dankstorage.event;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.block.DankItemBlock;
import com.tfar.dankstorage.block.DankBlock;
import com.tfar.dankstorage.container.AbstractPortableDankContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DankStorage.MODID)
public class DankEventHandler {

  @SubscribeEvent
  public static void handleEntityItemPickup(EntityItemPickupEvent event) {
    EntityPlayer player = event.getEntityPlayer();
    if (player.openContainer instanceof AbstractPortableDankContainer) {
      return;
    }
    InventoryPlayer inventory = player.inventory;
    for (int i = 0; i < inventory.getSizeInventory(); i++) {
      ItemStack stack = inventory.getStackInSlot(i);
      if (stack.getItem() instanceof DankItemBlock && DankBlock.onItemPickup(event, stack)) {
        event.setCanceled(true);
        return;
      }
    }
  }

}
