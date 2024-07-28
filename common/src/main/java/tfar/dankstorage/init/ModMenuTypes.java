package tfar.dankstorage.init;

import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import tfar.dankstorage.menu.ChangeFrequencyMenu;
import tfar.dankstorage.menu.DankMenu;
import tfar.dankstorage.menu.DockMenu;

public class ModMenuTypes {
    public static MenuType<DockMenu> dank_1 = new MenuType<>(DockMenu::t1, FeatureFlags.VANILLA_SET);
    public static MenuType<DockMenu> dank_2 = new MenuType<>(DockMenu::t2, FeatureFlags.VANILLA_SET);
    public static MenuType<DockMenu> dank_3 = new MenuType<>(DockMenu::t3, FeatureFlags.VANILLA_SET);
    public static MenuType<DockMenu> dank_4 = new MenuType<>(DockMenu::t4, FeatureFlags.VANILLA_SET);
    public static MenuType<DockMenu> dank_5 = new MenuType<>(DockMenu::t5, FeatureFlags.VANILLA_SET);
    public static MenuType<DockMenu> dank_6 = new MenuType<>(DockMenu::t6, FeatureFlags.VANILLA_SET);
    public static MenuType<DockMenu> dank_7 = new MenuType<>(DockMenu::t7, FeatureFlags.VANILLA_SET);
    public static MenuType<DankMenu> portable_dank_1 = new MenuType<>(DankMenu::t1, FeatureFlags.VANILLA_SET);
    public static MenuType<DankMenu> portable_dank_2 = new MenuType<>(DankMenu::t2, FeatureFlags.VANILLA_SET);
    public static MenuType<DankMenu> portable_dank_3 = new MenuType<>(DankMenu::t3, FeatureFlags.VANILLA_SET);
    public static MenuType<DankMenu> portable_dank_4 = new MenuType<>(DankMenu::t4, FeatureFlags.VANILLA_SET);
    public static MenuType<DankMenu> portable_dank_5 = new MenuType<>(DankMenu::t5, FeatureFlags.VANILLA_SET);
    public static MenuType<DankMenu> portable_dank_6 = new MenuType<>(DankMenu::t6, FeatureFlags.VANILLA_SET);
    public static MenuType<DankMenu> portable_dank_7 = new MenuType<>(DankMenu::t7, FeatureFlags.VANILLA_SET);

    public static MenuType<ChangeFrequencyMenu> change_frequency = new MenuType<>(ChangeFrequencyMenu::new,FeatureFlags.VANILLA_SET);
}
