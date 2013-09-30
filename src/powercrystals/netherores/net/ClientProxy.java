package powercrystals.netherores.net;

import cpw.mods.fml.client.registry.RenderingRegistry;
import powercrystals.netherores.entity.EntityArmedOre;
import powercrystals.netherores.entity.EntityHellfish;
import powercrystals.netherores.render.RenderHellfish;
import powercrystals.netherores.render.RendererArmedOre;

public class ClientProxy implements INetherOresProxy
{
	@Override
	public void load()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityArmedOre.class, new RendererArmedOre());
		RenderingRegistry.registerEntityRenderingHandler(EntityHellfish.class, new RenderHellfish());
	}
}
