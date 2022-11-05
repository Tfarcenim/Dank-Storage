package tfar.dankstorage.init;

import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.registries.RegisterEvent;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.block.DankDispenserBehavior;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.item.RedprintItem;
import tfar.dankstorage.item.UpgradeInfo;
import tfar.dankstorage.item.UpgradeItem;
import tfar.dankstorage.utils.DankStats;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ModItems {
    static Item.Properties properties = new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS);
    public static Item red_print = new RedprintItem(properties);
    public static final Item DOCK = new BlockItem(ModBlocks.dock, properties);
    public static final List<Item> DANKS;
    public static final List<Item> UPGRADES;

    static {
        DANKS = IntStream.range(1, 8).mapToObj(i -> {
            DankItem dankItem = new DankItem(properties, DankStats.values()[i]);
            DispenserBlock.registerBehavior(dankItem, new DankDispenserBehavior());
            return dankItem;
        }).collect(Collectors.toList());

        UPGRADES = IntStream.range(1, DANKS.size()).mapToObj(i -> new UpgradeItem(properties, new UpgradeInfo(i, i + 1))).collect(Collectors.toList());
    }

    public static void registerB(RegisterEvent event) {
        DankStorage.register(event, Registry.ITEM_REGISTRY,"dock", DOCK);
        DankStorage.register(event,Registry.ITEM_REGISTRY,"red_print", red_print);

        for (int i = 0; i < DANKS.size();i++) {
            DankStorage.register(event,Registry.ITEM_REGISTRY,"dank_"+(i+1),DANKS.get(i));
        }

        for (int i = 0; i < UPGRADES.size();i++) {
            DankStorage.register(event,Registry.ITEM_REGISTRY,(i+1)+"_to_"+(i+2),UPGRADES.get(i));
        }
    }
}
