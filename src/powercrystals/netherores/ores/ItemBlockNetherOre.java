package powercrystals.netherores.ores;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockNetherOre extends ItemBlock
{
	private String[] _oreNames =
		{ "coal", "diamond", "gold", "iron", "lapis", "redstone", "copper", "tin", "emerald", "silver", "lead", "uranium", "nikolite", "ruby", "greensapphire", "sapphire" };
	
	public ItemBlockNetherOre(int i)
	{
		super(i);
		setHasSubtypes(true);
		setMaxDamage(0);
	}
	
	@Override
	public int getMetadata(int i)
	{
		return i;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		int md = Math.min(stack.getItemDamage(), _oreNames.length);
		return "tile.blockNetherOres." + _oreNames[md];
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
	    for (int i = 0; i <= 15; i++)
	    {
	        par3List.add(new ItemStack(par1, 1, i));
	    }
    }
}
