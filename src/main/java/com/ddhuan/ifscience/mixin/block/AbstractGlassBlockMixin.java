package com.ddhuan.ifscience.mixin.block;

import com.ddhuan.ifscience.Config;
import com.ddhuan.ifscience.common.SoundEventRegistry;
import com.ddhuan.ifscience.network.Client.BlockBreakProgressPack;
import com.ddhuan.ifscience.network.Network;
import com.ddhuan.ifscience.network.SoundHandlerNetHelper;
import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BreakableBlock;
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

@Mixin(AbstractGlassBlock.class)
public abstract class AbstractGlassBlockMixin extends BreakableBlock {
    public AbstractGlassBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void AbstractGlassBlock(Properties properties, CallbackInfo ci) {
        this.setDefaultState(this.stateContainer.getBaseState().with(BreakProgress, 0));
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        glassTick(this, state, worldIn, pos);
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (Config.GLASS_BURST.get()) tryScheduleBurstTick(this, state, worldIn, pos);
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (Config.GLASS_BURST.get()) tryScheduleBurstTick(this, state, worldIn, pos);
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.getBlock() != state.getBlock()) {
            Network.INSTANCE.send(PacketDistributor.ALL.noArg(), new BlockBreakProgressPack(pos, -1));
            SoundHandlerNetHelper.stop(SoundEventRegistry.glassBurst.get().getName(), null);//停止玻璃开裂声
        }
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BreakProgress);
    }
}
