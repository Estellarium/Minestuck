package com.mraof.minestuck.world.biome;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import java.util.Set;

public class LandBiomeSet
{
	public final RegistryObject<Biome> NORMAL, ROUGH, OCEAN;
	
	public LandBiomeSet(DeferredRegister<Biome> register, String name, Biome.RainType precipitation, float temperature, float downfall)
	{
		NORMAL = null;//register.register("land_"+name+"_normal", () -> new LandBiome.Normal(precipitation, temperature, downfall));
		ROUGH = null;//register.register("land_"+name+"_rough", () -> new LandBiome.Rough(precipitation, temperature, downfall));
		OCEAN = null;//register.register("land_"+name+"_ocean", () -> new LandBiome.Ocean(precipitation, temperature, downfall));
	}
	
	public Set<Biome> getAll()
	{
		return ImmutableSet.of(NORMAL.get(), ROUGH.get(), OCEAN.get());
	}
	
	public Biome fromType(BiomeType type)
	{
		switch(type)
		{
			case NORMAL: default: return NORMAL.get();
			case ROUGH: return ROUGH.get();
			case OCEAN: return OCEAN.get();
		}
	}
	
	public static LandBiomeSet getSet(ChunkGenerator generator)
	{
		/*if(settings instanceof LandGenSettings)	TODO
			return ((LandGenSettings) settings).getLandTypes().terrain.getBiomeSet();
		else*/ return MSBiomes.DEFAULT_LAND;
	}
}