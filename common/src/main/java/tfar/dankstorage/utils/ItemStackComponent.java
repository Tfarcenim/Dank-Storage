package tfar.dankstorage.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public record ItemStackComponent(ItemStack itemStack) {
    public static final ItemStackComponent EMPTY = new ItemStackComponent(ItemStack.EMPTY);

    public static final Codec<ItemStackComponent> CODEC = RecordCodecBuilder
            .create(itemStackComponentInstance -> itemStackComponentInstance.group(SerializationHelper.LARGE_CODEC.fieldOf("selected")
                    .forGetter(ItemStackComponent::itemStack)).apply(itemStackComponentInstance,ItemStackComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackComponent> STREAM_CODEC = StreamCodec.composite(
            SerializationHelper.LARGE_OPTIONAL_STREAM_CODEC,
            ItemStackComponent::itemStack,
            ItemStackComponent::new
    );

    @Override
    public int hashCode() {
        return Objects.hash(itemStack.getItem(),itemStack.getCount(),itemStack.getComponents());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ItemStackComponent itemStackComponent)) return false;
        if (this == obj) return true;
        return ItemStack.matches(itemStack,itemStackComponent.itemStack);
    }
}
