package timeisup.compat.crafttweaker;

import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import timeisup.events.custom.TimeIsUpTickEvent;

public class BaseEvent implements IBaseEvent {

	private final TimeIsUpTickEvent event;

    public BaseEvent(TimeIsUpTickEvent event) {
        this.event = event;
    }
	
    @Override
    public IPlayer getPlayer()
    {
    	return CraftTweakerMC.getIPlayer(event.getPlayer());
    }

    @Override
    public IWorld getWorld() {
        return CraftTweakerMC.getIWorld(event.getWorld());
    }
    
    @Override
    public IBlockPos getPos() {
        return CraftTweakerMC.getIBlockPos(event.getPlayer().getPosition());
    }
    
    @Override
	public int getTick() {
		return event.getTick();
	}

	@Override
	public boolean isCanceled() {
		return event.isCanceled();
	}

	@Override
	public void setCanceled(boolean cancel) {
		event.setCanceled(cancel);	
	}
}