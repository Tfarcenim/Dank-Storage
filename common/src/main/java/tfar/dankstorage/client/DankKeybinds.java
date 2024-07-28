package tfar.dankstorage.client;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class DankKeybinds {
    static final String CATEGORY =  "key.categories.dankstorage";
    public static final KeyMapping CONSTRUCTION = new KeyMapping("key.dankstorage.construction", GLFW.GLFW_KEY_I,CATEGORY);
    public static final KeyMapping LOCK_SLOT= new KeyMapping("key.dankstorage.lock_slot", GLFW.GLFW_KEY_LEFT_CONTROL,CATEGORY);
    public static final KeyMapping PICKUP_MODE = new KeyMapping("key.dankstorage.pickup_mode", GLFW.GLFW_KEY_O, CATEGORY);
}
