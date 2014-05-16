package powercrystals.netherores.ores;

import appeng.api.AEApi;
import appeng.api.features.IGrinderEntry;
import appeng.api.features.IGrinderRegistry;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry;

import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.Recipes;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

import powercrystals.netherores.NetherOresCore;

public enum Ores
{
	/*Name, Chunk, Group, Smelt, Pulv*/
	Coal(       8,    16,     2,    4),
	Diamond(    4,     3,     2,    5),
	Gold(       8,     6,     2,    4),
	Iron(       8,     8,     2,    4),
	Lapis(      6,     6,     2,   24),
	Redstone(   6,     8,     2,   24),
	Copper(     8,     8,     2,    4),
	Tin(        8,     8,     2,    4),
	Emerald(    3,     2,     2,    5),
	Silver(     6,     4,     2,    4),
	Lead(       6,     6,     2,    4),
	Uranium(    3,     2,     2,    4, "crushed"),
	Nikolite(   8,     4,     2,   24),
	Ruby(       6,     3,     2,    5),
	Peridot(    6,     3,     2,    5),
	Sapphire(   6,     3,     2,    5),

	Platinum(   1,     3,     2,    4),
	Nickel(     4,     6,     2,    4),
	Steel(      3,     4,     2,    4),
	Iridium(    1,     2,     2,    4, "drop"),
	Osmium(     8,     7,     2,    4),
	Sulfur(    12,    12,     2,   24),
	Titanium(   3,     2,     2,    4),
	Mithril(    6,     6,     2,    4),
	Adamantium( 5,     4,     2,    4),
	Rutile(     3,     4,     2,    4),
	Tungsten(   8,     8,     2,    4),
	Amber(      5,     6,     2,    5),
	Tennantite( 8,     8,     2,    4),
	Salt(       5,     5,     2,   12, "food"),
	Saltpeter(  6,     4,     2,   10);

	private int _blockIndex;
	private int _metadata;
	private String _secondary;
	private boolean _registeredSmelting;
	private boolean _registeredMacerator;
	private int _oreGenMinY = 1;
	private int _oreGenMaxY = 127;
	private int _oreGenGroupsPerChunk = 6;
	private int _oreGenBlocksPerGroup = 14;
	private boolean _oreGenDisable = false;
	private boolean _oreGenForced = false;
	private int _smeltCount;
	private int _pulvCount;
	private int _miningLevel;

	private Ores(int groupsPerChunk, int blocksPerGroup, int smeltCount, int maceCount)
	{
		this(groupsPerChunk, blocksPerGroup, smeltCount, maceCount, "gem");
	}

	private Ores(int groupsPerChunk, int blocksPerGroup,
			int smeltCount, int maceCount, String secondaryType)
	{
		int meta = ordinal();
		_blockIndex = meta / 16;
		_metadata = meta % 16;
		_oreGenGroupsPerChunk = groupsPerChunk;
		_oreGenBlocksPerGroup = blocksPerGroup;
		_smeltCount = smeltCount;
		_pulvCount = maceCount;
		_miningLevel = 2;
		_secondary = secondaryType;
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
		return "ore" + name();
	}

	public String getDustName()
	{
		return "dust" + name();
	}

