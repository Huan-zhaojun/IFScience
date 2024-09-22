package com.ddhuan.ifscience.mixin.block;

import com.ddhuan.ifscience.network.Client.BlockBreakProgressPack;
import com.ddhuan.ifscience.network.Network;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FourWayBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

import static com.ddhuan.ifscience.Custom.ExplosionProofUtil.*;

@Mixin(PaneBlock.class)
public abstract class PaneBlockMixin extends FourWayBlock {
    public PaneBlockMixin(float nodeWidth, float extensionWidth, float nodeHeight, float extensionHeight, float collisionY, Properties properties) {
        super(nodeWidth, extensionWidth, nodeHeight, extensionHeight, collisionY, properties);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void PaneBlock(Properties builder, CallbackInfo ci) {
        this.setDefaultState(this.stateContainer.getBaseState().with(BreakProgress, 0).with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)).with(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if (isGlass(this.getBlock()))
            glassTick(this, state, worldIn, pos);
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (isGlass(this.getBlock()))
            tryScheduleBurstTick(this, state, worldIn, pos);
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (isGlass(this.getBlock()))
            tryScheduleBurstTick(this, state, worldIn, pos);
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (isGlass(this.getBlock()))
            Network.INSTANCE.send(PacketDistributor.ALL.noArg(), new BlockBreakProgressPack(pos, -1));
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Inject(method = "fillStateContainer", at = @At(value = "HEAD"))
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(BreakProgress);
    }
}
