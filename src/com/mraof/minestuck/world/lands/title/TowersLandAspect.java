package com.mraof.minestuck.world.lands.title;

import com.mraof.minestuck.util.EnumAspect;
import com.mraof.minestuck.world.biome.ModBiomes;
import com.mraof.minestuck.world.lands.decorator.PillarDecorator;
import com.mraof.minestuck.world.lands.decorator.structure.BasicTowerDecorator;
import com.mraof.minestuck.world.lands.gen.ChunkProviderLands;
import com.mraof.minestuck.world.lands.structure.blocks.StructureBlockRegistry;
import net.minecraft.block.Blocks;

public class TowersLandAspect extends TitleLandAspect
{
	public TowersLandAspect()
	{
		super(EnumAspect.HOPE);
	}
	
	@Override
	public String[] getNames()
	{
		return new String[] {"tower"};
	}
	
	@Override
	public void registerBlocks(StructureBlockRegistry registry)
	{
		registry.setBlockState("structure_wool_2", Blocks.LIGHT_BLUE_WOOL.getDefaultState());
		registry.setBlockState("carpet", Blocks.YELLOW_CARPET.getDefaultState());
	}
	
	//@Override
	public void prepareChunkProviderServer(ChunkProviderLands chunkProvider)
	{
		chunkProvider.decorators.add(new BasicTowerDecorator());
		chunkProvider.decorators.add(new PillarDecorator("structure_primary", 1, true, ModBiomes.mediumRough));
		//chunkProvider.sortDecorators();
	}
}