package net.fabricmc.err.nowaterredstone.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractButtonBlock;

import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

import net.fabricmc.err.nowaterredstone.Config;

@Mixin(FlowableFluid.class)
public abstract class FlowableFluidMixin {

	private boolean isRedstoneBlock(BlockState state) {
		if (Config.redstoneBlocks().contains("minecraft:button") && state.getBlock() instanceof AbstractButtonBlock) return true;
		String blockID = Registry.BLOCK.getId(state.getBlock()).toString();
		return Config.redstoneBlocks().contains(blockID);
	}

	@Inject(method = "canFlow", at = @At("RETURN"), cancellable = true)
	protected void canFlow(
		BlockView world,
		BlockPos fluidPos,
		BlockState fluidBlockState,
		Direction flowDirection,
		BlockPos flowTo,
		BlockState flowToBlockState,
		FluidState fluidState,
		Fluid fluid,
		CallbackInfoReturnable<Boolean> info
	) {
		if (Config.enable() && fluid.matchesType(Fluids.WATER) && isRedstoneBlock(flowToBlockState)) {
			info.setReturnValue(false);
		}
	}
}
