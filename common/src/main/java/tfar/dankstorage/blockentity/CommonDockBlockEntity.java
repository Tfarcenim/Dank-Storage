package tfar.dankstorage.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tfar.dankstorage.DankStorage;
import tfar.dankstorage.init.ModBlockEntityTypes;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.inventory.LimitedContainerData;
import tfar.dankstorage.inventory.TierDataSlot;
import tfar.dankstorage.item.CDankItem;
import tfar.dankstorage.block.CDockBlock;
import tfar.dankstorage.menu.ChangeFrequencyMenuBlockEntity;
import tfar.dankstorage.menu.DockMenu;
import tfar.dankstorage.platform.Services;
import tfar.dankstorage.utils.*;
import tfar.dankstorage.world.DankSavedData;

import javax.annotation.Nullable;

public abstract class CommonDockBlockEntity<T extends DankInterface> extends BlockEntity implements Nameable, MenuProvider {
    public CommonDockBlockEntity(BlockPos $$1, BlockState $$2) {
        super(ModBlockEntityTypes.dank_tile, $$1, $$2);
    }

    @Nullable
    protected Component customName;

    private int frequency = CommonUtils.INVALID;
    public PickupMode pickupMode = PickupMode.none;
    public UseType useType = UseType.bag;
    public int selected = CommonUtils.INVALID;
    public boolean oredict = false;

    public int numPlayersUsing = 0;


    public void setFrequency(int freq) {
        frequency = freq;
    }

    public int getFrequency() {
        return frequency;
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

    @org.jetbrains.annotations.Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {

        int tier = getBlockState().getValue(CDockBlock.TIER);

        T dankInventory = getInventory();

        DankStats defaults = DankStats.values()[tier];
        if (defaults != dankInventory.getDankStats()) {
            if (defaults.ordinal() < dankInventory.getDankStats().ordinal()) {
               // CommonUtils.warn(player, defaults, dankInventory.getDankStats());
                return new ChangeFrequencyMenuBlockEntity(syncId,inventory, new LimitedContainerData(dankInventory,3),new TierDataSlot(defaults),this);
            }
            dankInventory.upgradeTo(defaults);
        }

        return switch (getBlockState().getValue(CDockBlock.TIER)) {
            case 1 -> DockMenu.t1s(syncId, inventory, dankInventory, this);
            case 2 -> DockMenu.t2s(syncId, inventory, dankInventory, this);
            case 3 -> DockMenu.t3s(syncId, inventory, dankInventory, this);
            case 4 -> DockMenu.t4s(syncId, inventory, dankInventory, this);
            case 5 -> DockMenu.t5s(syncId, inventory, dankInventory, this);
            case 6 -> DockMenu.t6s(syncId, inventory, dankInventory, this);
            case 7 -> DockMenu.t7s(syncId, inventory, dankInventory, this);
            default -> null;
        };
    }

    public static final DankInterface DUMMY = Services.PLATFORM.createInventory(DankStats.zero, CommonUtils.INVALID);

    public T getInventory() {
        if (!level.isClientSide && frequency != CommonUtils.INVALID) {
            DankSavedData savedData = DankStorage.getData(frequency,level.getServer());
            DankInterface dataInventory = savedData.createInventory(level.registryAccess(),frequency);

            if (!dataInventory.valid()) {
                savedData.setStats(DankStats.values()[getBlockState().getValue(CDockBlock.TIER)],frequency);
                dataInventory = savedData.createInventory(level.registryAccess(),frequency);
            }

            return (T)dataInventory;
        }
        return (T)DUMMY;
    }
    @Override
    public Component getName() {
        return customName != null ? customName : getDefaultName();
    }

    public Component getDefaultName() {
        return Component.translatable("container.dankstorage.dank_" + getBlockState().getValue(CDockBlock.TIER));
    }

    public int getComparatorSignal() {
        return this.getInventory().calcRedstone();
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
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(tag, pRegistries);
        if (tag.contains("frequency")) {
            frequency = tag.getInt("frequency");
        }
        pickupMode = PickupMode.valueOf(tag.getString("pickup_mode"));
        useType = UseType.valueOf(tag.getString("use_type"));
        if (tag.contains("selected")) {
            selected = tag.getInt("selected");
        }

        oredict = tag.getBoolean("oredict");

        if (tag.contains("CustomName", Tag.TAG_STRING)) {
            this.setCustomName(Component.Serializer.fromJson(tag.getString("CustomName"),pRegistries));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        if (frequency != CommonUtils.INVALID) {
            tag.putInt("frequency",frequency);
        }
        tag.putString("pickup_mode",pickupMode.name());
        tag.putString("use_type", useType.name());

        if (selected != CommonUtils.INVALID) {
            tag.putInt("selected",selected);
        }
        tag.putBoolean("oredict",oredict);

    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
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



        if (hasCustomName()) {
            stack.set(DataComponents.CUSTOM_NAME,customName);
        }

        setCustomName(null);
        setChanged();
        return stack;
    }


    public void addDank(ItemStack tank) {
        if (tank.getItem() instanceof CDankItem) {
            DankStats stats = ((CDankItem) tank.getItem()).stats;
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(CDockBlock.TIER, stats.ordinal()));
            if (tank.has(DataComponents.CUSTOM_NAME)) {
                setCustomName(tank.getHoverName());
            }
            tank.shrink(1);

            if (CommonUtils.getFrequency(tank)!= CommonUtils.INVALID) {//existing frequency
                this.frequency = CommonUtils.getFrequency(tank);
            } else {
                int newId = DankStorage.maxId.getMaxId();
                DankStorage.maxId.increment();
                frequency = newId;
            }

            pickupMode = CommonUtils.getPickupMode(tank);
            useType = CommonUtils.getUseType(tank);
            selected = CommonUtils.getSelectedSlot(tank);
            oredict = CommonUtils.oredict(tank);

            setChanged();
        }
    }

    public void upgradeTo(DankStats stats) {
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(CDockBlock.TIER, stats.ordinal()));
        DankInterface dankInventory = getInventory();
        dankInventory.upgradeTo(stats);
    }

}
