package tfar.dankstorage.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.RegistryEvent;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.block.DockBlock;

public class ModBlocks {
    public static void registerB(RegistryEvent.Register<Block> event) {
        DankStorage.register(event.getRegistry(), "dock", DankStorage.dock = new DockBlock(BlockBehaviour.Properties.of(Material.METAL).strength(1, 30)));
    }
}
