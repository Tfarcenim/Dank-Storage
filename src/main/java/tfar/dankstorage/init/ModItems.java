package tfar.dankstorage.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.event.RegistryEvent;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.block.DankDispenserBehavior;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.item.RedprintItem;
import tfar.dankstorage.item.UpgradeInfo;
import tfar.dankstorage.item.UpgradeItem;
import tfar.dankstorage.utils.DankStats;

import java.util.stream.IntStream;

import static tfar.dankstorage.DankStorage.register;

public class ModItems {
    public static void registerB(RegistryEvent.Register<Item> event) {
        Item.Properties properties = new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS);
        register(event.getRegistry(),( "dock"), new BlockItem(DankStorage.dock, properties));
        register(event.getRegistry(),( "red_print"), DankStorage.red_print = new RedprintItem(properties));

        properties.stacksTo(1);

        IntStream.range(1, 8).forEach(i -> {
            DankItem dankItem = new DankItem(properties, DankStats.values()[i]);
            DispenserBlock.registerBehavior(dankItem, new DankDispenserBehavior());
            register(event.getRegistry(),( "dank_" + i), dankItem);
        });
        IntStream.range(1, 7).forEach(i -> register(event.getRegistry(),( i + "_to_" + (i + 1)), new UpgradeItem(properties, new UpgradeInfo(i, i + 1))));
    }

}
