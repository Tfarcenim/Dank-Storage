package com.tfar.dankstorage.network;

import com.tfar.dankstorage.block.DankItemBlock;
import com.tfar.dankstorage.util.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;



public class CMessageChangeSlot implements IMessage {

  boolean right;

  public CMessageChangeSlot(){}

  public CMessageChangeSlot(boolean right){this.right = right;}

  /**
   * Convert from the supplied buffer into your specific message type
   *
   * @param buf
   */
  @Override
  public void fromBytes(ByteBuf buf) {
    this.right = buf.readBoolean();
  }

  /**
   * Deconstruct your message into the supplied byte buffer
   *
   * @param buf
   */
  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeBoolean(right);
  }

  public static class Handler implements IMessageHandler<CMessageChangeSlot, IMessage> {
    @Override
    public IMessage onMessage(CMessageChangeSlot message, MessageContext ctx) {
      FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
      return null;
    }

    private void handle(CMessageChangeSlot message, MessageContext ctx) {

      EntityPlayer player = ctx.getServerHandler().player;
      if (player == null) return;
      ItemStack bag = player.getHeldItemMainhand();
      if (bag.getItem() instanceof DankItemBlock) {
        boolean construction = Utils.construction(player.getHeldItemMainhand());
        if (construction){
          Utils.changeSlot(bag,message.right);
        }
      }
    }
  }
}

