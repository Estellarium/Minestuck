package com.mraof.minestuck.item.block;

import com.mraof.minestuck.tileentity.TransportalizerTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class TransportalizerItem extends BlockItem
{
	public TransportalizerItem(Block blockIn, Properties builder)
	{
		super(blockIn, builder);
	}
	
	@Override
	protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state)
	{
		if(stack.hasCustomHoverName() && stack.getHoverName().getString().length() == 4)
		{
			BlockEntity te = level.getBlockEntity(pos);
			if(te instanceof TransportalizerTileEntity transportalizer)
				transportalizer.setId(stack.getHoverName().getString().toUpperCase());
		}
		return true;
	}
}