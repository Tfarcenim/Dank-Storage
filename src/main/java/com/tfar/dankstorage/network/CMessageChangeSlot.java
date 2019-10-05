package com.tfar.dankstorage.network;

import com.tfar.dankstorage.block.DankItemBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;


public class CMessageChangeSlot {

  boolean right;

  public CMessageChangeSlot(){}

  public CMessageChangeSlot(boolean right){ this.right = right;}

  //decode
  public CMessageChangeSlot(PacketBuffer buf) {
    this.right = buf.readBoolean();
  }

  public void encode(PacketBuffer buf) {
    buf.writeBoolean(right);
  }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
      PlayerEntity player = ctx.get().getSender();

      if (player == null) return;

      ctx.get().enqueueWork(  ()->  {
        ItemStack bag = player.getHeldItemMainhand();
        if (bag.getItem() instanceof DankItemBlock) {
          boolean construction = Utils.isConstruction(player.getHeldItemMainhand());
          if (construction){
            Utils.changeSlot(bag,right);
          }
        }
      });
      ctx.get().setPacketHandled(true);
    }
}

