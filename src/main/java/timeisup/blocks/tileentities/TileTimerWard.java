package timeisup.blocks.tileentities;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import timeisup.Configs;
import timeisup.TimeIsUp;
import timeisup.capabilities.TimerCapability;
import timeisup.network.PacketHandler;
import timeisup.network.WardPacket;

public class TileTimerWard extends TileEntity implements ITickable {

	private int cooldown = 20;

	@Override
	public void update() {
		
		if(!this.world.isRemote) {
			//Do nothing if there's no timer in this dimension
			if(Configs.isDisabled(this.getWorld().provider.getDimension())) 
				return;
			cooldown--;
			if(cooldown <= 0)
			{	
				cooldown = 20;
				for(EntityPlayerMP player : this.getWorld().getEntitiesWithinAABB(EntityPlayerMP.class, new AxisAlignedBB(this.getPos()).grow(5)))
				{
					TimerCapability cap = player.getCapability(TimeIsUp.TIMER, null);
					if(cap != null && !cap.isBossKilled())
					{
						if(cap.ward <= 0) //Notify the client if there's no active ward already.
						{
							PacketHandler.INSTANCE.sendTo(new WardPacket(true), player);
						}
						cap.ward = 30;
					}
				}
			}
		}
		
	}
	
	

}
