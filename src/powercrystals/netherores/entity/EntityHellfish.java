package powercrystals.netherores.entity;

import powercrystals.netherores.NetherOresCore;
import powercrystals.netherores.world.BlockHellfish;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityHellfish extends EntitySilverfish
{
	public EntityHellfish(World world)
	{
		super(world);
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.9D);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(1.5D);
		this.isImmuneToFire = true;
	}

	@Override
	protected void updateEntityActionState()
	{
		super.updateEntityActionState();

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
								int blockId = worldObj.getBlockId(posX + x, posY + y, posZ + z);

								if (blockId == NetherOresCore.blockHellfish.blockID)
								{
									if (!worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))
										worldObj.setBlock(posX + x, posY + y, posZ + z, Block.netherrack.blockID, 0, 3);
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
				int blockId = this.worldObj.getBlockId(posX + Facing.offsetsXForSide[direction], posY + Facing.offsetsYForSide[direction], posZ + Facing.offsetsZForSide[direction]);

				if(blockId == Block.netherrack.blockID && this.rand.nextInt(3) == 0)
				{
					this.worldObj.setBlock(posX + Facing.offsetsXForSide[direction], posY + Facing.offsetsYForSide[direction], posZ + Facing.offsetsZForSide[direction], NetherOresCore.blockHellfish.blockID, 0, 0);
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
	public float getBlockPathWeight(int par1, int par2, int par3)
	{
		return this.worldObj.getBlockId(par1, par2 - 1, par3) == Block.netherrack.blockID ? 10.0F : (-this.worldObj.getLightBrightness(par1, par2, par3));
	}
}
