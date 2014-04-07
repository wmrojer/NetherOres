package powercrystals.netherores.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import powercrystals.netherores.NetherOresCore;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class NOCreativeTab extends CreativeTabs
{
	public static final NOCreativeTab tab = new NOCreativeTab("Nether Ores");

	public NOCreativeTab(String label)
	{
		super(label);
	}

	@Override
	public ItemStack getIconItemStack()
	{
		return new ItemStack(NetherOresCore.blockNetherOres[0], 1, 1);
	}

	@Override
	public String getTranslatedTabLabel()
	{
		return this.getTabLabel();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem()
	{
		return null; // TODO: return ItemBlock here
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int func_151243_f()
	{
		return 1;
	}
}
