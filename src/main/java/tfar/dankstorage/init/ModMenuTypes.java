package tfar.dankstorage.init;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.event.RegistryEvent;
import tfar.dankstorage.container.DankMenu;
import tfar.dankstorage.container.DockMenu;

import static tfar.dankstorage.DankStorage.register;

public class ModMenuTypes {
    public static MenuType<DockMenu> dank_1_container;
    public static MenuType<DockMenu> dank_2_container;
    public static MenuType<DockMenu> dank_3_container;
    public static MenuType<DockMenu> dank_4_container;
    public static MenuType<DockMenu> dank_5_container;
    public static MenuType<DockMenu> dank_6_container;
    public static MenuType<DockMenu> dank_7_container;
    public static MenuType<DankMenu> portable_dank_1_container;
    public static MenuType<DankMenu> portable_dank_2_container;
    public static MenuType<DankMenu> portable_dank_3_container;
    public static MenuType<DankMenu> portable_dank_4_container;
    public static MenuType<DankMenu> portable_dank_5_container;
    public static MenuType<DankMenu> portable_dank_6_container;
    public static MenuType<DankMenu> portable_dank_7_container;

    public static void registerB(RegistryEvent.Register<MenuType<?>> event) {
        register(event.getRegistry(), "dank_1", ModMenuTypes.dank_1_container = new MenuType<>(DockMenu::t1));
        register(event.getRegistry(), "portable_dank_1", ModMenuTypes.portable_dank_1_container = new MenuType<>(DankMenu::t1));

        register(event.getRegistry(),( "dank_2"), ModMenuTypes.dank_2_container = new MenuType<>(DockMenu::t2));
        register(event.getRegistry(),"portable_dank_2", ModMenuTypes.portable_dank_2_container = new MenuType<>(DankMenu::t2));

        register(event.getRegistry(),( "dank_3"), ModMenuTypes.dank_3_container = new MenuType<>(DockMenu::t3));
        register(event.getRegistry(), "portable_dank_3", ModMenuTypes.portable_dank_3_container = new MenuType<>(DankMenu::t3));

        register(event.getRegistry(),( "dank_4"), ModMenuTypes.dank_4_container = new MenuType<>(DockMenu::t4));
        register(event.getRegistry(), "portable_dank_4", ModMenuTypes.portable_dank_4_container = new MenuType<>(DankMenu::t4));

        register(event.getRegistry(), "dank_5", ModMenuTypes.dank_5_container = new MenuType<>(DockMenu::t5));
        register(event.getRegistry(), "portable_dank_5", ModMenuTypes.portable_dank_5_container = new MenuType<>(DankMenu::t5));

        register(event.getRegistry(), "dank_6", ModMenuTypes.dank_6_container = new MenuType<>(DockMenu::t6));
        register(event.getRegistry(), "portable_dank_6", ModMenuTypes.portable_dank_6_container = new MenuType<>(DankMenu::t6));

        register(event.getRegistry(), "dank_7", ModMenuTypes.dank_7_container = new MenuType<>(DockMenu::t7));
        register(event.getRegistry(), "portable_dank_7", ModMenuTypes.portable_dank_7_container = new MenuType<>(DankMenu::t7));
    }
}
