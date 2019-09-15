package com.tfar.dankstorage.network;

import com.tfar.dankstorage.block.DankItemBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;


public class CMessageTagMode {

  public CMessageTagMode(){}



    public void handle(Supplier<NetworkEvent.Context> ctx) {
      PlayerEntity player = ctx.get().getSender();

      if (player == null) return;

      ctx.get().enqueueWork(() ->  {
        if (player.getHeldItemMainhand().getItem() instanceof DankItemBlock) {
          boolean toggle = Utils.tag(player.getHeldItemMainhand());
          player.getHeldItemMainhand().getOrCreateTag().putBoolean("tag",!toggle);
        }
      });
      ctx.get().setPacketHandled(true);
    }

}

