package powercrystals.netherores.ores;

import powercrystals.netherores.NetherOresCore;

import net.minecraft.block.BlockOre;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockNetherOverrideOre extends BlockOre
{
	public BlockNetherOverrideOre(int par1)
	{
		super(par1);
	}
	
	@Override
	public void harvestBlock(World world, EntityPlayer entityplayer, int x, int y, int z, int fortune)
	{
		super.harvestBlock(world, entityplayer, x, y, z, fortune);
		if(NetherOresCore.enableAngryPigmen.getBoolean(true))
		{
			BlockNetherOres.angerPigmen(entityplayer, world, x, y, z);
		}
	}
	
	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z)
	{
		if(player == null || !EnchantmentHelper.getSilkTouchModifier(player))
		{
			BlockNetherOres.checkExplosionChances(this, world, x, y, z);				
		}
		
		return super.removeBlockByPlayer(world, player, x, y, z);
	}
	
	@Override
	public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion)
	{
		if(NetherOresCore.enableExplosionChainReactions.getBoolean(true))
		{
			BlockNetherOres.checkExplosionChances(this, world, x, y, z);
		}
	}

}
