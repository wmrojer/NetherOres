package powercrystals.netherores.render;

import powercrystals.netherores.NetherOresCore;

import net.minecraft.client.renderer.entity.RenderSilverfish;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.util.ResourceLocation;

public class RenderHellfish extends RenderSilverfish
{
    private static final ResourceLocation silverfishTextures = new ResourceLocation(NetherOresCore.mobTexureFolder + "hellfish.png");

    @Override
	protected ResourceLocation getSilverfishTextures(EntitySilverfish par1EntitySilverfish)
    {
        return silverfishTextures;
    }
}
