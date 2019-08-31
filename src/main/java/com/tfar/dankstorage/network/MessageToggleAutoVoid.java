package com.tfar.dankstorage.network;

import com.tfar.dankstorage.block.DankItemBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;


public class MessageToggleAutoVoid {

  public MessageToggleAutoVoid(){}

  public void encode(PacketBuffer buf) {
  }

  public static MessageToggleAutoVoid decode(PacketBuffer buffer) {
    return new MessageToggleAutoVoid();
  }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
      PlayerEntity player = ctx.get().getSender();

      if (player == null) return;

      ctx.get().enqueueWork(  ()->  {
        if (player.getHeldItemMainhand().getItem() instanceof DankItemBlock) {
          boolean toggle = Utils.autoVoid(player.getHeldItemMainhand());
          player.sendStatusMessage(new TranslationTextComponent("dankstorage.void." + (toggle ? "disabled" : "enabled")),false);
          player.getHeldItemMainhand().getOrCreateTag().putBoolean("void",!toggle);
        }
      });
      ctx.get().setPacketHandled(true);
    }


}

