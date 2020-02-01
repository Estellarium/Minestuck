package com.mraof.minestuck.block.multiblock;

import com.mraof.minestuck.block.CruxtruderBlock;
import com.mraof.minestuck.block.MSBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.RegistryObject;

import static com.mraof.minestuck.block.MSBlockShapes.*;

public class CruxtruderMultiblock extends MachineMultiblock
{
	public final RegistryObject<Block> CORNER = register("cruxtruder_corner", () -> new CruxtruderBlock(this, CRUXTRUDER_BASE_CORNER, false, new BlockPos(1, 1, 1), Block.Properties.create(Material.IRON).hardnessAndResistance(3.0F).noDrops()));
	public final RegistryObject<Block> SIDE = register("cruxtruder_side", () -> new CruxtruderBlock(this, CRUXTRUDER_BASE_SIDE, false, new BlockPos(0, 1, 1), Block.Properties.create(Material.IRON).hardnessAndResistance(3.0F).noDrops()));
	public final RegistryObject<Block> CENTER = register("cruxtruder_center", () -> new CruxtruderBlock(this, CRUXTRUDER_CENTER, true, new BlockPos(0, 1, 0), Block.Properties.create(Material.IRON).hardnessAndResistance(3.0F).noDrops()));
	public final RegistryObject<Block> TUBE = register("cruxtruder_tube", () -> new CruxtruderBlock(this, CRUXTRUDER_TUBE, false, new BlockPos(0, 0, 0), Block.Properties.create(Material.IRON).hardnessAndResistance(3.0F).noDrops()));
	
	public CruxtruderMultiblock(String modId)
	{
		super(modId);
		registerPlacement(new BlockPos(0, 0, 0), applyDirection(CORNER, Direction.NORTH));
		registerPlacement(new BlockPos(1, 0, 0), applyDirection(SIDE, Direction.NORTH));
		registerPlacement(new BlockPos(2, 0, 0), applyDirection(CORNER, Direction.EAST));
		registerPlacement(new BlockPos(2, 0, 1), applyDirection(SIDE, Direction.EAST));
		registerPlacement(new BlockPos(2, 0, 2), applyDirection(CORNER, Direction.SOUTH));
		registerPlacement(new BlockPos(1, 0, 2), applyDirection(SIDE, Direction.SOUTH));
		registerPlacement(new BlockPos(0, 0, 2), applyDirection(CORNER, Direction.WEST));
		registerPlacement(new BlockPos(0, 0, 1), applyDirection(SIDE, Direction.WEST));
		registerPlacement(new BlockPos(1, 0, 1), applyDirection(CENTER, Direction.NORTH));
		registerPlacement(new BlockPos(1, 1, 1), applyDirection(TUBE, Direction.NORTH));
		//noinspection Convert2MethodRef
		registerPlacement(new BlockPos(1, 2, 1), () -> MSBlocks.CRUXTRUDER_LID.getDefaultState(), (state1, state2) -> true);
	}
}