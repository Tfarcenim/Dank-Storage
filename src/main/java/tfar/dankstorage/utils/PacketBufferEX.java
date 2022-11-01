package tfar.dankstorage.utils;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.EncoderException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PacketBufferEX {

    public static void writeExtendedItemStack(FriendlyByteBuf buf, ItemStack stack) {
        if (stack.isEmpty()) {
            buf.writeInt(-1);
        } else {
            buf.writeInt(Item.getId(stack.getItem()));
            buf.writeInt(stack.getCount());

            CompoundTag nbttagcompound = stack.getTag();

            writeNBT(buf, nbttagcompound);
        }
    }

    public static void writeNBT(FriendlyByteBuf buf, @Nullable CompoundTag nbt) {
        if (nbt == null) {
            buf.writeByte(0);
        } else {
            try {
                NbtIo.write(nbt, new ByteBufOutputStream(buf));
            } catch (IOException ioexception) {
                throw new EncoderException(ioexception);
            }
        }
    }

    public static ItemStack readExtendedItemStack(FriendlyByteBuf buf) {
        int i = buf.readInt();

        if (i < 0) {
            return ItemStack.EMPTY;
        } else {
            int j = buf.readInt();
            ItemStack itemstack = new ItemStack(Item.byId(i), j);
            itemstack.setTag(readNBT(buf));
            return itemstack;
        }
    }

    static final long LIMIT = 2097152L * 4;

    public static CompoundTag readNBT(FriendlyByteBuf buf) {
        int i = buf.readerIndex();
        byte b0 = buf.readByte();

        if (b0 == 0) {
            return null;
        } else {
            buf.readerIndex(i);
            try {
                return NbtIo.read(new ByteBufInputStream(buf), new NbtAccounter(LIMIT));
            } catch (IOException ioexception) {
                throw new EncoderException(ioexception);
            }
        }
    }

    public static void writeList(FriendlyByteBuf buf, List<ItemStack> stacks) {
        buf.writeInt(stacks.size());
        for (int i = 0; i < stacks.size();i++) {
            writeExtendedItemStack(buf,stacks.get(i));
        }
    }

    public static List<ItemStack> readList(FriendlyByteBuf buf) {
        List<ItemStack> stacks = new ArrayList<>();
        int size = buf.readInt();
        for (int i = 0; i < size;i++) {
            ItemStack stack = readExtendedItemStack(buf);
            stacks.add(stack);
        }
        return stacks;
    }
}

