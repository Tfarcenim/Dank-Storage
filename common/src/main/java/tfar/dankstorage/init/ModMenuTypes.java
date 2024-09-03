package tfar.dankstorage.init;

import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
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
}
