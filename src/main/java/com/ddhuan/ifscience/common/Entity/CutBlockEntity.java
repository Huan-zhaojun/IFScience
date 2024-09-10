package com.ddhuan.ifscience.common.Entity;

import com.ddhuan.ifscience.common.DataSerializersRegistry;
import com.ddhuan.ifscience.common.ModNBTUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.UUID;

public class CutBlockEntity extends BlockEntity {
    public Direction direction;
    public static final DataParameter<Direction> directionData = EntityDataManager.createKey(CutBlockEntity.class, (IDataSerializer<Direction>) DataSerializersRegistry.CutBlockEntity_Direction.get().getSerializer());

    @OnlyIn(Dist.CLIENT)
    public int animationTick = 0;//动画时间计数
    @OnlyIn(Dist.CLIENT)
    public long lastTime = 0;

    public static int maxLifeTick = 60;
    public int lifeTick = maxLifeTick;//生命时长

    public UUID playerUuid = null;
    public ItemStack angleGrinder = ItemStack.EMPTY;
    public static final DataParameter<ItemStack> angleGrinderData = EntityDataManager.createKey(CutBlockEntity.class, DataSerializers.ITEMSTACK);

    public CutBlockEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    public CutBlockEntity(World worldIn, double x, double y, double z, BlockState blockState, CompoundNBT tileEntityNbt, net.minecraft.util.Direction direction, UUID playerUuid, ItemStack angleGrinder) {
        super(entityTypeRegistry.CutBlockEntity.get(), worldIn, x, y, z, blockState, tileEntityNbt);
        this.direction = Direction.valueOf(direction.name());
        this.playerUuid = playerUuid;
        this.angleGrinder = angleGrinder;
    }

    @Override
    public void synchBlockState() {
        super.synchBlockState();
        if (!world.isRemote) {
            this.dataManager.set(directionData, this.direction);
            this.dataManager.set(angleGrinderData, this.angleGrinder);
        }
        if (world.isRemote) {
            this.direction = this.dataManager.get(directionData);
            this.angleGrinder = this.dataManager.get(angleGrinderData);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!world.isRemote) {
            if (lifeTick <= 0) {
                for (ItemStack itemStack : getItemStacks(this.blockState.getBlock().asItem()))
                    this.entityDropItem(itemStack);//切完方块可以获得其合成材料的物品(如果有)
                this.remove();
            }
            PlayerEntity player = world.getPlayerByUuid(playerUuid);
            if (!angleGrinder.equals(ItemStack.EMPTY) && lifeTick <= maxLifeTick - 40 && player != null) {
                if (player.inventory.getFirstEmptyStack() == -1 || !player.addItemStackToInventory(angleGrinder.copy()))
                    player.dropItem(angleGrinder.copy(), false, true);
                angleGrinder = ItemStack.EMPTY;
            } else if (!angleGrinder.equals(ItemStack.EMPTY) && lifeTick <= maxLifeTick - 40) {
                this.entityDropItem(angleGrinder.copy());
                angleGrinder = ItemStack.EMPTY;
            }
        }
        lifeTick--;
    }

    private final static HashMap<Item, ItemStack[]> recipeCache = new HashMap<>();//Cache

    public ItemStack[] getItemStacks(Item item) {
        if (recipeCache.containsKey(item)) {
            ItemStack[] materials = recipeCache.get(item), copyMaterials = new ItemStack[materials.length];
            for (int i = 0; i < copyMaterials.length; i++) {
                copyMaterials[i] = materials[i].copy();
            }
            return copyMaterials;
        }
        ItemStack targetItemStack = new ItemStack(item, 1);
        for (ICraftingRecipe recipe : world.getRecipeManager().getRecipesForType(IRecipeType.CRAFTING)) {
            if (ItemStack.areItemStacksEqual(recipe.getRecipeOutput(), targetItemStack)) {
                //获取配方的所有输入材料
                NonNullList<Ingredient> ingredients = recipe.getIngredients();
                ItemStack[] materials = new ItemStack[ingredients.size()];
                for (int i = 0; i < ingredients.size(); i++) {
                    ItemStack[] matchingStacks = ingredients.get(i).getMatchingStacks();
                    //将每个 Ingredient 的第一个匹配的物品复制到数组中
                    materials[i] = matchingStacks.length > 0 ? matchingStacks[0].copy() : ItemStack.EMPTY;
                }
                recipeCache.put(item, materials);//缓存
                ItemStack[] copyMaterials = new ItemStack[materials.length];
                for (int i = 0; i < copyMaterials.length; i++) {
                    copyMaterials[i] = materials[i].copy();
                }
                return copyMaterials;
            }
        }
        //如果没有找到匹配的配方，返回自己
        recipeCache.put(item, new ItemStack[]{targetItemStack});
        return new ItemStack[]{targetItemStack};
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(directionData, this.direction);
        this.dataManager.register(angleGrinderData, ItemStack.EMPTY);
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putByte("CutBlock&direction", direction.index);
        compound.putUniqueId("CutBlock&playerUuid", playerUuid);
        compound.put("CutBlock&ItemStack", ModNBTUtil.writeItemStack(new CompoundNBT(), angleGrinder));
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        direction = Direction.get(compound.getByte("CutBlock&direction"));
        if (this.direction != null)
            this.dataManager.set(directionData, this.direction);
        playerUuid = compound.getUniqueId("CutBlock&playerUuid");
        angleGrinder = ModNBTUtil.readItemStack(compound.getCompound("CutBlock&ItemStack"));
        this.dataManager.set(angleGrinderData, this.angleGrinder);
    }


    @Override
    public IPacket<?> createSpawnPacket() {
        return super.createSpawnPacket();
    }

    @Override
    public void onKillCommand() {
    }

    public enum Direction {
        NORTH((byte) 1, +0.0625 * 2.0D, -0.25D/*+1.0D*/, -90f),
        SOUTH((byte) 2, -0.0625 * 2.0D, +0.25D/*-1.0D*/, 90f),
        EAST((byte) 3, +0.25D/*-1.0D*/, +0.0625 * 2.0D, 0f),
        WEST((byte) 4, -0.25D/*+1.0D*/, -0.0625 * 2.0D, -180f);

        public final byte index;//序号
        public final double translateX;
        public final double translateZ;
        public final float degreesYN;

        Direction(byte index, double translateX, double translateZ, float degreesYN) {
            this.index = index;
            this.translateX = translateX;
            this.translateZ = translateZ;
            this.degreesYN = degreesYN;
        }

        public static Direction get(byte index) {
            for (Direction direction : Direction.values()) {
                if (direction.index == index) {
                    return direction;
                }
            }
            return null;
        }

        //在x轴方向的移动
        public double moveX(int tick, int maxTick, double maxLength) {
            if (this.equals(Direction.NORTH) || this.equals(Direction.SOUTH)) return 0;
            else if (this.equals(Direction.WEST))
                return tick > maxTick ? maxLength : ((double) tick / maxTick) * maxLength;
            else if (this.equals(Direction.EAST))
                return tick > maxTick ? -maxLength : -((double) tick / maxTick) * maxLength;
            else return 0;
        }

        //在z轴方向的移动
        public double moveZ(int tick, int maxTick, double maxLength) {
            if (this.equals(Direction.WEST) || this.equals(Direction.EAST)) return 0;
            else if (this.equals(Direction.NORTH))
                return tick > maxTick ? maxLength : ((double) tick / maxTick) * maxLength;
            else if (this.equals(Direction.SOUTH))
                return tick > maxTick ? -maxLength : -((double) tick / maxTick) * maxLength;
            else return 0;
        }
    }
}