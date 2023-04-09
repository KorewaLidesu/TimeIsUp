package timeisup.events.custom;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class TimeIsUpTickEvent extends Event {
	
	private final World world;
	private final EntityPlayerMP player;
	private final int currentTick;
	public TimeIsUpTickEvent(World world, EntityPlayerMP player, int currentTick) {
		this.world = world;
		this.player = player;
		this.currentTick = currentTick;
	}
	
	public EntityPlayerMP getPlayer() {
		return this.player;
	}
	
	public World getWorld() {
		return this.world;
	}
	
	public int getTick() {
		return this.currentTick;
	}
	
	public static class DangerousEvent extends TimeIsUpTickEvent {

		public DangerousEvent(World world, EntityPlayerMP player, int currentTick) {
			super(world, player, currentTick);
		}
		
	}
	
	public static class EmergencyEvent extends TimeIsUpTickEvent {

		public EmergencyEvent(World world, EntityPlayerMP player, int currentTick) {
			super(world, player, currentTick);
		}
		
	}
	
	public static class TimeIsUpEvent extends TimeIsUpTickEvent {

		public TimeIsUpEvent(World world, EntityPlayerMP player, int currentTick) {
			super(world, player, currentTick);
		}
		
	}

}
