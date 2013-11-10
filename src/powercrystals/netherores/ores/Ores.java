package powercrystals.netherores.ores;

import appeng.api.IAppEngGrinderRecipe;
import appeng.api.IGrinderRecipeManager;
import appeng.api.Util;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry;

import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.Recipes;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;

import powercrystals.netherores.NetherOresCore;

public enum Ores
{
	coal("Coal", 8, 16, 2, 4),
	diamond("Diamond", 4, 3, 2, 4),
	gold("Gold", 8, 6, 2, 4),
	iron("Iron", 8, 8, 2, 4),
	lapis("Lapis", 6, 6, 2, 24),
	redstone("Redstone", 6, 8, 2, 24),
	copper("Copper", 8, 8, 2, 4),
	tin("Tin", 8, 8, 2, 4),
	emerald("Emerald", 3, 2, 2, 4),
	silver("Silver", 6, 4, 2, 4),
	lead("Lead", 6, 6, 2, 4),
	uranium("Uranium", 3, 2, 2, 4),
	nikolite("Nikolite", 8, 4, 2, 24),
	ruby("Ruby", 6, 3, 2, 4),
	peridot("Peridot", 6, 3, 2, 4),
	sapphire("Sapphire", 6, 3, 2, 4),

	platinum("Platinum", 3, 3, 2, 4),
	nickel("Nickel", 4, 6, 2, 4),
	pigiron("Steel", 3, 4, 2, 4),
	iridium("Iridium", 1, 2, 2, 4),
	osmium("Osmium", 8, 7, 2, 4);

	private int _blockIndex;
	private int _metadata;
	private String _oreName;
	private String _netherOreName;
	private String _dustName;
	private boolean _registeredSmelting;
	private boolean _registeredMacerator;
	private int _oreGenMinY = 1;
	private int _oreGenMaxY = 126;
	private int _oreGenGroupsPerChunk = 6;
	private int _oreGenBlocksPerGroup = 14;
	private boolean _oreGenDisable = false;
	private int _smeltCount;
	private int _maceCount;
	private Ores(String oreSuffix, int groupsPerChunk, int blocksPerGroup, int smeltCount, int maceCount)
	{
		int meta = this.ordinal();
		_blockIndex = meta / 16;
		_metadata = meta % 16;
		_oreName = "ore" + oreSuffix;
		_dustName = "dust" + oreSuffix;
		_netherOreName = "oreNether" + oreSuffix;
		_oreGenGroupsPerChunk = groupsPerChunk;
		_oreGenBlocksPerGroup = blocksPerGroup;
		_smeltCount = smeltCount;
		_maceCount = maceCount; 
	}

	public int getBlockIndex()
	{
		return _blockIndex;
	}

	public int getMetadata()
	{
		return _metadata;
	}

	public String getOreName()
	{
		return _oreName;
	}

	public String getDustName()
	{
		return _dustName;
	}

	public boolean isRegisteredSmelting()
	{
		return _registeredSmelting;
	}

	public boolean isRegisteredMacerator()
	{
		return _registeredMacerator;
	}

	public int getMaxY()
	{
		return _oreGenMaxY;
	}

	public int getMinY()
	{
		return _oreGenMinY;
	}

	public int getGroupsPerChunk()
	{
		return _oreGenGroupsPerChunk;
	}

	public int getBlocksPerGroup()
	{
		return _oreGenBlocksPerGroup;
	}

	public boolean getDisabled()
	{
		return _oreGenDisable;
	}

	public void load()
	{
		MinecraftForge.setBlockHarvestLevel(NetherOresCore.getOreBlock(_blockIndex),
				_metadata, "pickaxe", 2);
		ItemStack oreStack = new ItemStack(NetherOresCore.getOreBlock(_blockIndex), 1, _metadata);
		OreDictionary.registerOre(_netherOreName, oreStack);
	}

	public void postLoad()
	{
	}

	public void registerSmelting(ItemStack smeltStack)
	{
		_registeredSmelting = true;
		if(NetherOresCore.enableStandardFurnaceRecipes.getBoolean(true))
		{
			ItemStack smeltTo = smeltStack.copy();
			smeltTo.stackSize = _smeltCount;
			FurnaceRecipes.smelting().
				addSmelting(NetherOresCore.getOreBlock(_blockIndex).blockID, _metadata, smeltTo, 1F);
		}

		if(NetherOresCore.enableInductionSmelterRecipes.getBoolean(true) &&
				Loader.isModLoaded("ThermalExpansion"))
		{
			ItemStack input = new ItemStack(NetherOresCore.getOreBlock(_blockIndex), 1, _metadata);
			ItemStack regSec = new ItemStack(Block.sand);
			ItemStack slagRich = GameRegistry.findItemStack("ThermalExpansion", "slagRich", 1);
			ItemStack slag = GameRegistry.findItemStack("ThermalExpansion", "slag", 1);
			ItemStack smeltToReg = smeltStack.copy();
			ItemStack smeltToRich = smeltStack.copy();

			smeltToReg.stackSize += 1;
			smeltToRich.stackSize += 2;

			NBTTagCompound toSend = new NBTTagCompound();
			toSend.setInteger("energy", 320);
			toSend.setCompoundTag("primaryInput", new NBTTagCompound());
			toSend.setCompoundTag("secondaryInput", new NBTTagCompound());
			toSend.setCompoundTag("primaryOutput", new NBTTagCompound());
			toSend.setCompoundTag("secondaryOutput", new NBTTagCompound());
			input.writeToNBT(toSend.getCompoundTag("primaryInput"));
			regSec.writeToNBT(toSend.getCompoundTag("secondaryInput"));
			smeltToReg.writeToNBT(toSend.getCompoundTag("primaryOutput"));
			slagRich.writeToNBT(toSend.getCompoundTag("secondaryOutput"));
			toSend.setInteger("secondaryChance", 10);
			sendComm("SmelterRecipe", toSend);

			toSend = new NBTTagCompound();
			toSend.setInteger("energy", 400);
			toSend.setCompoundTag("primaryInput", new NBTTagCompound());
			toSend.setCompoundTag("secondaryInput", new NBTTagCompound());
			toSend.setCompoundTag("primaryOutput", new NBTTagCompound());
			toSend.setCompoundTag("secondaryOutput", new NBTTagCompound());
			input.writeToNBT(toSend.getCompoundTag("primaryInput"));
			slagRich.writeToNBT(toSend.getCompoundTag("secondaryInput"));
			smeltToReg.writeToNBT(toSend.getCompoundTag("primaryOutput"));
			slag.writeToNBT(toSend.getCompoundTag("secondaryOutput"));
			toSend.setInteger("secondaryChance", 80);
			sendComm("SmelterRecipe", toSend);
		}
	}

