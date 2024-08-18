package tfar.dankstorage.datagen;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import tfar.dankstorage.block.CDockBlock;
import tfar.dankstorage.init.ModBlocks;
import tfar.dankstorage.init.ModDataComponents;
import tfar.dankstorage.init.ModItems;
import tfar.dankstorage.item.DankItem;

import java.util.List;
import java.util.Set;

public class ModBlockLoot extends BlockLootSubProvider {
    protected ModBlockLoot(HolderLookup.Provider pRegistries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), pRegistries);
    }

    @Override
    protected void generate() {
        add(ModBlocks.dock, LootTable.lootTable().withPool(LootPool.lootPool().add(this.applyExplosionCondition(ModBlocks.dock, LootItem.lootTableItem(ModBlocks.dock))))
                .withPool(makeDank(ModItems.DANKS.get("dank_1")))
                .withPool(makeDank(ModItems.DANKS.get("dank_2")))
                .withPool(makeDank(ModItems.DANKS.get("dank_3")))
                .withPool(makeDank(ModItems.DANKS.get("dank_4")))
                .withPool(makeDank(ModItems.DANKS.get("dank_5")))
                .withPool(makeDank(ModItems.DANKS.get("dank_6")))
                .withPool(makeDank(ModItems.DANKS.get("dank_7")))
        );
    }

    protected LootPool.Builder makeDank(DankItem dank) {
        return LootPool.lootPool().add(LootItem.lootTableItem(dank)
                .when(
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.dock)
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CDockBlock.TIER, dank.stats.ordinal()))
                )
                .apply(
                        CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                .include(DataComponents.CUSTOM_NAME)
                                .include(ModDataComponents.FREQUENCY)
                                .include(ModDataComponents.PICKUP_MODE)
                                .include(ModDataComponents.USE_TYPE)
                                .include(ModDataComponents.OREDICT)
                )
        );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return List.of(ModBlocks.dock);
    }
}
