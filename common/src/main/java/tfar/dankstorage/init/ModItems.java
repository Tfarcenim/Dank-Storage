package tfar.dankstorage.init;

import com.google.gson.internal.LinkedTreeMap;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.block.DankDispenserBehavior;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.item.RedprintItem;
import tfar.dankstorage.item.UpgradeItem;
import tfar.dankstorage.utils.UpgradeInfo;
import tfar.dankstorage.utils.DankStats;

import java.util.Map;
import java.util.stream.IntStream;

public class ModItems {
    public static Item RED_PRINT = new RedprintItem(new Item.Properties().setId(key("red_print")));
    public static final Item DOCK = new BlockItem(ModBlocks.DOCK, new Item.Properties().setId(key("dock")));
    public static final Map<String, DankItem> DANKS;
    public static final Map<String, UpgradeItem> UPGRADES;
    public static final Map<String,Item> ALL = new LinkedTreeMap<>();

    static ResourceKey<Item> key(String s) {
        return ResourceKey.create(Registries.ITEM,DankStorage.id(s));
    }

    static {
        DANKS = new LinkedTreeMap<>();
        IntStream.range(1, 8).forEach(i -> {
            String s = "dank_" + i;
            DankItem dankItem = new DankItem(new Item.Properties().setId(key(s)), DankStats.values()[i]);
            DispenserBlock.registerBehavior(dankItem, new DankDispenserBehavior());
            DANKS.put(s, dankItem);
        });

        UPGRADES = new LinkedTreeMap<>();
        int bound = DANKS.size();
        for (int i = 1; i < bound; i++) {
            String s = i+"_to_"+(i+1);
            UpgradeItem upgradeItem = new UpgradeItem(new Item.Properties().setId(key(s)), new UpgradeInfo(i, i + 1));
            UPGRADES.put(s,upgradeItem);
        }
    }

    static {
        getAll().forEach((string, item) -> Registry.register(BuiltInRegistries.ITEM, DankStorage.id(string),item));
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

    public static void init() {

    }
}
