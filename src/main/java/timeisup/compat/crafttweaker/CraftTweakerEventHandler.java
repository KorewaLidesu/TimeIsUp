package timeisup.compat.crafttweaker;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import timeisup.events.custom.TimeIsUpTickEvent;

public class CraftTweakerEventHandler {

	
	@SubscribeEvent
	public void onDangerous(TimeIsUpTickEvent.DangerousEvent event) {
		if(CTEventManager.INSTANCE.hasDangerous())
			CTEventManager.INSTANCE.publishDangerous(new DangerousEvent(event));
	}
	
	@SubscribeEvent
	public void onEmergency(TimeIsUpTickEvent.EmergencyEvent event) {
		if(CTEventManager.INSTANCE.hasEmergency())
			CTEventManager.INSTANCE.publishEmergency(new EmergencyEvent(event));
	}
	
	@SubscribeEvent
	public void onTimeIsUp(TimeIsUpTickEvent.TimeIsUpEvent event) {
		if(CTEventManager.INSTANCE.hasTimeIsUp())
			CTEventManager.INSTANCE.publishTimeIsUp(new TimeIsUpEvent(event));
	}
	
	@SubscribeEvent
	public void onTimerTick(TimeIsUpTickEvent event) {
		if(CTEventManager.INSTANCE.hasTimerTick())
			CTEventManager.INSTANCE.publishTimerTick(new BaseEvent(event));
	}
	
}
