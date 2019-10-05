package com.tfar.dankstorage.network;

import com.tfar.dankstorage.container.AbstractAbstractDankContainer;
import com.tfar.dankstorage.container.AbstractDankContainer;
import com.tfar.dankstorage.container.AbstractPortableDankContainer;
import com.tfar.dankstorage.inventory.DankHandler;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;


public class CMessageSort {

  public CMessageSort() {
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    PlayerEntity player = ctx.get().getSender();

    if (player == null) return;

    ctx.get().enqueueWork(() -> {
      Container openContainer = player.openContainer;
      if (openContainer instanceof AbstractAbstractDankContainer) {
        List<SortingData> itemlist = new ArrayList<>();
        DankHandler handler;
        if (openContainer instanceof AbstractDankContainer) {
          handler = ((AbstractDankContainer) openContainer).te.itemHandler;
        } else handler = ((AbstractPortableDankContainer)openContainer).handler;

        for (int i = 0; i < handler.getSlots(); i++) {
          ItemStack stack = handler.getStackInSlot(i);
          if (stack.isEmpty()) continue;
          boolean exists = SortingData.exists(itemlist, stack.copy());
          if (exists) {
            int rem = SortingData.addToList(itemlist, stack.copy());
            if (rem > 0) {
              ItemStack bigstack = stack.copy();
              bigstack.setCount(Integer.MAX_VALUE);
              ItemStack smallstack = stack.copy();
              smallstack.setCount(rem);
              itemlist.add(new SortingData(bigstack));
              itemlist.add(new SortingData(smallstack));
            }
          } else {
            itemlist.add(new SortingData(stack.copy()));
          }
        }
        handler.clear();
        Collections.sort(itemlist);
        for (SortingData data : itemlist) {
          ItemStack stack = data.stack.copy();
          ItemStack rem = stack.copy();
          for (int i = 0; i < handler.getSlots(); i++) {
            rem = handler.insertItem(i, rem, false);
            if (rem.isEmpty()) break;
          }
        }
      }
    });
  }
}