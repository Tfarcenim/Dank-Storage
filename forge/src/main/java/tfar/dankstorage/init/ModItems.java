package tfar.dankstorage.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.registries.RegisterEvent;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.DankStorageForge;
import tfar.dankstorage.block.DankDispenserBehavior;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.item.RedprintItem;
import tfar.dankstorage.utils.UpgradeInfo;
import tfar.dankstorage.item.UpgradeItem;
import tfar.dankstorage.utils.DankStats;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ModItems {
    static Item.Properties properties = new Item.Properties();
    public static Item red_print = new RedprintItem(properties);
    public static final Item DOCK = new BlockItem(ModBlocks.dock, properties);
    public static final List<Item> DANKS;
    public static final List<Item> UPGRADES;

    public static CreativeModeTab tab;

    static {
        DANKS = IntStream.range(1, 8).mapToObj(i -> {
            DankItem dankItem = new DankItem(properties, DankStats.values()[i]);
            DispenserBlock.registerBehavior(dankItem, new DankDispenserBehavior());
            return dankItem;
        }).collect(Collectors.toList());

        UPGRADES = IntStream.range(1, DANKS.size()).mapToObj(i -> new UpgradeItem(properties, new UpgradeInfo(i, i + 1))).collect(Collectors.toList());

        tab = CreativeModeTab.builder()
                .icon(() -> new ItemStack(DOCK))
                .title(Component.translatable("itemGroup."+ DankStorage.MODID))
                .displayItems((features, output) -> {
                    DANKS.forEach(output::accept);
                    UPGRADES.forEach(output::accept);
                    output.accept(DOCK);
                    output.accept(red_print);
                }).build();
    }

    public static void registerB(RegisterEvent event) {
        DankStorageForge.register(event, Registries.ITEM,"dock", DOCK);
        DankStorageForge.register(event,Registries.ITEM,"red_print", red_print);

        for (int i = 0; i < DANKS.size();i++) {
            DankStorageForge.register(event,Registries.ITEM,"dank_"+(i+1),DANKS.get(i));
        }

        for (int i = 0; i < UPGRADES.size();i++) {
            DankStorageForge.register(event,Registries.ITEM,(i+1)+"_to_"+(i+2),UPGRADES.get(i));
        }
    }
}
