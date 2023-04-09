package timeisup.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import timeisup.events.custom.TimeIsUpTickEvent;

@ZenClass("crafttweaker.api.event.timeisup.EmergencyEvent")
@ZenRegister()
public class EmergencyEvent extends BaseEvent {

	public EmergencyEvent(TimeIsUpTickEvent.EmergencyEvent event) {
		super(event);
	}    
}
