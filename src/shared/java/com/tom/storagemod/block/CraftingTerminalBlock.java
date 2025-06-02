package com.tom.storagemod.block;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.tom.storagemod.StorageModClient;
import com.tom.storagemod.tile.CraftingTerminalBlockEntity;

public class CraftingTerminalBlock extends AbstractStorageTerminalBlock {

	public CraftingTerminalBlock() {
		super();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CraftingTerminalBlockEntity(pos, state);
	}

	@Override
	public void appendHoverText(ItemStack stack, BlockGetter worldIn, List<Component> tooltip,
			TooltipFlag flagIn) {
		StorageModClient.tooltip("crafting_terminal", tooltip);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState state2, boolean flag) {
		if (!state.is(state2.getBlock())) {
			BlockEntity blockentity = world.getBlockEntity(pos);
			if (blockentity instanceof CraftingTerminalBlockEntity te) {
				Containers.dropContents(world, pos, te.getCraftingInv());
				world.updateNeighbourForOutputSignal(pos, this);
			}

			super.onRemove(state, world, pos, state2, flag);
		}
	}
}
