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
import tfar.dankstorage.blockentity.DockBlockEntity;
import tfar.dankstorage.init.ModDataComponentTypes;
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
        ItemStack redPrintStack = useOnContext.getItemInHand();
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof DockBlockEntity dockBlockEntity) {
                if (player.isCrouching()) {
                    int freq = DankItem.getFrequency(dockBlockEntity.getDank());
                    DankItem.setFrequency(redPrintStack, freq);
                } else {
                    if (redPrintStack.has(ModDataComponentTypes.FREQUENCY)) {
                        dockBlockEntity.setFrequency(redPrintStack.get(ModDataComponentTypes.FREQUENCY));
                    }
                }
            }
        }
        return super.useOn(useOnContext);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction clickAction, Player player) {
        ItemStack otherStack = slot.getItem();

        if (otherStack.getItem() instanceof DankItem) {
            int redF = DankItem.getFrequency(stack);
            if (redF > CommonUtils.INVALID) {
                DankItem.setFrequency(otherStack,redF);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack otherStack, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {
        if (otherStack.getItem() instanceof DankItem) {
            int freq = DankItem.getFrequency(otherStack);
            if (freq > CommonUtils.INVALID) {
                DankItem.setFrequency(stack,freq);
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
