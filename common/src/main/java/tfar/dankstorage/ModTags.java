package tfar.dankstorage;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {

   // public static final Tags.IOptionalNamedTag<Item> MEKANISM_CABLES = ItemTags.createOptional(new ResourceLocation("dankstorage","mekanism_cables"));
    public static final TagKey<Item> WRENCHES = bind(Identifier.fromNamespaceAndPath("c", "wrenches"));
    public static final TagKey<Item> UNSTACKABLE = bind(DankStorage.id("unstackable"));
    public static final TagKey<Item> BLACKLISTED_STORAGE = bind(DankStorage.id("blacklisted_storage"));
    public static final TagKey<Item> BLACKLISTED_USAGE = bind(DankStorage.id("blacklisted_usage"));

    private static TagKey<Item> bind(Identifier $$0) {
        return TagKey.create(Registries.ITEM, $$0);
    }
}
