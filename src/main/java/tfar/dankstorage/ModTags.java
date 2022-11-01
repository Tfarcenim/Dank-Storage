package tfar.dankstorage;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {

   // public static final Tags.IOptionalNamedTag<Item> MEKANISM_CABLES = ItemTags.createOptional(new ResourceLocation("dankstorage","mekanism_cables"));

    public static final TagKey<Item> BLACKLISTED_STORAGE = ItemTags.create(new ResourceLocation(DankStorage.MODID, "blacklisted_storage"));
    public static final TagKey<Item> BLACKLISTED_USAGE = ItemTags.create(new ResourceLocation(DankStorage.MODID, "blacklisted_usage"));
    public static final TagKey<Item> WRENCHES = ItemTags.create(new ResourceLocation("forge", "wrenches"));
}
