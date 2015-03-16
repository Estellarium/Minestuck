package com.mraof.minestuck.world.gen.lands.title;

import com.mraof.minestuck.world.gen.ChunkProviderLands;
import com.mraof.minestuck.world.gen.lands.decorator.BucketDecorator;

public class LandAspectBuckets extends TitleAspect	//Yes, buckets
{

	@Override
	public String getPrimaryName()
	{
		return "Buckets";
	}

	@Override
	public String[] getNames()
	{
		return new String[]{"bucket"};
	}
	
	@Override
	protected void prepareChunkProvider(ChunkProviderLands chunkProvider)
	{
		if(chunkProvider.decorators != null)
		{
			chunkProvider.decorators.add(new BucketDecorator());
			chunkProvider.sortDecorators();
		}
		
	}
	
}
