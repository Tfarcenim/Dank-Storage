package tfar.dankstorage.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.blockentity.DockBlockEntity;

public class ModBlockEntityTypes {
    public static void registerB(RegistryEvent.Register<BlockEntityType<?>> event) {
        DankStorage.register(event.getRegistry(), "dank_tile", DankStorage.dank_tile = BlockEntityType.Builder.of(DockBlockEntity::new, DankStorage.dock).build(null));
    }
}
