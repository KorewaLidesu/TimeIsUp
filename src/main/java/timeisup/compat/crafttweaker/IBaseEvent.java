package timeisup.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.event.IEventCancelable;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

@ZenClass("crafttweaker.api.event.timeisup.TickEvent")
@ZenRegister
public interface IBaseEvent extends IEventCancelable {
	
    @ZenGetter("player")
    public IPlayer getPlayer();

    @ZenGetter("world")
    public IWorld getWorld();
    
    @ZenGetter("pos")
    public IBlockPos getPos();
    
    @ZenGetter("tick")
    public int getTick();
}