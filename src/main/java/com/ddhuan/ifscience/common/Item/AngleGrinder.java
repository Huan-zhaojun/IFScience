package com.ddhuan.ifscience.common.Item;

import com.ddhuan.ifscience.common.Entity.CutBlockEntity;
import com.ddhuan.ifscience.common.customDamage;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class AngleGrinder extends Item {
    public final float hardness;
    public final float attackDamage;
    private final Multimap<Attribute, AttributeModifier> itemAttributes;

    public AngleGrinder(Properties properties, float hardness, float attackDamage) {
        super(properties);
        this.hardness = hardness;
        this.attackDamage = attackDamage;
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", attackDamage, AttributeModifier.Operation.ADDITION));
        this.itemAttributes = builder.build();
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return false;
    }

    //左键点击切割方块
    public static void onPlayerBreakBlock(PlayerInteractEvent.LeftClickBlock event) {
        PlayerEntity player = event.getPlayer();
        World world = player.world;
        if (!world.isRemote && player.getHeldItemMainhand().getItem() instanceof AngleGrinder) {
            AngleGrinder angleGrinder = (AngleGrinder) player.getHeldItemMainhand().getItem();
            player.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
            event.setCanceled(true);
            BlockPos pos = event.getPos();
            BlockState blockState = world.getBlockState(pos);
            if (!Blocks.AIR.equals(blockState.getBlock())) {
                float blockHardness = blockState.getBlockHardness(world, pos);
                ItemEntity itementity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(angleGrinder));
                itementity.setDefaultPickupDelay();
                if (angleGrinder.hardness == blockHardness) {
                    if (Math.random() < 0.10) {
                        world.addEntity(itementity);
                        player.attackEntityFrom(customDamage.AngleGrinder1, 8);
                        return;
                    }
                } else if (angleGrinder.hardness < blockHardness || blockHardness <= 0) {
                    world.addEntity(itementity);
                    player.attackEntityFrom(customDamage.AngleGrinder1, (float) (player.getMaxHealth() * (1 + Math.random())));
                    return;
                } else if (angleGrinder.hardness > blockHardness) {
                    if (Math.random() < 0.01) {
                        world.addEntity(itementity);
                        player.attackEntityFrom(customDamage.AngleGrinder1, 8);
                        return;
                    }
                }
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                Direction direction = player.getHorizontalFacing();//获取玩家切割的朝向
                world.addEntity(new CutBlockEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, blockState, direction,
                        player.getUniqueID(), (byte) (angleGrinder.equals(itemRegistry.ironAngleGrinder.get()) ? 1 : 2)));
            }
        }
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damageItem(2, attacker, (entity) -> {
            entity.sendBreakAnimation(EquipmentSlotType.MAINHAND);
        });
        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
        return equipmentSlot == EquipmentSlotType.MAINHAND ? this.itemAttributes : super.getAttributeModifiers(equipmentSlot);
    }

    /*@Override
    public boolean equals(Object obj) {
        return obj instanceof AngleGrinder && this.hardness == ((AngleGrinder) obj).hardness;
    }*/

/*@Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        if (!world.isRemote) {
            BlockPos pos = context.getPos();
            BlockState blockState = world.getBlockState(pos);
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            Direction direction = Direction.NORTH;
            if (context.getPlayer() != null) {
                direction = context.getPlayer().getHorizontalFacing();//获取玩家切割的朝向
            }
            world.addEntity(new CutBlockEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, blockState,direction));
        }
        return super.onItemUse(context);
    }*/
}
