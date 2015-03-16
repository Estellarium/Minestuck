package com.mraof.minestuck.world.gen.lands.title;

import com.mraof.minestuck.world.gen.ChunkProviderLands;
import com.mraof.minestuck.world.gen.lands.decorator.RabbitSpawner;

public class LandAspectRabbits extends TitleAspect
{
	
	@Override
	public String getPrimaryName()
	{
		return "Rabbits";
	}
	
	@Override
	public String[] getNames()
	{
		return new String[] {"rabbit", "bunny"};
	}
	
	@Override
	protected void prepareChunkProvider(ChunkProviderLands chunkProvider)
	{
		if(chunkProvider.decorators != null)
		{
			chunkProvider.decorators.add(new RabbitSpawner());
			chunkProvider.sortDecorators();
		}
	}
	
}
