package com.tfar.dankstorage.network;

import com.tfar.dankstorage.block.DankItemBlock;
import com.tfar.dankstorage.util.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public class CMessageToggle implements IMessage {

  public CMessageToggle() {}

  public static final KeybindToggleType[] keys = KeybindToggleType.values();
  KeybindToggleType key;

  public CMessageToggle(KeybindToggleType key){
    this.key = key;
  }

  /**
   * Convert from the supplied buffer into your specific message type
   *
   * @param buf
   */
  @Override
  public void fromBytes(ByteBuf buf) {
    this.key = keys[buf.readInt()];
  }

  /**
   * Deconstruct your message into the supplied byte buffer
   *
   * @param buf
   */
  @Override
  public void toBytes(ByteBuf buf) {buf.writeInt(key.ordinal());}

  public static class Handler implements IMessageHandler<CMessageToggle, IMessage> {
    @Override
    public IMessage onMessage(CMessageToggle message, MessageContext ctx) {
      FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message,ctx));
      return null;
    }

    private void handle(CMessageToggle mess,MessageContext ctx) {
      EntityPlayer player = ctx.getServerHandler().player;
      ItemStack bag = player.getHeldItemMainhand();
      if (!(bag.getItem() instanceof DankItemBlock)){
        bag = player.getHeldItemOffhand();
        if (!(bag.getItem() instanceof DankItemBlock))return;
      }
      Utils.toggle(bag,mess.key);
    }
  }

  public enum KeybindToggleType {
    PICKUP,VOID,CONSTRUCTION;
    KeybindToggleType(){}
  }
}

