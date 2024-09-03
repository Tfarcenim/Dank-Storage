package tfar.dankstorage.init;

import com.google.gson.internal.LinkedTreeMap;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;
import tfar.dankstorage.block.DankDispenserBehavior;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.item.RedprintItem;
import tfar.dankstorage.item.UpgradeItem;
import tfar.dankstorage.utils.UpgradeInfo;
import tfar.dankstorage.utils.DankStats;

import java.util.Map;
import java.util.stream.IntStream;

public class ModItems {
    static Item.Properties properties = new Item.Properties();
    static Item.Properties dank_properties = new Item.Properties();
    public static Item RED_PRINT = new RedprintItem(properties);
    public static final Item DOCK = new BlockItem(ModBlocks.dock, properties);
    public static final Map<String, DankItem> DANKS;
    public static final Map<String, UpgradeItem> UPGRADES;
    public static final Map<String,Item> ALL = new LinkedTreeMap<>();


    static {
        DANKS = new LinkedTreeMap<>();
        IntStream.range(1, 8).forEach(i -> {
            String s = "dank_" + i;
            DankItem dankItem = new DankItem(dank_properties, DankStats.values()[i]);
            DispenserBlock.registerBehavior(dankItem, new DankDispenserBehavior());
            DANKS.put(s, dankItem);
        });

        UPGRADES = new LinkedTreeMap<>();
        int bound = DANKS.size();
        for (int i = 1; i < bound; i++) {
            String s = i+"_to_"+(i+1);
            UpgradeItem upgradeItem = new UpgradeItem(properties, new UpgradeInfo(i, i + 1));
            UPGRADES.put(s,upgradeItem);
        }
    }

    public static Map<String,Item> getAll() {
        if (ALL.isEmpty()) {
            ALL.putAll(DANKS);
            ALL.putAll(UPGRADES);
            ALL.put("dock",DOCK);
            ALL.put("red_print",RED_PRINT);
        }
        return ALL;
    }
}
