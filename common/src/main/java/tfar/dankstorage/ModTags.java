package tfar.dankstorage;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import tfar.dankstorage.utils.CommonUtils;

public class ModTags {

   // public static final Tags.IOptionalNamedTag<Item> MEKANISM_CABLES = ItemTags.createOptional(new ResourceLocation("dankstorage","mekanism_cables"));
    public static final TagKey<Item> WRENCHES = bind(new ResourceLocation("forge", "wrenches"));
    public static final TagKey<Item> UNSTACKABLE = bind(new ResourceLocation(DankStorage.MODID,"unstackable"));
    public static final TagKey<Item> BLACKLISTED_STORAGE = bind(new ResourceLocation(DankStorage.MODID, "blacklisted_storage"));
    public static final TagKey<Item> BLACKLISTED_USAGE = bind(new ResourceLocation(DankStorage.MODID, "blacklisted_usage"));

    private static TagKey<Item> bind(ResourceLocation $$0) {
        return TagKey.create(Registries.ITEM, $$0);
    }
}
