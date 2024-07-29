package tfar.dankstorage.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import tfar.dankstorage.blockentity.CommonDockBlockEntity;
import tfar.dankstorage.init.ModDataComponents;
import tfar.dankstorage.utils.CommonUtils;

import java.util.List;

public class RedprintItem extends Item {
    public RedprintItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(CommonUtils.translatable("text.dankstorage.red_print.tooltip0"));
        pTooltipComponents.add(CommonUtils.translatable("text.dankstorage.red_print.tooltip1"));
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Player player = useOnContext.getPlayer();
        Level level = useOnContext.getLevel();
        BlockPos pos = useOnContext.getClickedPos();
        ItemStack stack = useOnContext.getItemInHand();
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CommonDockBlockEntity dockBlockEntity) {
                if (player.isCrouching()) {
                    int freq = dockBlockEntity.getInventory().frequency();
                    CommonUtils.setFrequency(stack, freq);
                } else {
                    if (stack.has(ModDataComponents.FREQUENCY)) {
                        dockBlockEntity.setFrequency(stack.get(ModDataComponents.FREQUENCY));
                    }
                }
            }
        }
        return super.useOn(useOnContext);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction clickAction, Player player) {
        ItemStack otherStack = slot.getItem();

        if (otherStack.getItem() instanceof CDankItem) {
            int redF = CommonUtils.getFrequency(stack);
            if (redF > CommonUtils.INVALID) {
                CommonUtils.setFrequency(otherStack,redF);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack otherStack, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {
        if (otherStack.getItem() instanceof CDankItem) {
            int freq = CommonUtils.getFrequency(otherStack);
            if (freq > CommonUtils.INVALID) {
                CommonUtils.setFrequency(stack,freq);
                return true;
            }
        }
        return false;
    }

    @Override
    public Component getName(ItemStack itemStack) {
        MutableComponent component = CommonUtils.literal(super.getName(itemStack).getString());
    //    if (itemStack.hasTag() && itemStack.getTag().contains(CommonUtils.FREQ)) {
    //        component.append(" ("+itemStack.getTag().getInt(CommonUtils.FREQ)+")");
    //    }
        return component;
    }
}
