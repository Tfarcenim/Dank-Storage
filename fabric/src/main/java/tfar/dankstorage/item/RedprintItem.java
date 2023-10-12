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
import org.jetbrains.annotations.Nullable;
import tfar.dankstorage.blockentity.DockBlockEntity;
import tfar.dankstorage.utils.Utils;

import java.util.List;

public class RedprintItem extends Item {
    public RedprintItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("text.dankstorage.red_print.tooltip0"));
        list.add(Component.translatable("text.dankstorage.red_print.tooltip1"));

      //  int frequency = getFrequency(stack);

      //  list.add(Component.literal("ID: "+frequency));

    }

    private static int getFrequency(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("frequency")) {
            return stack.getTag().getInt("frequency");
        }
        return Utils.INVALID;
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Player player = useOnContext.getPlayer();
        Level level = useOnContext.getLevel();
        BlockPos pos = useOnContext.getClickedPos();
        ItemStack stack = useOnContext.getItemInHand();
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof DockBlockEntity dockBlockEntity) {
                if (player.isCrouching()) {
                    int freq = dockBlockEntity.getInventory().getFrequency();
                    stack.getOrCreateTag().putInt("frequency", freq);
                } else {
                    if (stack.hasTag() && stack.getTag().contains("frequency")) {
                        int freq = stack.getTag().getInt("frequency");
                        dockBlockEntity.setFrequency(freq);
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
            int redF = getFrequency(stack);
            if (redF != Utils.INVALID) {
                Utils.setFrequency(otherStack,redF);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack otherStack, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {

        if (otherStack.getItem() instanceof DankItem) {
            int freq = Utils.getFrequency(otherStack);
            if (freq > Utils.INVALID) {
                stack.getOrCreateTag().putInt("frequency", freq);
                return true;
            }
        }
        return false;
    }

    @Override
    public Component getName(ItemStack itemStack) {
        MutableComponent component = Component.literal(super.getName(itemStack).getString());
        if (itemStack.hasTag() && itemStack.getTag().contains("frequency")) {
            component.append(" ("+itemStack.getTag().getInt("frequency")+")");
        }
        return (Component) component;
    }
}
