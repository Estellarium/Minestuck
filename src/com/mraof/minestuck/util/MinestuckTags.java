package com.mraof.minestuck.util;

import com.mraof.minestuck.Minestuck;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class MinestuckTags
{
	public static class Blocks
	{
		public static final Tag<Block> GLOWING_LOGS = tag("logs/glowing");
		public static final Tag<Block> FROST_LOGS = tag("logs/frost");
		public static final Tag<Block> RAINBOW_LOGS = tag("logs/rainbow");
		public static final Tag<Block> END_LOGS = tag("logs/end");
		public static final Tag<Block> VINE_LOGS = tag("logs/vine");
		public static final Tag<Block> FLOWERY_VINE_LOGS = tag("logs/flowery_vine");
		public static final Tag<Block> DEAD_LOGS = tag("logs/dead");
		public static final Tag<Block> PETRIFIED_LOGS = tag("logs/petrified");
		public static final Tag<Block> ASPECT_LOGS = tag("logs/aspect");
		public static final Tag<Block> ASPECT_PLANKS = tag("planks/aspect");
		public static final Tag<Block> ASPECT_LEAVES = tag("leaves/aspect");
		public static final Tag<Block> ASPECT_SAPLINGS = tag("saplings/aspect");
		public static final Tag<Block> CRUXITE_ORES = tag("ores/cruxite");
		public static final Tag<Block> URANIUM_ORES = tag("ores/uranium");
		public static final Tag<Block> CRUXITE_STORAGE_BLOCKS = tag("storage_blocks/cruxite");
		public static final Tag<Block> URANIUM_STORAGE_BLOCKS = tag("storage_blocks/uranium");
		
		private static Tag<Block> tag(String name)
		{
			return new BlockTags.Wrapper(new ResourceLocation(Minestuck.MOD_ID, name));
		}
	}
	
	public static class Items
	{
		public static final Tag<Item> GLOWING_LOGS = tag("logs/glowing");
		public static final Tag<Item> FROST_LOGS = tag("logs/frost");
		public static final Tag<Item> RAINBOW_LOGS = tag("logs/rainbow");
		public static final Tag<Item> END_LOGS = tag("logs/end");
		public static final Tag<Item> VINE_LOGS = tag("logs/vine");
		public static final Tag<Item> FLOWERY_VINE_LOGS = tag("logs/flowery_vine");
		public static final Tag<Item> DEAD_LOGS = tag("logs/dead");
		public static final Tag<Item> PETRIFIED_LOGS = tag("logs/petrified");
		public static final Tag<Item> ASPECT_LOGS = tag("logs/aspect");
		public static final Tag<Item> ASPECT_PLANKS = tag("planks/aspect");
		public static final Tag<Item> ASPECT_LEAVES = tag("leaves/aspect");
		public static final Tag<Item> ASPECT_SAPLINGS = tag("saplings/aspect");
		public static final Tag<Item> CRUXITE_ORES = tag("ores/cruxite");
		public static final Tag<Item> URANIUM_ORES = tag("ores/uranium");
		public static final Tag<Item> CRUXITE_STORAGE_BLOCKS = tag("storage_blocks/cruxite");
		public static final Tag<Item> URANIUM_STORAGE_BLOCKS = tag("storage_blocks/uranium");
		
		private static Tag<Item> tag(String name)
		{
			return new ItemTags.Wrapper(new ResourceLocation(Minestuck.MOD_ID, name));
		}
	}
}