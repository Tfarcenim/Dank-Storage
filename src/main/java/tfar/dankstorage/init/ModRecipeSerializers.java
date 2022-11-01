package tfar.dankstorage.init;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.recipe.Serializer2;

public class ModRecipeSerializers {

    public static void registerB(RegistryEvent.Register<RecipeSerializer<?>> event) {
        DankStorage.register(event.getRegistry(),"upgrade", DankStorage.upgrade = new Serializer2());
    }
}
