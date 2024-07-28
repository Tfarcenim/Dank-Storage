package tfar.dankstorage.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import tfar.dankstorage.DankStorage;

public class ModCreativeTabs {

    public static final CreativeModeTab tab = CreativeModeTab.builder(null,-1)
                .icon(() -> new ItemStack(ModItems.DOCK))
            .title(Component.translatable("itemGroup."+ DankStorage.MODID))
            .displayItems((features, output) -> BuiltInRegistries.ITEM.stream().filter(item -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(DankStorage.MODID))
                    .forEach(output::accept)).build();


}
