package tfar.dankstorage.utils;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Locale;

public class PacketBufferEX {

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStack> LARGE_OPTIONAL_STREAM_CODEC = new StreamCodec<>() {
        private static final StreamCodec<RegistryFriendlyByteBuf, Holder<Item>> ITEM_STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.ITEM);

        public ItemStack decode(RegistryFriendlyByteBuf buf) {
            int i = buf.readInt();
            if (i <= 0) {
                return ItemStack.EMPTY;
            } else {
                Holder<Item> holder = ITEM_STREAM_CODEC.decode(buf);
                DataComponentPatch datacomponentpatch = DataComponentPatch.STREAM_CODEC.decode(buf);
                return new ItemStack(holder, i, datacomponentpatch);
            }
        }

        public void encode(RegistryFriendlyByteBuf buf, ItemStack stack) {
            if (stack.isEmpty()) {
                buf.writeInt(0);
            } else {
                buf.writeInt(stack.getCount());
                ITEM_STREAM_CODEC.encode(buf, stack.getItemHolder());
                DataComponentPatch.STREAM_CODEC.encode(buf, ((PatchedDataComponentMap) stack.getComponents()).asPatch());
            }
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, List<ItemStack>> LARGE_OPTIONAL_LIST_STREAM_CODEC = LARGE_OPTIONAL_STREAM_CODEC.apply(
            ByteBufCodecs.collection(NonNullList::createWithCapacity)
    );

    public static void writeExtendedItemStack(RegistryFriendlyByteBuf buf, ItemStack stack) {
        LARGE_OPTIONAL_STREAM_CODEC.encode(buf,stack);
    }

    public static ItemStack readExtendedItemStack(RegistryFriendlyByteBuf buf) {
        return LARGE_OPTIONAL_STREAM_CODEC.decode(buf);
    }

    public static void writeList(RegistryFriendlyByteBuf buf, List<ItemStack> stacks) {
        LARGE_OPTIONAL_LIST_STREAM_CODEC.encode(buf,stacks);
    }

    public static List<ItemStack> readList(RegistryFriendlyByteBuf buf) {
        return LARGE_OPTIONAL_LIST_STREAM_CODEC.decode(buf);
    }


    public static <B extends FriendlyByteBuf, V extends Enum<V>> StreamCodec<B, V> enumCodec(final Class<V> enumClass) {
        return new StreamCodec<>() {
            public V decode(B buf) {
                return buf.readEnum(enumClass);
            }

            public void encode(B buf, V value) {
                buf.writeEnum(value);
            }
        };
    }
}

