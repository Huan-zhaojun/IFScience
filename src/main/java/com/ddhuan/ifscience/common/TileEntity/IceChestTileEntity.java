package com.ddhuan.ifscience.common.TileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleSidedInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(
        value = Dist.CLIENT,
        _interface = IChestLid.class
)
public class IceChestTileEntity extends LockableLootTileEntity implements IChestLid, ITickableTileEntity {
    private NonNullList<ItemStack> chestContents = NonNullList.withSize(27, ItemStack.EMPTY);
    protected float lidAngle;
    protected float prevLidAngle;
    protected int numPlayersUsing;
    private int ticksSinceSync;
    private net.minecraftforge.common.util.LazyOptional<net.minecraftforge.items.IItemHandlerModifiable> chestHandler;

    protected IceChestTileEntity(TileEntityType<?> typeIn) {
        super(typeIn);
    }

    public IceChestTileEntity() {
        this(TileEntityType.CHEST);
    }

    public int getSizeInventory() {
        return 27;
    }

    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.chest");
    }

    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        this.chestContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        if (!this.checkLootAndRead(nbt)) {
            ItemStackHelper.loadAllItems(nbt, this.chestContents);
        }

    }

    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        if (!this.checkLootAndWrite(compound)) {
            ItemStackHelper.saveAllItems(compound, this.chestContents);
        }

        return compound;
    }

    public void tick() {
        int i = this.pos.getX();
        int j = this.pos.getY();
        int k = this.pos.getZ();
        ++this.ticksSinceSync;
        this.numPlayersUsing = calculatePlayersUsingSync(this.world, this, this.ticksSinceSync, i, j, k, this.numPlayersUsing);
        this.prevLidAngle = this.lidAngle;
        float f = 0.1F;
        if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F) {
            this.playSound(SoundEvents.BLOCK_CHEST_OPEN);
        }

        if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) {
            float f1 = this.lidAngle;
            if (this.numPlayersUsing > 0) {
                this.lidAngle += 0.1F;
            } else {
                this.lidAngle -= 0.1F;
            }

            if (this.lidAngle > 1.0F) {
                this.lidAngle = 1.0F;
            }

            float f2 = 0.5F;
            if (this.lidAngle < 0.5F && f1 >= 0.5F) {
                this.playSound(SoundEvents.BLOCK_CHEST_CLOSE);
            }

            if (this.lidAngle < 0.0F) {
                this.lidAngle = 0.0F;
            }
        }

    }

    public static int calculatePlayersUsingSync(World world, LockableTileEntity p_213977_1_, int p_213977_2_, int p_213977_3_, int p_213977_4_, int p_213977_5_, int p_213977_6_) {
        if (!world.isRemote && p_213977_6_ != 0 && (p_213977_2_ + p_213977_3_ + p_213977_4_ + p_213977_5_) % 200 == 0) {
            p_213977_6_ = calculatePlayersUsing(world, p_213977_1_, p_213977_3_, p_213977_4_, p_213977_5_);
        }

        return p_213977_6_;
    }

    public static int calculatePlayersUsing(World world, LockableTileEntity p_213976_1_, int x, int y, int z) {
        int i = 0;
        float f = 5.0F;

        for(PlayerEntity playerentity : world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB((double)((float)x - 5.0F), (double)((float)y - 5.0F), (double)((float)z - 5.0F), (double)((float)(x + 1) + 5.0F), (double)((float)(y + 1) + 5.0F), (double)((float)(z + 1) + 5.0F)))) {
            if (playerentity.openContainer instanceof ChestContainer) {
                IInventory iinventory = ((ChestContainer)playerentity.openContainer).getLowerChestInventory();
                if (iinventory == p_213976_1_ || iinventory instanceof DoubleSidedInventory && ((DoubleSidedInventory)iinventory).isPartOfLargeChest(p_213976_1_)) {
                    ++i;
                }
            }
        }

        return i;
    }

    private void playSound(SoundEvent soundIn) {
        ChestType chesttype = this.getBlockState().get(ChestBlock.TYPE);
        if (chesttype != ChestType.LEFT) {
            double d0 = (double)this.pos.getX() + 0.5D;
            double d1 = (double)this.pos.getY() + 0.5D;
            double d2 = (double)this.pos.getZ() + 0.5D;
            if (chesttype == ChestType.RIGHT) {
                Direction direction = ChestBlock.getDirectionToAttached(this.getBlockState());
                d0 += (double)direction.getXOffset() * 0.5D;
                d2 += (double)direction.getZOffset() * 0.5D;
            }

            this.world.playSound((PlayerEntity)null, d0, d1, d2, soundIn, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
        }
    }

    /**
     * See {@link Block#eventReceived} for more information. This must return true serverside before it is called
     * clientside.
     */
    public boolean receiveClientEvent(int id, int type) {
        if (id == 1) {
            this.numPlayersUsing = type;
            return true;
        } else {
            return super.receiveClientEvent(id, type);
        }
    }

    public void openInventory(PlayerEntity player) {
        if (!player.isSpectator()) {
            if (this.numPlayersUsing < 0) {
                this.numPlayersUsing = 0;
            }

            ++this.numPlayersUsing;
            this.onOpenOrClose();
        }

    }

    public void closeInventory(PlayerEntity player) {
        if (!player.isSpectator()) {
            --this.numPlayersUsing;
            this.onOpenOrClose();
        }

    }

    protected void onOpenOrClose() {
        Block block = this.getBlockState().getBlock();
        if (block instanceof ChestBlock) {
            this.world.addBlockEvent(this.pos, block, 1, this.numPlayersUsing);
            this.world.notifyNeighborsOfStateChange(this.pos, block);
        }

    }

    protected NonNullList<ItemStack> getItems() {
        return this.chestContents;
    }

    protected void setItems(NonNullList<ItemStack> itemsIn) {
        this.chestContents = itemsIn;
    }

    @OnlyIn(Dist.CLIENT)
    public float getLidAngle(float partialTicks) {
        return MathHelper.lerp(partialTicks, this.prevLidAngle, this.lidAngle);
    }

    /*public static int getPlayersUsing(IBlockReader reader, BlockPos posIn) {
        BlockState blockstate = reader.getBlockState(posIn);
        if (blockstate.hasTileEntity()) {
            TileEntity tileentity = reader.getTileEntity(posIn);
            if (tileentity instanceof ChestTileEntity) {
                return ((ChestTileEntity)tileentity).numPlayersUsing;
            }
        }

        return 0;
    }

    public static void swapContents(ChestTileEntity chest, ChestTileEntity otherChest) {
        NonNullList<ItemStack> nonnulllist = chest.getItems();
        chest.setItems(otherChest.getItems());
        otherChest.setItems(nonnulllist);
    }*/

    protected Container createMenu(int id, PlayerInventory player) {
        return ChestContainer.createGeneric9X3(id, player, this);
    }

    @Override
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        if (this.chestHandler != null) {
            net.minecraftforge.common.util.LazyOptional<?> oldHandler = this.chestHandler;
            this.chestHandler = null;
            oldHandler.invalidate();
        }
    }

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, Direction side) {
        if (!this.removed && cap == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (this.chestHandler == null)
                this.chestHandler = net.minecraftforge.common.util.LazyOptional.of(this::createHandler);
            return this.chestHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    private net.minecraftforge.items.IItemHandlerModifiable createHandler() {
        BlockState state = this.getBlockState();
        if (!(state.getBlock() instanceof ChestBlock)) {
            return new net.minecraftforge.items.wrapper.InvWrapper(this);
        }
        IInventory inv = ChestBlock.getChestInventory((ChestBlock) state.getBlock(), state, getWorld(), getPos(), true);
        return new net.minecraftforge.items.wrapper.InvWrapper(inv == null ? this : inv);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        if (chestHandler != null)
            chestHandler.invalidate();
    }
}
