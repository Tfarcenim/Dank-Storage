package com.tfar.dankstorage.network;

import com.tfar.dankstorage.block.DankItemBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;


public class MessageToggleConstruction {

  public MessageToggleConstruction(){}

  public void encode(PacketBuffer buf) {
  }

  public static MessageToggleConstruction decode(PacketBuffer buffer) {
    return new MessageToggleConstruction();
  }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
      EntityPlayer player = ctx.get().getSender();

      if (player == null) return;

      ctx.get().enqueueWork(  ()->  {
        if (player.getHeldItemMainhand().getItem() instanceof DankItemBlock) {
          boolean toggle = Utils.construction(player.getHeldItemMainhand());
          player.sendStatusMessage(new TextComponentTranslation("dankstorage.construction." + (toggle ? "disabled" : "enabled")),true);
          player.getHeldItemMainhand().getOrCreateTag().putBoolean("construction",!toggle);
        }
      });
      ctx.get().setPacketHandled(true);
    }
}

