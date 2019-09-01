package com.tfar.dankstorage.network;

import com.tfar.dankstorage.block.DankItemBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;


public class MessageToggleAutoPickup {

  public MessageToggleAutoPickup(){}

  public void encode(PacketBuffer buf) {
  }

  public static MessageToggleAutoPickup decode(PacketBuffer buffer) {
    return new MessageToggleAutoPickup();
  }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
      PlayerEntity player = ctx.get().getSender();

      if (player == null) return;

      ctx.get().enqueueWork(  ()->  {
        if (player.getHeldItemMainhand().getItem() instanceof DankItemBlock) {
          boolean toggle = Utils.autoPickup(player.getHeldItemMainhand());
          player.sendStatusMessage(new TranslationTextComponent("dankstorage.pickup." + (toggle ? "disabled" : "enabled")),true);
          player.getHeldItemMainhand().getOrCreateTag().putBoolean("pickup",!toggle);
        }
      });
      ctx.get().setPacketHandled(true);
    }


}

