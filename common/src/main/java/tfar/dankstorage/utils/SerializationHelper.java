package tfar.dankstorage.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.DankStorage;

import java.util.List;

public class SerializationHelper {

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


    public static final Codec<ItemStack> LARGE_CODEC = RecordCodecBuilder.create(
                    p_347288_ -> p_347288_.group(
                                    ItemStack.ITEM_NON_AIR_CODEC.fieldOf("id").forGetter(ItemStack::getItemHolder),
                                    ExtraCodecs.intRange(1, Integer.MAX_VALUE).fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
                                    DataComponentPatch.CODEC
                                            .optionalFieldOf("components", DataComponentPatch.EMPTY)
                                            .forGetter(stack -> ((PatchedDataComponentMap) stack.getComponents()).asPatch())
                            )
                            .apply(p_347288_, ItemStack::new));

    public static Tag encodeLargeStack(ItemStack stack, HolderLookup.Provider provider, CompoundTag tag) {
        return LARGE_CODEC.encode(stack, provider.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow();
    }

    public static ItemStack decodeLargeItemStack(HolderLookup.Provider provider, CompoundTag tag) {
        if (tag.isEmpty()) return ItemStack.EMPTY;
        return LARGE_CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), tag)
                .resultOrPartial(string -> DankStorage.LOG.error("Tried to load invalid item: '{}'", string)).orElse(ItemStack.EMPTY);
    }

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
        List<ItemStack> decode = LARGE_OPTIONAL_LIST_STREAM_CODEC.decode(buf);
        return decode;
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

