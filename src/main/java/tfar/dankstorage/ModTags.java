package tfar.dankstorage;

import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class ModTags {

    public static final Tags.IOptionalNamedTag<Item> MEKANISM_CABLES = ItemTags.createOptional(new ResourceLocation("dankstorage","mekanism_cables"));

    public static final ITag.INamedTag<Item> BLACKLISTED_STORAGE = ItemTags.makeWrapperTag(new ResourceLocation(DankStorage.MODID, "blacklisted_storage").toString());
    public static final ITag.INamedTag<Item> BLACKLISTED_USAGE = ItemTags.makeWrapperTag(new ResourceLocation(DankStorage.MODID, "blacklisted_usage").toString());
    public static final ITag.INamedTag<Item> WRENCHES = ItemTags.makeWrapperTag(new ResourceLocation("forge", "wrenches").toString());
}
