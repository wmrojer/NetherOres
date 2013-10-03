package powercrystals.netherores.world;

import java.util.Random;

import powercrystals.netherores.NetherOresCore;
import powercrystals.netherores.entity.EntityHellfish;
import net.minecraft.block.Block;
import net.minecraft.block.BlockNetherrack;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockHellfish extends BlockNetherrack
{
	public BlockHellfish(int blockId)
	{
		super(blockId);
		setHardness(0.4F);
		setStepSound(soundStoneFootstep);
		setUnlocalizedName("netherores.hellfish");
		setTextureName("netherrack");
	}
	
	@Override
	public int idDropped(int meta, Random rand, int fortune)
	{
		return Block.netherrack.blockID;
	}
	
	@Override
	public int quantityDropped(Random rand)
	{
		return NetherOresCore.enableHellfish.getBoolean(true) ? 0 : 1;
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int fortune)
	{
		spawnHellfish(world, x, y, z);
		super.onBlockDestroyedByPlayer(world, x, y, z, fortune);
	}
	
	@Override
	public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion)
	{
		spawnHellfish(world, x, y, z);
		super.onBlockDestroyedByExplosion(world, x, y, z, explosion);
	}
	
	public static void spawnHellfish(World world, int x, int y, int z)
	{
		if(!world.isRemote && NetherOresCore.enableHellfish.getBoolean(true))
		{
			EntityHellfish hellfish = new EntityHellfish(world);
			hellfish.setLocationAndAngles(x + 0.5D, y, z + 0.5D, 0.0F, 0.0F);
			world.spawnEntityInWorld(hellfish);
			hellfish.spawnExplosionParticle();
		}
	}
}
