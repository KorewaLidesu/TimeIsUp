package timeisup.compat.crafttweaker;


import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import timeisup.events.custom.TimeIsUpTickEvent;

@ZenClass("crafttweaker.api.event.timeisup.DangerousEvent")
@ZenRegister()
public class DangerousEvent extends BaseEvent {

	public DangerousEvent(TimeIsUpTickEvent.DangerousEvent event) {
		super(event);
	}
}
