package powercrystals.netherores.world;

import static powercrystals.netherores.NetherOresCore.*;

import java.util.Random;

import powercrystals.netherores.ores.Ores;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class NetherOresWorldGenHandler implements IWorldGenerator
{
	@Override
	public void generate(Random random, int chunkX, int chunkZ,
			World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		if(world.provider.dimensionId == -1 || worldGenAllDimensions.getBoolean(false))
		{
			generateNether(world, random, chunkX * 16, chunkZ * 16);
		}
	}

	private void generateNether(World world, Random random, int chunkX, int chunkZ)
	{
		if (enableWorldGen.getBoolean(true))
			for (Ores o : Ores.values()) if (o.getForced() || 
					((o.isRegisteredSmelting() ||
							o.isRegisteredMacerator() ||
							forceOreSpawn.getBoolean(false)) &&
							!o.getDisabled()))
				for (int i = o.getGroupsPerChunk(); i --> 0; )
				{
					int x = chunkX + random.nextInt(16);
					int y = o.getMinY() + random.nextInt(o.getMaxY() - o.getMinY());
					int z = chunkZ + random.nextInt(16);
					new WorldGenNetherOres(getOreBlock(o.getBlockIndex()),
							o.getMetadata(), o.getBlocksPerGroup()).generate(world, random, x, y, z);
				}
		
		if (enableHellfish.getBoolean(true))
		{
			int hellfishVein = hellFishPerGroup.getInt();
			int minY = hellFishMinY.getInt(), maxY = hellFishMaxY.getInt();

			for (int i = hellFishPerChunk.getInt(); i --> 0; )
			{
				int x = chunkX + random.nextInt(16); 
				int y = minY + random.nextInt(maxY - minY);
				int z = chunkZ + random.nextInt(16);
				new WorldGenNetherOres(blockHellfish, 0, hellfishVein).generate(world, random, x, y, z);
			}
		}
	}
}
