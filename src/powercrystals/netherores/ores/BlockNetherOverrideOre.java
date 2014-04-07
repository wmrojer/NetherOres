package powercrystals.netherores.ores;

import static powercrystals.netherores.ores.BlockNetherOres.*;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import powercrystals.netherores.NetherOresCore;

public class BlockNetherOverrideOre extends BlockOre implements INetherOre
{
	private ThreadLocal<Boolean> explode = new ThreadLocal<Boolean>() {
		@Override
		protected Boolean initialValue()
		{
			return true;
		}
	}, willAnger = new ThreadLocal<Boolean>();

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z)
	{
		boolean silky = player == null || !EnchantmentHelper.getSilkTouchModifier(player); 
		explode.set(silky);
		willAnger.set(true);
		boolean r = super.removedByPlayer(world, player, x, y, z);
		if (silky || NetherOresCore.silkyStopsPigmen.getBoolean(true))
			angerPigmen(player, world, x, y, z);
		willAnger.set(false);
		explode.set(true);
		return r;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
	{
		if (explode.get())
			checkExplosionChances(this, world, x, y, z);
		Boolean ex = willAnger.get();
		if (ex == null || !ex)
			angerPigmen(world, x, y, z);
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public void onBlockExploded(World world, int x, int y, int z, Explosion explosion)
	{
		explode.set(false);
		willAnger.set(NetherOresCore.enableMobsAngerPigmen.getBoolean(true) ||
				explosion == null || !(explosion.getExplosivePlacedBy() instanceof EntityLiving));
		super.onBlockExploded(world, x, y, z, explosion);
		willAnger.set(true);
		explode.set(true);
		if (NetherOresCore.enableExplosionChainReactions.getBoolean(true))
			checkExplosionChances(this, world, x, y, z);
	}
}
