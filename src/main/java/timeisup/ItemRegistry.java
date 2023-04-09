package timeisup;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import timeisup.blocks.TimerWard;
import timeisup.blocks.tileentities.TileTimerWard;
import timeisup.effects.DoomEffect;
import timeisup.effects.ExileEffect;
import timeisup.effects.FrailtyEffect;
import timeisup.effects.TargetEffect;
import timeisup.items.TimerAnchor;
import timeisup.items.TimerBonus;
import timeisup.items.WardHead;
import timeisup.recipes.RefillItem;

@EventBusSubscriber()
public class ItemRegistry {

	public static final CreativeTabs TAB_TIMEISUP = new CreativeTabs(TimeIsUp.MODID) {
	      @SideOnly(Side.CLIENT)
	      public ItemStack getTabIconItem() {
	         return new ItemStack(ItemRegistry.timer_anchor);
	      }
	};
	
	public static TimerAnchor timer_anchor = new TimerAnchor();
	public static TimerBonus timer_bonus = new TimerBonus();
	public static WardHead ward_head = new WardHead();
	public static final IRecipe CRAFTING_SPECIAL_REFILLITEM = new RefillItem().setRegistryName(TimeIsUp.MODID, "refill_item");
	
	public static TimerWard ward = new TimerWard();
	public static Item ward_item = new ItemBlock(ward).setRegistryName(ward.getRegistryName()).setUnlocalizedName(ward.getUnlocalizedName());
	
	
	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event) {
		event.getRegistry().register(timer_anchor);
		event.getRegistry().register(timer_bonus);
		event.getRegistry().register(ward_item);
		event.getRegistry().register(ward_head);
	}
	
	@SubscribeEvent
    public static void registerRecipes(final RegistryEvent.Register<IRecipe> event) {
        event.getRegistry().register(CRAFTING_SPECIAL_REFILLITEM);
    }
	
	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
	    event.getRegistry().register(ward);
	}
		
	public static void init() {
		GameRegistry.registerTileEntity(TileTimerWard.class, new ResourceLocation(TimeIsUp.MODID, "tileentity_timer_ward"));
	}
	
	public static DoomEffect doomEffect = new DoomEffect();
	public static ExileEffect exileEffect = new ExileEffect();
	public static TargetEffect targetEffect = new TargetEffect();
	public static FrailtyEffect frailtyEffect = new FrailtyEffect();
	
	@SubscribeEvent
	public static void registerEffects(final RegistryEvent.Register<Potion> event) {
	    event.getRegistry().register(doomEffect);
	    event.getRegistry().register(exileEffect);
	    event.getRegistry().register(targetEffect);
	    event.getRegistry().register(frailtyEffect);
	}
	
	public static PotionType doomPotion = new PotionType(new PotionEffect(doomEffect, 800)).setRegistryName(TimeIsUp.MODID, "doom_potion");
	
	@SubscribeEvent
	public static void registerPotions(final RegistryEvent.Register<PotionType> event) {
	    event.getRegistry().register(doomPotion);
	}
	
	public static final SoundEvent TIMER_SOUND = createSoundEvent(new ResourceLocation(TimeIsUp.MODID, "timer"));
	public static final SoundEvent WARD_ON_SOUND = createSoundEvent(new ResourceLocation(TimeIsUp.MODID, "ward.on"));
	public static final SoundEvent WARD_OFF_SOUND = createSoundEvent(new ResourceLocation(TimeIsUp.MODID, "ward.off"));
	
	private static SoundEvent createSoundEvent(ResourceLocation location) {
		return new SoundEvent(location).setRegistryName(location);
	}
	
	@SubscribeEvent
	public static void registerSounds(final RegistryEvent.Register<SoundEvent> event) {
	    event.getRegistry().register(TIMER_SOUND);
	    event.getRegistry().register(WARD_ON_SOUND);
	    event.getRegistry().register(WARD_OFF_SOUND);
	}
	
}