	public String getAltName()
	{
		return _secondary + name();
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
	
	public ItemStack getItemStack(int amt)
	{
		return new ItemStack(NetherOresCore.getOreBlock(_blockIndex), amt, _metadata);
	}

	public void load()
	{
		NetherOresCore.getOreBlock(_blockIndex).setHarvestLevel("pickaxe", _miningLevel, _metadata);
		if (_oreGenForced | !_oreGenDisable)
		{
			ItemStack oreStack = getItemStack(1);
			OreDictionary.registerOre("oreNether" + name(), oreStack);
			GameRegistry.registerCustomItemStack("netherOresBlock" + name(), oreStack);
		}
	}

	public void registerSmelting(ItemStack smeltStack)
	{
		if (_registeredSmelting)
			return;
		_registeredSmelting = true;
		if(NetherOresCore.enableStandardFurnaceRecipes.getBoolean(true))
		{
			ItemStack smeltTo = smeltStack.copy();
			smeltTo.stackSize = _smeltCount;
			FurnaceRecipes.smelting().func_151394_a(getItemStack(1), smeltTo, 1F);
		}

		if(NetherOresCore.enableInductionSmelterRecipes.getBoolean(true) &&
				Loader.isModLoaded("ThermalExpansion"))
		{
			ItemStack input = getItemStack(1);
			ItemStack regSec = new ItemStack(Blocks.sand);
			ItemStack slagRich = GameRegistry.findItemStack("ThermalExpansion", "slagRich", 1);
			ItemStack slag = GameRegistry.findItemStack("ThermalExpansion", "slag", 1);
			ItemStack smeltToReg = smeltStack.copy();
			smeltToReg.stackSize = _smeltCount;
			ItemStack smeltToRich = smeltStack.copy();
			smeltToRich.stackSize = _smeltCount + (int)Math.ceil(_smeltCount / 3f);

			NBTTagCompound toSend = new NBTTagCompound();
			toSend.setInteger("energy", 3200);
			toSend.setTag("primaryInput", new NBTTagCompound());
			toSend.setTag("secondaryInput", new NBTTagCompound());
			toSend.setTag("primaryOutput", new NBTTagCompound());
			toSend.setTag("secondaryOutput", new NBTTagCompound());
			input.writeToNBT(toSend.getCompoundTag("primaryInput"));
			regSec.writeToNBT(toSend.getCompoundTag("secondaryInput"));
			smeltToReg.writeToNBT(toSend.getCompoundTag("primaryOutput"));
			slagRich.writeToNBT(toSend.getCompoundTag("secondaryOutput"));
			toSend.setInteger("secondaryChance", 10);
			FMLInterModComms.sendMessage("ThermalExpansion", "SmelterRecipe", toSend);

			toSend = new NBTTagCompound();
			toSend.setInteger("energy", 4000);
			toSend.setTag("primaryInput", new NBTTagCompound());
			toSend.setTag("secondaryInput", new NBTTagCompound());
			toSend.setTag("primaryOutput", new NBTTagCompound());
			toSend.setTag("secondaryOutput", new NBTTagCompound());
			input.writeToNBT(toSend.getCompoundTag("primaryInput"));
			slagRich.writeToNBT(toSend.getCompoundTag("secondaryInput"));
			smeltToRich.writeToNBT(toSend.getCompoundTag("primaryOutput"));
			slag.writeToNBT(toSend.getCompoundTag("secondaryOutput"));
			toSend.setInteger("secondaryChance", 100);
			FMLInterModComms.sendMessage("ThermalExpansion", "SmelterRecipe", toSend);
		}
	}

	public void registerPulverizing(ItemStack maceStack)
	{
		if (_registeredMacerator)
			return;
		_registeredMacerator = true;
		
		if (NetherOresCore.enableMaceratorRecipes.getBoolean(true) &&
				Loader.isModLoaded("IC2"))
		{
			registerMacerator(maceStack);
		}

		if (NetherOresCore.enablePulverizerRecipes.getBoolean(true) &&
				Loader.isModLoaded("ThermalExpansion"))
		{
			ItemStack input = getItemStack(1);
			ItemStack pulvPriTo = maceStack.copy();
			ItemStack pulvSecTo = new ItemStack(Blocks.netherrack);

			pulvPriTo.stackSize = _pulvCount;
			pulvSecTo.stackSize = 1;

			NBTTagCompound toSend = new NBTTagCompound();
			toSend.setInteger("energy", 3200);
			toSend.setTag("input", new NBTTagCompound());
			toSend.setTag("primaryOutput", new NBTTagCompound());
			toSend.setTag("secondaryOutput", new NBTTagCompound());
			input.writeToNBT(toSend.getCompoundTag("input"));
			pulvPriTo.writeToNBT(toSend.getCompoundTag("primaryOutput"));
			pulvSecTo.writeToNBT(toSend.getCompoundTag("secondaryOutput"));
			toSend.setInteger("secondaryChance", 15);
			FMLInterModComms.sendMessage("ThermalExpansion", "PulverizerRecipe", toSend);
		}

		if (NetherOresCore.enableGrinderRecipes.getBoolean(true) && 
				Loader.isModLoaded("appliedenergistics2"))
		{
			registerAEGrinder(maceStack.copy());
		}
	}
	
	@Optional.Method(modid="IC2")
	private void registerMacerator(ItemStack maceStack)
	{
		ItemStack input = getItemStack(1);
		ItemStack maceTo = maceStack.copy();
		maceTo.stackSize = _pulvCount;
		Recipes.macerator.addRecipe(new RecipeInputItemStack(input), null, maceTo.copy());
	}
	
	@Optional.Method(modid="appliedenergistics2")
	private void registerAEGrinder(ItemStack maceStack)
	{
		ItemStack maceTo = maceStack.copy();
		maceTo.stackSize = _pulvCount;

		IGrinderRegistry grinder = AEApi.instance().registries().grinder();

		for (ItemStack ore : OreDictionary.getOres(getOreName()))
		{
			IGrinderEntry recipe = grinder.getRecipeForInput(ore);

			if (recipe != null)
			{
				grinder.addRecipe(getItemStack(1), maceTo, recipe.getEnergyCost() * 2);
				return;
			}
		}
		// if there's no overworld recipe to get the energy cost from, default to 16 turns
		grinder.addRecipe(getItemStack(1), maceTo, 16);
	}

	public void loadConfig(Configuration c)
	{
		String cat = "WorldGen.Ores." + name();
		_oreGenMaxY = c.get(cat, "MaxY", _oreGenMaxY).getInt();
		_oreGenMinY = c.get(cat, "MinY", _oreGenMinY).getInt();
		if (_oreGenMinY >= _oreGenMaxY)
		{
			_oreGenMinY = _oreGenMaxY - 1;
			c.get(cat, "MinY", _oreGenMinY).set(_oreGenMinY);
		}
		
		_oreGenGroupsPerChunk = c.get(cat, "GroupsPerChunk", _oreGenGroupsPerChunk).getInt();
		_oreGenBlocksPerGroup = c.get(cat, "BlocksPerGroup", _oreGenBlocksPerGroup).getInt();
		_oreGenDisable = c.get(cat, "Disable", false, "Disables generation of " + name() +
				" (overrides ForceOreSpawn)").getBoolean(false);
		_oreGenForced = c.get(cat, "Force", false, "Force " + name() +
				" to generate (overrides Disable)").getBoolean(false);
		_miningLevel = c.get(cat, "MiningLevel", _miningLevel, "The pickaxe level required to mine " +
				name()).getInt();
		
		cat = "Processing.Ores." + name();
		_smeltCount = c.get(cat, "SmeltedCount", _smeltCount, "Output from smelting " + name()).getInt();
		_pulvCount = c.get(cat, "PulverizedCount", _pulvCount, "Output from grinding " + name()).getInt();
		_secondary = c.get(cat, "AlternateOrePrefix", _secondary, "Output from grinding " +
				 name() + " if dust" + name() + " is not found (i.e., " + _secondary + name() + ")").getString();
	}
}
