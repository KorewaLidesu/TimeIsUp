package timeisup.items;

import net.minecraft.item.Item;
import timeisup.ItemRegistry;
import timeisup.TimeIsUp;

public class WardHead extends Item {

	public WardHead() {
		super();
		this.setRegistryName(TimeIsUp.MODID, "timer_ward_head");
		this.setTranslationKey(TimeIsUp.MODID+".timer_ward_head");
		this.setCreativeTab(ItemRegistry.TAB_TIMEISUP);
	}

}
