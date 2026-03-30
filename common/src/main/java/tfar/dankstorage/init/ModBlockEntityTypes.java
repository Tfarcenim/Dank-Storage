package tfar.dankstorage.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.blockentity.DockBlockEntity;

import java.util.Set;

public class ModBlockEntityTypes {
    public static final BlockEntityType<DockBlockEntity> DOCK = new BlockEntityType<>(DockBlockEntity::new, Set.of(ModBlocks.dock));

    static {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, DankStorage.id("dock"),DOCK);
    }

    public static void init() {
    }
}
