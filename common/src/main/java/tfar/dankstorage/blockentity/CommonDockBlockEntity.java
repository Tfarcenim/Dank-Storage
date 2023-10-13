package tfar.dankstorage.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.item.CDankItem;
import tfar.dankstorage.block.CDockBlock;
import tfar.dankstorage.utils.CommonUtils;
import tfar.dankstorage.utils.DankStats;

import javax.annotation.Nullable;

public abstract class CommonDockBlockEntity extends BlockEntity implements Nameable {
    public CommonDockBlockEntity(BlockEntityType<?> $$0, BlockPos $$1, BlockState $$2) {
        super($$0, $$1, $$2);
    }

    @Nullable
    protected Component customName;
    public CompoundTag settings;

    public int numPlayersUsing = 0;


    public void setFrequency(int freq) {
        if (settings == null) {

        } else {
            settings.putInt(CommonUtils.FREQ,freq);
        }
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            this.numPlayersUsing = type;
            this.setChanged();
            return true;
        } else {
            return super.triggerEvent(id, type);
        }
    }

    @Override
    public Component getName() {
        return customName != null ? customName : getDefaultName();
    }

    public Component getDefaultName() {
        return Component.translatable("container.dankstorage.dank_" + getBlockState().getValue(CDockBlock.TIER));
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return customName;
    }

    public void setCustomName(Component text) {
        this.customName = text;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.settings = compound.getCompound(CommonUtils.SET);
        if (compound.contains("CustomName", 8)) {
            this.setCustomName(Component.Serializer.fromJson(compound.getString("CustomName")));
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (settings != null) {
            tag.put(CommonUtils.SET, settings);
        }
        if (this.hasCustomName()) {
            tag.putString("CustomName", Component.Serializer.toJson(this.customName));
        }
    }

    public void giveToPlayer(Player player) {
        ItemStack dankInStack = removeDankWithoutItemSpawn();

        if (!player.addItem(dankInStack)) {
            ItemEntity entity = new ItemEntity(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), dankInStack);
            level.addFreshEntity(entity);
        }
    }

    public void removeDankWithItemSpawn() {
        ItemStack dankInStack = removeDankWithoutItemSpawn();
        ItemEntity entity = new ItemEntity(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), dankInStack);
        level.addFreshEntity(entity);
    }

    public ItemStack removeDankWithoutItemSpawn() {
        int tier = getBlockState().getValue(CDockBlock.TIER);

        if (tier == 0) {
            throw new RuntimeException("tried to remove a null dank?");
        }

        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(CDockBlock.TIER, 0));
        ItemStack stack = new ItemStack(CommonUtils.getItemFromTier(tier));

        if (settings != null) {
            stack.getOrCreateTag().put(CommonUtils.SET, settings);
        }

        settings = null;

        if (hasCustomName()) {
            stack.setHoverName(getCustomName());
        }

        setCustomName(null);
        setChanged();
        return stack;
    }


    public void addDank(ItemStack tank) {
        if (tank.getItem() instanceof CDankItem) {
            DankStats stats = ((CDankItem) tank.getItem()).stats;
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(CDockBlock.TIER, stats.ordinal()));
            if (tank.hasCustomHoverName()) {
                setCustomName(tank.getHoverName());
            }
            CompoundTag iSettings = CommonUtils.getSettings(tank);
            tank.shrink(1);

            if (iSettings != null && iSettings.contains(CommonUtils.FREQ)) {//existing frequency
                this.settings = iSettings;
            } else {
                this.settings = new CompoundTag();
                int newId = DankStorage.maxId.getMaxId();
                DankStorage.maxId.increment();
                settings.putInt(CommonUtils.FREQ, newId);
            }
            setChanged();
        }
    }

    public abstract void upgradeTo(DankStats stats);

}
