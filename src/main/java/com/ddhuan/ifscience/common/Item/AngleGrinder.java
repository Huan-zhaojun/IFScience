package com.ddhuan.ifscience.common.Item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
