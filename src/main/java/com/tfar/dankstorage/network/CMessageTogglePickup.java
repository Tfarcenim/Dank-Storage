package com.tfar.dankstorage.network;

import com.tfar.dankstorage.block.DankItemBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;


public class CMessageTogglePickup {

  public CMessageTogglePickup(){}

  public static final Mode[] modes = Mode.values();

    public void handle(Supplier<NetworkEvent.Context> ctx) {
      PlayerEntity player = ctx.get().getSender();

      if (player == null) return;

      ctx.get().enqueueWork(  ()->  {
        ItemStack bag = player.getHeldItemMainhand();
        if (!(bag.getItem() instanceof DankItemBlock)){
          bag = player.getHeldItemOffhand();
          if (!(bag.getItem() instanceof DankItemBlock))return;
        }
          Utils.cycleMode(bag,player);
      });
      ctx.get().setPacketHandled(true);
    }
public enum Mode {
    NORMAL,PICKUP_ALL,FILTERED_PICKUP,VOID_PICKUP
  }
}

