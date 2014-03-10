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
import net.minecraftforge.common.Property;
import net.minecraftforge.oredict.OreDictionary;

import powercrystals.netherores.NetherOresCore;

public enum Ores
{
	/*Name, Chunk, Group, Smelt, Pulv*/
	Coal(       8,    16,     2,    4),
	Diamond(    4,     3,     2,    4),
	Gold(       8,     6,     2,    4),
	Iron(       8,     8,     2,    4),
	Lapis(      6,     6,     2,   24),
	Redstone(   6,     8,     2,   24),
	Copper(     8,     8,     2,    4),
	Tin(        8,     8,     2,    4),
	Emerald(    3,     2,     2,    4),
	Silver(     6,     4,     2,    4),
	Lead(       6,     6,     2,    4),
	Uranium(    3,     2,     2,    4),
	Nikolite(   8,     4,     2,   24),
	Ruby(       6,     3,     2,    4),
	Peridot(    6,     3,     2,    4),
	Sapphire(   6,     3,     2,    4),

	Platinum(   1,     3,     2,    4),
	Nickel(     4,     6,     2,    4),
	Pigiron(    3,     4,     2,    4),
	Iridium(    1,     2,     2,    4),
	Osmium(     8,     7,     2,    4),
	Sulfur(    12,    12,     2,   24),
	Titanium(   3,     2,     2,    4),
	Mythril(    6,     6,     2,    4),
	Adamantium( 5,     4,     2,    4),
	Rutile(     3,     4,     2,    4),
	Tungsten(   8,     8,     2,    4),
	Amber(      5,     6,     2,    4),
	Tennantite( 8,     8,     2,    4),
	Salt(       5,     5,     2,    8);

	private int _blockIndex;
	private int _metadata;
	private String _oreName;
	private String _gemName;
	private String _dustName;
	private String _netherOreName;
	private boolean _registeredSmelting;
	private boolean _registeredMacerator;
	private int _oreGenMinY = 1;
	private int _oreGenMaxY = 126;
	private int _oreGenGroupsPerChunk = 6;
	private int _oreGenBlocksPerGroup = 14;
	private boolean _oreGenDisable = false;
	private boolean _oreGenForced = false;
	private int _smeltCount;
	private int _pulvCount;
	private int _miningLevel;

	private Ores(int groupsPerChunk, int blocksPerGroup, int smeltCount, int maceCount)
	{
		this(groupsPerChunk, blocksPerGroup, smeltCount, maceCount, 2);
	}

	private Ores(int groupsPerChunk, int blocksPerGroup,
			int smeltCount, int maceCount, int miningLevel)
	{
		int meta = ordinal();
		String oreSuffix = name();
		_blockIndex = meta / 16;
		_metadata = meta % 16;
		_oreName = "ore" + oreSuffix;
		_gemName = "gem" + oreSuffix;
		_dustName = "dust" + oreSuffix;
		_netherOreName = "oreNether" + oreSuffix;
		_oreGenGroupsPerChunk = groupsPerChunk;
		_oreGenBlocksPerGroup = blocksPerGroup;
		_smeltCount = smeltCount;
		_pulvCount = maceCount;
		_miningLevel = miningLevel;
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

	public String getGemName()
	{
		return _gemName;
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

	public boolean getForced()
	{
		return _oreGenForced;
	}

	public int getSmeltCount()
	{
		return _smeltCount;
	}

	public int getMaceCount()
	{
		return _pulvCount;
	}

	public void load()
	{
		MinecraftForge.setBlockHarvestLevel(NetherOresCore.getOreBlock(_blockIndex),
				_metadata, "pickaxe", _miningLevel);
		if (_oreGenForced | !_oreGenDisable)
		{
			ItemStack oreStack = new ItemStack(NetherOresCore.getOreBlock(_blockIndex), 1, _metadata);
			OreDictionary.registerOre(_netherOreName, oreStack);
		}
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
			smeltToReg.stackSize = _smeltCount;
			ItemStack smeltToRich = smeltToReg.copy();
			smeltToRich.stackSize += 1;

			NBTTagCompound toSend = new NBTTagCompound();
			toSend.setInteger("energy", 3200);
			toSend.setCompoundTag("primaryInput", new NBTTagCompound());
			toSend.setCompoundTag("secondaryInput", new NBTTagCompound());
			toSend.setCompoundTag("primaryOutput", new NBTTagCompound());
			toSend.setCompoundTag("secondaryOutput", new NBTTagCompound());
			input.writeToNBT(toSend.getCompoundTag("primaryInput"));
			regSec.writeToNBT(toSend.getCompoundTag("secondaryInput"));
			smeltToReg.writeToNBT(toSend.getCompoundTag("primaryOutput"));
			slagRich.writeToNBT(toSend.getCompoundTag("secondaryOutput"));
			toSend.setInteger("secondaryChance", 10);
			FMLInterModComms.sendMessage("ThermalExpansion", "SmelterRecipe", toSend);

			toSend = new NBTTagCompound();
			toSend.setInteger("energy", 4000);
			toSend.setCompoundTag("primaryInput", new NBTTagCompound());
			toSend.setCompoundTag("secondaryInput", new NBTTagCompound());
			toSend.setCompoundTag("primaryOutput", new NBTTagCompound());
			toSend.setCompoundTag("secondaryOutput", new NBTTagCompound());
			input.writeToNBT(toSend.getCompoundTag("primaryInput"));
			slagRich.writeToNBT(toSend.getCompoundTag("secondaryInput"));
			smeltToReg.writeToNBT(toSend.getCompoundTag("primaryOutput"));
			slag.writeToNBT(toSend.getCompoundTag("secondaryOutput"));
			toSend.setInteger("secondaryChance", 80);
			FMLInterModComms.sendMessage("ThermalExpansion", "SmelterRecipe", toSend);
		}
	}

	public void registerMacerator(ItemStack maceStack)
	{
		_registeredMacerator = true;
		if(NetherOresCore.enableMaceratorRecipes.getBoolean(true) && Loader.isModLoaded("IC2"))
		{
			ItemStack maceTo = maceStack.copy();
			maceTo.stackSize = _pulvCount;

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
				}
			}
		}

