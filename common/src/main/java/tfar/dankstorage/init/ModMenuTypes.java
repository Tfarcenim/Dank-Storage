package tfar.dankstorage.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.menu.ChangeFrequencyMenu;
import tfar.dankstorage.menu.DankMenu;

public class ModMenuTypes {

    public static MenuType<DankMenu> dank_1 = new MenuType<>(DankMenu::t1, FeatureFlags.VANILLA_SET);
    public static MenuType<DankMenu> dank_2 = new MenuType<>(DankMenu::t2, FeatureFlags.VANILLA_SET);
    public static MenuType<DankMenu> dank_3 = new MenuType<>(DankMenu::t3, FeatureFlags.VANILLA_SET);
    public static MenuType<DankMenu> dank_4 = new MenuType<>(DankMenu::t4, FeatureFlags.VANILLA_SET);
    public static MenuType<DankMenu> dank_5 = new MenuType<>(DankMenu::t5, FeatureFlags.VANILLA_SET);
    public static MenuType<DankMenu> dank_6 = new MenuType<>(DankMenu::t6, FeatureFlags.VANILLA_SET);
    public static MenuType<DankMenu> dank_7 = new MenuType<>(DankMenu::t7, FeatureFlags.VANILLA_SET);

    public static MenuType<ChangeFrequencyMenu> change_frequency = new MenuType<>(ChangeFrequencyMenu::new,FeatureFlags.VANILLA_SET);

    static {
        Registry.register(BuiltInRegistries.MENU, DankStorage.id("dank_1"),dank_1);
        Registry.register(BuiltInRegistries.MENU, DankStorage.id("dank_2"),dank_2);
        Registry.register(BuiltInRegistries.MENU, DankStorage.id("dank_3"),dank_3);
        Registry.register(BuiltInRegistries.MENU, DankStorage.id("dank_4"),dank_4);
        Registry.register(BuiltInRegistries.MENU, DankStorage.id("dank_5"),dank_5);
        Registry.register(BuiltInRegistries.MENU, DankStorage.id("dank_6"),dank_6);
        Registry.register(BuiltInRegistries.MENU, DankStorage.id("dank_7"),dank_7);
        Registry.register(BuiltInRegistries.MENU, DankStorage.id("change_frequency"),change_frequency);
    }

    public static void init() {
    }
}
