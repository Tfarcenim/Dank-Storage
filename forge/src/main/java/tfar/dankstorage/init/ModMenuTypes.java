package tfar.dankstorage.init;

import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import tfar.dankstorage.container.DankMenu;
import tfar.dankstorage.container.DockMenu;

public class ModMenuTypes {
    public static MenuType<DockMenu> dank_1_container= new MenuType<>(DockMenu::t1, FeatureFlags.VANILLA_SET);
    public static MenuType<DockMenu> dank_2_container= new MenuType<>(DockMenu::t2 ,FeatureFlags.VANILLA_SET);
    public static MenuType<DockMenu> dank_3_container= new MenuType<>(DockMenu::t3 ,FeatureFlags.VANILLA_SET);
    public static MenuType<DockMenu> dank_4_container= new MenuType<>(DockMenu::t4 ,FeatureFlags.VANILLA_SET);
    public static MenuType<DockMenu> dank_5_container= new MenuType<>(DockMenu::t5 ,FeatureFlags.VANILLA_SET);
    public static MenuType<DockMenu> dank_6_container= new MenuType<>(DockMenu::t6 ,FeatureFlags.VANILLA_SET);
    public static MenuType<DockMenu> dank_7_container= new MenuType<>(DockMenu::t7 ,FeatureFlags.VANILLA_SET);
    public static MenuType<DankMenu> portable_dank_1_container= new MenuType<>(DankMenu::t1 ,FeatureFlags.VANILLA_SET);
    public static MenuType<DankMenu> portable_dank_2_container= new MenuType<>(DankMenu::t2 ,FeatureFlags.VANILLA_SET);
    public static MenuType<DankMenu> portable_dank_3_container= new MenuType<>(DankMenu::t3 ,FeatureFlags.VANILLA_SET);
    public static MenuType<DankMenu> portable_dank_4_container= new MenuType<>(DankMenu::t4 ,FeatureFlags.VANILLA_SET);
    public static MenuType<DankMenu> portable_dank_5_container= new MenuType<>(DankMenu::t5 ,FeatureFlags.VANILLA_SET);
    public static MenuType<DankMenu> portable_dank_6_container= new MenuType<>(DankMenu::t6 ,FeatureFlags.VANILLA_SET);
    public static MenuType<DankMenu> portable_dank_7_container= new MenuType<>(DankMenu::t7 ,FeatureFlags.VANILLA_SET);
}