		if(NetherOresCore.enablePulverizerRecipes.getBoolean(true) &&
				Loader.isModLoaded("ThermalExpansion"))
		{
			ItemStack input = new ItemStack(NetherOresCore.getOreBlock(_blockIndex), 1, _metadata);
			ItemStack pulvPriTo = maceStack.copy();
			ItemStack pulvSecTo = new ItemStack(Block.netherrack);

			pulvPriTo.stackSize = _pulvCount;
			pulvSecTo.stackSize = 1;

			NBTTagCompound toSend = new NBTTagCompound();
			toSend.setInteger("energy", 3200);
			toSend.setCompoundTag("input", new NBTTagCompound());
			toSend.setCompoundTag("primaryOutput", new NBTTagCompound());
			toSend.setCompoundTag("secondaryOutput", new NBTTagCompound());
			input.writeToNBT(toSend.getCompoundTag("input"));
			pulvPriTo.writeToNBT(toSend.getCompoundTag("primaryOutput"));
			pulvSecTo.writeToNBT(toSend.getCompoundTag("secondaryOutput"));
			toSend.setInteger("secondaryChance", 15);
			FMLInterModComms.sendMessage("ThermalExpansion", "PulverizerRecipe", toSend);
		}

		appeng: if(NetherOresCore.enableGrinderRecipes.getBoolean(true) && 
				Loader.isModLoaded("AppliedEnergistics"))
		{
			ItemStack maceTo = maceStack.copy();
			maceTo.stackSize = _pulvCount;

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

	public void loadConfig(Configuration c)
	{
		_oreGenMaxY = loadLegacy(c, "WorldGen", _oreName + ".MaxY", _oreName + "MaxY", _oreGenMaxY).getInt();
		_oreGenMinY = loadLegacy(c, "WorldGen", _oreName + ".MinY", _oreName + "MinY",_oreGenMinY).getInt();
		_oreGenGroupsPerChunk = loadLegacy(c, "WorldGen", _oreName + ".GroupsPerChunk", _oreName + "GroupsPerChunk", _oreGenGroupsPerChunk).
				getInt();
		_oreGenBlocksPerGroup = loadLegacy(c, "WorldGen", _oreName + ".BlocksPerGroup", _oreName + "BlocksPerGroup", _oreGenBlocksPerGroup).
				getInt();
		_oreGenDisable = loadLegacy(c, "WorldGen", _oreName + ".Disable", _oreName + "Disable", false).getBoolean(false);
		_oreGenForced = loadLegacy(c, "WorldGen", _oreName + ".Force", _oreName + "Force", false).getBoolean(false);
		_miningLevel = c.get("WorldGen", _oreName + ".MiningLevel", _miningLevel).getInt();
		_smeltCount = c.get("Smelting", _oreName + ".SmeltedCount", _smeltCount).getInt();
		_pulvCount = c.get("Smelting", _oreName + ".PulverizedCount", _pulvCount).getInt();

		if(_oreGenMinY >= _oreGenMaxY)
		{
			_oreGenMinY = _oreGenMaxY - 1;
		}
	}

	// TODO: remove legacy loading in 1.7
	private static Property loadLegacy(Configuration config, String category, String name,
			String oldName, int def)
	{
		Property r = null;
		String old = null;

		if (config.hasKey(category, oldName))
		{
			r = config.get(category, oldName, def);
			old = r.getString();
			deleteEntry(config, category, oldName);
		}

		r = config.get(category, name, def);
		if (old != null)
			r.set(old);
		return r;
	}
	private static Property loadLegacy(Configuration config, String category, String name,
			String oldName, boolean def)
	{
		Property r = null;
		String old = null;

		if (config.hasKey(category, oldName))
		{
			r = config.get(category, oldName, def);
			old = r.getString();
			deleteEntry(config, category, oldName);
		}

		r = config.get(category, name, def);
		if (old != null)
			r.set(old);
		return r;
	}

	private static void deleteEntry(Configuration config, String category, String name)
	{
		config.getCategory(category).remove(name);
	}
}
