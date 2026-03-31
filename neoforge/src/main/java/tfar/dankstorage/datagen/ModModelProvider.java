package tfar.dankstorage.datagen;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.block.DockBlock;
import tfar.dankstorage.init.ModBlocks;
import tfar.dankstorage.init.ModItems;

import static net.minecraft.client.data.models.BlockModelGenerators.condition;
import static net.minecraft.client.data.models.BlockModelGenerators.plainVariant;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, DankStorage.MODID);
    }

    public static final ModelTemplate CORE = ModelTemplates.create(
            "dankstorage:core", TextureSlot.ALL, TextureSlot.PARTICLE);

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(ModItems.RED_PRINT, ModelTemplates.FLAT_ITEM);
        for (Item item : ModItems.UPGRADES.values()) {
            itemModels.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
        }
        for (Item item : ModItems.DANKS.values()) {
            createDank(item,itemModels);
        }
        createDock(blockModels);
    }

    public void createDank(Item item,ItemModelGenerators itemModels) {
        TextureMapping textureMapping = new TextureMapping().put(TextureSlot.ALL,TextureMapping.getItemTexture(item));
        itemModels.itemModelOutput.accept(item,ItemModelUtils.plainModel(CORE.create(item,textureMapping,itemModels.modelOutput)));
    }
    //    public Identifier createFlatItemModel(Item item, ModelTemplate template) {
    //        return template.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(item), this.modelOutput);
    //    }

    public void createDock(BlockModelGenerators blockModels) {
            blockModels.blockStateOutput
                    .accept(
                            MultiPartGenerator.multiPart(ModBlocks.DOCK)
                                    .with(plainVariant(ModelLocationUtils.getModelLocation(ModBlocks.DOCK)))
                                    .with(
                                            condition().term(DockBlock.TIER, 1),
                                            plainVariant(modLocation("item/dank_1"))
                                    )
                                    .with(
                                            condition().term(DockBlock.TIER, 2),
                                            plainVariant(modLocation("item/dank_2"))
                                    )
                                    .with(
                                            condition().term(DockBlock.TIER, 3),
                                            plainVariant(modLocation("item/dank_3"))
                                    )
                                    .with(
                                            condition().term(DockBlock.TIER, 4),
                                            plainVariant(modLocation("item/dank_4"))
                                    )
                                    .with(
                                            condition().term(DockBlock.TIER, 5),
                                            plainVariant(modLocation("item/dank_5"))
                                    )
                                    .with(
                                            condition().term(DockBlock.TIER, 6),
                                            plainVariant(modLocation("item/dank_6"))
                                    )
                                    .with(
                                            condition().term(DockBlock.TIER, 7),
                                            plainVariant(modLocation("item/dank_7"))
                                    )
                    );
    }
}
