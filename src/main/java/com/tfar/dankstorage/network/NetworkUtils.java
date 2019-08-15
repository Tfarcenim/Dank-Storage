package com.tfar.dankstorage.network;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.EncoderException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nullable;
import java.io.IOException;

public class NetworkUtils {

  public static void writeNBT(PacketBuffer buf, @Nullable CompoundNBT nbt) {
    if (nbt == null) {
      buf.writeByte(0);
    } else {
      try {
        CompressedStreamTools.write(nbt, new ByteBufOutputStream(buf));
      } catch (IOException ioexception) {
        throw new EncoderException(ioexception);
      }
    }
  }

  public static CompoundNBT readNBT(PacketBuffer buf) {
    int i = buf.readerIndex();
    byte b0 = buf.readByte();

    if (b0 == 0) {
      return null;
    } else {
      buf.readerIndex(i);
      try {
        return CompressedStreamTools.read(new ByteBufInputStream(buf), new NBTSizeTracker(2097152L));
      } catch (IOException ioexception) {
        throw new EncoderException(ioexception);
      }
    }
  }

  public static void writeExtendedItemStack(PacketBuffer buf, ItemStack stack) {
    if (stack.isEmpty()) {
      buf.writeInt(-1);
    } else {
      buf.writeInt(Item.getIdFromItem(stack.getItem()));
      buf.writeInt(stack.getCount());
      CompoundNBT CompoundNBT = null;

      if (stack.getItem().isDamageable()) {
        CompoundNBT = stack.getItem().getShareTag(stack);
      }

      writeNBT(buf, CompoundNBT);
    }
  }

  public static ItemStack readExtendedItemStack(PacketBuffer buf) {
    int i = buf.readInt();

    if (i < 0) {
      return ItemStack.EMPTY;
    } else {
      int count = buf.readInt();
      ItemStack itemstack = new ItemStack(Item.getItemById(i),count);
      itemstack.setTag(readNBT(buf));
      return itemstack;
    }
  }

}