	public void registerMacerator(ItemStack maceStack)
	{
		_registeredMacerator = true;
		if(NetherOresCore.enableMaceratorRecipes.getBoolean(true) && Loader.isModLoaded("IC2"))
		{
			ItemStack maceTo = maceStack.copy();
			maceTo.stackSize = _maceCount;

			Method m = null;
			try
			{

				for (Method t : IMachineRecipeManager.class.getDeclaredMethods())
					if (t.getName().equals("addRecipe"))
					{
						m = t;
						break;
					}
				m.invoke(Recipes.macerator,
						new ItemStack(NetherOresCore.getOreBlock(_blockIndex), 1, _metadata),
						maceTo.copy());
			}
			catch (Throwable _)
			{
				try
				{
					Class<?> clazz = Class.forName("ic2.api.recipe.RecipeInputItemStack");
					Constructor<?> c = clazz.getDeclaredConstructor(ItemStack.class);
					Object o = c.newInstance(new ItemStack(NetherOresCore.getOreBlock(_blockIndex),
							1, _metadata));
					m.invoke(Recipes.macerator, o, null, new ItemStack[] {maceTo.copy()});
				}
				catch (Throwable e)
				{
					e.printStackTrace();
					_registeredMacerator = false;
				}
			}
		}

		if(NetherOresCore.enablePulverizerRecipes.getBoolean(true) &&
				Loader.isModLoaded("ThermalExpansion"))
		{
			ItemStack input = new ItemStack(NetherOresCore.getOreBlock(_blockIndex), 1, _metadata);
			ItemStack pulvPriTo = maceStack.copy();
			ItemStack pulvSecTo = new ItemStack(Block.netherrack);

			pulvPriTo.stackSize = _maceCount;
			pulvSecTo.stackSize = 1;

			NBTTagCompound toSend = new NBTTagCompound();
			toSend.setInteger("energy", 320);
			toSend.setCompoundTag("input", new NBTTagCompound());
			toSend.setCompoundTag("primaryOutput", new NBTTagCompound());
			toSend.setCompoundTag("secondaryOutput", new NBTTagCompound());
			input.writeToNBT(toSend.getCompoundTag("input"));
			pulvPriTo.writeToNBT(toSend.getCompoundTag("primaryOutput"));
			pulvSecTo.writeToNBT(toSend.getCompoundTag("secondaryOutput"));
			toSend.setInteger("secondaryChance", 15);
			sendComm("PulverizerRecipe", toSend);
		}

		appeng: if(NetherOresCore.enableGrinderRecipes.getBoolean(true) && 
				Loader.isModLoaded("AppliedEnergistics"))
		{
			ItemStack maceTo = maceStack.copy();
			maceTo.stackSize = _maceCount;

			IGrinderRecipeManager grinder = Util.getGrinderRecipeManage();

			for(ItemStack ore : OreDictionary.getOres(_oreName))
			{
				IAppEngGrinderRecipe recipe = grinder.getRecipeForInput(ore);

				if(recipe != null)
				{
					grinder.addRecipe(new ItemStack(NetherOresCore.getOreBlock(_blockIndex), 1,
							_metadata), maceTo, recipe.getEnergyCost() * 2);
					break appeng;
				}
			}
			// if there's no overworld recipe to get the energy cost from, default to 16 turns
			grinder.addRecipe(new ItemStack(NetherOresCore.getOreBlock(_blockIndex), 1, _metadata),
					maceTo, 16);
		}
	}

	private static void sendComm(String type, NBTTagCompound msg)
	{
		FMLInterModComms.sendMessage("ThermalExpansion", type, msg);
	}

	public void loadConfig(Configuration c)
	{
		_oreGenMaxY = c.get("WorldGen", _oreName + "MaxY", _oreGenMaxY).getInt();
		_oreGenMinY = c.get("WorldGen", _oreName + "MinY", _oreGenMinY).getInt();
		_oreGenGroupsPerChunk = c.get("WorldGen", _oreName + "GroupsPerChunk", _oreGenGroupsPerChunk).
				getInt();
		_oreGenBlocksPerGroup = c.get("WorldGen", _oreName + "BlocksPerGroup", _oreGenBlocksPerGroup).
				getInt();
		_oreGenDisable = c.get("WorldGen", _oreName + "Disable", false).getBoolean(false);

		if(_oreGenMinY >= _oreGenMaxY)
		{
			_oreGenMinY = _oreGenMaxY - 1;
		}
	}
}
