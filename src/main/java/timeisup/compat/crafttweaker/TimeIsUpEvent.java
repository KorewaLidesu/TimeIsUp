package timeisup.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import timeisup.events.custom.TimeIsUpTickEvent;

@ZenClass("crafttweaker.api.event.timeisup.TimeIsUpEvent")
@ZenRegister()
public class TimeIsUpEvent extends BaseEvent {

	public TimeIsUpEvent(TimeIsUpTickEvent.TimeIsUpEvent event) {
		super(event);
	}
}
