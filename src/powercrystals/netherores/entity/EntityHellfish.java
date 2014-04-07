package powercrystals.netherores.entity;

import powercrystals.netherores.NetherOresCore;
import powercrystals.netherores.world.BlockHellfish;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityHellfish extends EntitySilverfish
{
	public EntityHellfish(World world)
	{
		super(world);
		rand.setSeed(world.getSeed() ^ this.getEntityId());
		this.isImmuneToFire = true;
		this.stepHeight = 1.0F;
		this.hurtTime = 15;
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).
		setBaseValue(12.5D);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).
		setBaseValue(0.925D + (rand.nextDouble() / 10));
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).
		setBaseValue(1.5D + (rand.nextDouble()));
		this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).
		setBaseValue(rand.nextDouble());
	}

	@Override
	protected void updateEntityActionState()
	{
		int cooldown = allySummonCooldown;
		super.updateEntityActionState();
		allySummonCooldown = cooldown;

		if(!this.worldObj.isRemote)
		{
			int posX;
			int posY;
			int posZ;

			if(this.allySummonCooldown > 0)
			{
				--this.allySummonCooldown;

				if(this.allySummonCooldown == 0)
				{
					posX = MathHelper.floor_double(this.posX);
					posY = MathHelper.floor_double(this.posY);
					posZ = MathHelper.floor_double(this.posZ);

					l: for (int y = 0; y <= 5 & y >= -5; y = y <= 0 ? 1 - y : 0 - y)
						for (int x = 0; x <= 10 & x >= -10; x = x <= 0 ? 1 - x : 0 - x)
							for (int z = 0; z <= 10 & z >= -10; z = z <= 0 ? 1 - z : 0 - z)
							{
								Block block = worldObj.getBlock(posX + x, posY + y, posZ + z);

								if (block == NetherOresCore.blockHellfish)
								{
									if (!worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))
										worldObj.setBlock(posX + x, posY + y, posZ + z, Blocks.netherrack, 0, 3);
									else
										worldObj.destroyBlock(posX + x, posY + y, posZ + z, false);
									BlockHellfish.spawnHellfish(worldObj, posX + x, posY + y, posZ + z);

									if (rand.nextBoolean())
										break l;
								}
							}
				}
			}

			if(this.entityToAttack == null && !this.hasPath())
			{
				posX = MathHelper.floor_double(this.posX);
				posY = MathHelper.floor_double(this.posY + 0.5D);
				posZ = MathHelper.floor_double(this.posZ);
				int direction = this.rand.nextInt(6);
				Block block = this.worldObj.getBlock(posX + Facing.offsetsXForSide[direction], posY + Facing.offsetsYForSide[direction], posZ + Facing.offsetsZForSide[direction]);

				if(block == Blocks.netherrack && this.rand.nextInt(3) == 0)
				{
					this.worldObj.setBlock(posX + Facing.offsetsXForSide[direction], posY + Facing.offsetsYForSide[direction], posZ + Facing.offsetsZForSide[direction], NetherOresCore.blockHellfish, 0, 0);
					this.spawnExplosionParticle();
					this.setDead();
				}
				else
				{
					this.updateWanderPath();
				}
			}
			else if (this.entityToAttack != null && !this.hasPath())
			{
				this.entityToAttack = null;
			}
		}
	}

	@Override
	public boolean attackEntityAsMob(Entity par1Entity)
	{
		par1Entity.setFire(3);
		return super.attackEntityAsMob(par1Entity);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage)
	{
		if (this.isEntityInvulnerable())
			return false;
		else
		{
			int c = allySummonCooldown;
			boolean r = super.attackEntityFrom(source, damage);
			allySummonCooldown = c;

			if (allySummonCooldown <= 0 && source instanceof EntityDamageSource)
				allySummonCooldown = 20;

			return r;
		}
	}

	@Override
	public float getBlockPathWeight(int par1, int par2, int par3)
	{
		return this.worldObj.getBlock(par1, par2 - 1, par3) == Blocks.netherrack ? 10.0F : (-this.worldObj.getLightBrightness(par1, par2, par3));
	}
}
