package timeisup;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import timeisup.capabilities.Timer;

@EventBusSubscriber(modid = TimeIsUp.MODID)
public class Configs {

	public static List<Integer> warnings;
	public static List<Integer> blacklist;
	public static boolean useWhitelist;
	public static int dangerous;
	public static int emergency;
	public static Timer defaultTimer;
	public static HashMap<Integer, Timer> basetimers = new HashMap<>();
	
	public static boolean potionEffect;
	
	public static List<PotionEffect> emergencyEffects;
	public static List<PotionEffect> dangerousEffects;
	public static List<PotionEffect> TimeIsUpEffects;
	public static int cooldownEffects;
	
	public static HashMap<ResourceLocation, List<SpawnListEntry>> DoomSpawners;
	public static float mobSpawningChance;
	
	public static float exileChance;
	public static int exileStrength;
	
	public static NBTTagCompound boss;
	public static String bossMessage;
	
	public static boolean globalTimer;
	public static boolean coop;
	
	public static boolean hardcoreTimer;

	private static final String BASECATEGORY = "Configs";
	private static final String EFFECTSCATEGORY = "Effects";
	private static final String BOSSCATEGORY = "Boss";
	private static Configuration configs = new Configuration(new File("config/time_is_up.cfg"));
	private static Configuration configsClient = new Configuration(new File("config/time_is_up_client.cfg"));
	
	@SideOnly(Side.CLIENT)
	public static Function3<ScaledResolution, FontRenderer, String, Integer> Top;
	
	@SideOnly(Side.CLIENT)
	public static Function3<ScaledResolution, FontRenderer, String, Integer> Left;
	
	public interface Function3<T1, T2, T3, R> {
	    R apply(T1 t1, T2 t2, T3 t3);
	}
	
	public static boolean isDisabled(Integer world) {
		return useWhitelist ^ Configs.blacklist.contains(world);
	}
	
	@SubscribeEvent
    public static void onCfgChange(ConfigChangedEvent.OnConfigChangedEvent event) {
		if(TimeIsUp.MODID.equals(event.getModID())) {
			configs.save();
			bakeConfig();
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
    public static void onClientCfgChange(ConfigChangedEvent.OnConfigChangedEvent event) {
		if(TimeIsUp.MODID.equals(event.getModID())) {
			configsClient.save();
			bakeClientConfig();
		}
	}
	
	public static void loadConfigs() {
		configs.load();
		bakeConfig();
		configs.save();
	}
	
	@SideOnly(Side.CLIENT)
	public static void loadClientConfigs() {
		configsClient.load();
		bakeClientConfig();
		configsClient.save();
	}
	
	@SideOnly(Side.CLIENT)
	private static void bakeClientConfig() {
		String[] offsets = configsClient.get(BASECATEGORY, "offset", "5:-5", "Offset position of the timer on the screen.\nFormat : 'x:y'.").getString().split(":");
		int x = Integer.parseInt(offsets[0]);
		int y = Integer.parseInt(offsets[1]);
		int position = configsClient.getInt("position", BASECATEGORY, 2, 0, 3, "Position of the timer on the screen.\n0 : top-left\n1 : top-right\n2 : bottom-left\n3 : bottom-right");;
		Top = position < 2 ? ((ScaledResolution window, FontRenderer font, String timer) -> {return y;}) : ((ScaledResolution window, FontRenderer font, String timer) -> {return window.getScaledHeight() + y - font.FONT_HEIGHT;});
		Left = position == 0 || position == 2  ? ((ScaledResolution window, FontRenderer font, String timer) -> {return x;}) : ((ScaledResolution window, FontRenderer font, String timer) -> {return window.getScaledWidth() + x - font.getStringWidth(timer) - 13;});
	}
	
	private static void bakeConfig() {		
		warnings = Arrays.asList(ArrayUtils.toObject(configs.get(BASECATEGORY, "Warnings", defaultWarnings(), "Play a warning sound at these ticks. [default: [1220, 620] ]").getIntList()));
		dangerous = configs.getInt("Dangerous", BASECATEGORY, 620, 0, Integer.MAX_VALUE, "Tick when the timer becomes red. [default:620]");
		emergency = configs.getInt("Emergency", BASECATEGORY, 220, 0, Integer.MAX_VALUE, "Tick when the timer comes in the front of the screen [default:220]");
		
		int defaultDuration = configs.getInt("Default duration", BASECATEGORY, 12000, 0, Integer.MAX_VALUE, "Default duration when the player comes for the first time in this dimension. [default:12000 = 10 minutes]");
		int defaultTickPerDuration = configs.getInt("Default ticks per duration", BASECATEGORY, 4, 1, Integer.MAX_VALUE, "Default tick amount to spend in an other dimension to recover ticks in this dimension. [default:4]");
		int defaultMaxDuration = configs.getInt("Default max duration", BASECATEGORY, 72000, 0, Integer.MAX_VALUE, "Default max duration that can be accumulated for a dimension. [default:72000 = 1 hour]");
		int defaultAmountPerRecover = configs.getInt("Default amount recovered", BASECATEGORY, 1, 1, Integer.MAX_VALUE, "Default amount of ticks recovered per tick recover. [default:1]");
		defaultTimer = new Timer(defaultDuration, defaultTickPerDuration, defaultMaxDuration, defaultAmountPerRecover);
		
		globalTimer = configs.getBoolean("Global timer", BASECATEGORY, false, "Set to true to use the same shared timer for all dimensions");
		
		coop = configs.getBoolean("Co-op", BASECATEGORY, false, "Set to true to stop the timer for all players once one of them met the conditions");
		
		hardcoreTimer = configs.getBoolean("Hardcore timer", BASECATEGORY, false, "Set to true to set the world to hardcore mode once time is up.");
		
		useWhitelist = !configs.getBoolean("Use blackList", BASECATEGORY, true, "'BlackList' is used as a blacklist if true.","'BlackList' is used as a whitelist otherwise.");
		
		blacklist = Arrays.asList(ArrayUtils.toObject(configs.get(BASECATEGORY, "BlackList", defaultBlackList(), "Dimensions where there is NO timer").getIntList()));
		
		deserializeTimers(Arrays.asList(configs.getStringList("Default timers", BASECATEGORY, new String[] {}, "Default timer for each dimension.\nFormat for one dimension : \"dimension_id;default_duration;ticks_per_duration;max_duration;amount_recovered\".\nExample : \"-1;12000;4;72000;3\" (-1 = nether)")), defaultAmountPerRecover);
		
		exileChance = configs.getFloat("Exile chances", EFFECTSCATEGORY, 0.05f, 0.0f, 1.0f, "Chances to get Exile Effect by killing a mob.");
		exileStrength = configs.getInt("Exile amplifier", EFFECTSCATEGORY, 0, 0, 255, "Exile Effect amplifier");
		
		
		deserializeDoomSpawners(Arrays.asList(configs.getStringList("Doom spawners", EFFECTSCATEGORY, new String[] {}, String.join("\n",new String[] {"Sets mobs that can be spawned by doom effect.",
				"If a dimension or biome is not set, default spawnset for biome will be used.",
				"format :  dimension_or_biome_name;entry1;entry2; ...",
				"entry format : entity_name,weight,minCount,maxCount",
				"weight - higher weight mean higher chance to spawn",
				"minCount - minimum group size",
				"maxCount - maximum group size"}))));
		mobSpawningChance = configs.getFloat("Doom spawn chance", EFFECTSCATEGORY, 0.6f, 0.0f, 1.0f, "Sets the spawn chance when doom performs its effect.\nAssuming nothing else is preventing spawning.");
		
		bossMessage = configs.getString("Boss message", BOSSCATEGORY, "\u00A76You are now free to travel across dimensions without any limit.", "Message sent when the player killed the boss.");
		String strboss = configs.getString("Boss", BOSSCATEGORY, "{id:\"minecraft:ender_dragon\"}", "NBT of the boss.\nEvery entity matching this NBT is considered as boss.\nOnce the player has killed the boss, his timers are disabled.\nLeaving this blank will disable boss functionality.");
		if(!strboss.isEmpty())
			try {
				boss = JsonToNBT.getTagFromJson(strboss);
			} catch (NBTException e1) {
				e1.printStackTrace();
			}
		
		cooldownEffects = configs.getInt("effects cooldown", EFFECTSCATEGORY, 80, 1, Integer.MAX_VALUE, "Cooldown before applying effects. [80 ticks by default]");
		potionEffect = configs.getBoolean("Potion Effects", BASECATEGORY, false, "When time is up, the player die if set to false or receive bad potion effects if set to true");
		try {
			setEffects();
		} catch (NBTException e) {
			e.printStackTrace();
		}
	}
	
	private static void setEffects() throws NBTException {
		if(potionEffect)
		{
			emergencyEffects = new ArrayList<>();
			String[] emergencyEffectsStrings = configs.getStringList("emergency effects", EFFECTSCATEGORY, defaultEmergencyEffect(), "Sets effects for emergency phase. format : effectId;duration;amplifier;show_icon");
			for(String effect : emergencyEffectsStrings)
				readEffectConfig(effect, emergencyEffects);
			
			dangerousEffects = new ArrayList<>();
			String[] dangerousEffectsStrings = configs.getStringList("dangerous effects", EFFECTSCATEGORY, defaultDangerousEffect(), "Sets effects for dangerous phase. format : effectId;duration;amplifier;show_icon");
			for(String effect : dangerousEffectsStrings)
				readEffectConfig(effect, dangerousEffects);
			
			TimeIsUpEffects = new ArrayList<>();
			String[] TimeIsUpEffectsStrings = configs.getStringList("time is up effects", EFFECTSCATEGORY, defaultTimeIsUpEffect(), "Sets effects for when the time is up. format : effectId;duration;amplifier;show_icon");
			for(String effect : TimeIsUpEffectsStrings)
				readEffectConfig(effect, TimeIsUpEffects);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void deserializeDoomSpawners(List<? extends String> list) {
		DoomSpawners = new HashMap<>();
		
		for(String serialized : list)
		{
			String[] entries = serialized.split(";");
			ResourceLocation location = new ResourceLocation(entries[0]);
			List<SpawnListEntry> spawners = new ArrayList<SpawnListEntry>();
			
			for(int index = 1; index < entries.length; index++) {
				String[] data = entries[index].split(",");
				Class<? extends Entity> entityType = EntityList.getClass(new ResourceLocation(data[0]));
				int weight = Integer.parseInt(data[1]);
				int minCount = Integer.parseInt(data[2]);
				int maxCount = Integer.parseInt(data[3]);
				
				if(entityType != null && EntityLiving.class.isAssignableFrom(entityType))
					spawners.add(new SpawnListEntry((Class<? extends EntityLiving>) entityType,weight, minCount, maxCount));
			}
			
			DoomSpawners.put(location, spawners);
			
		}
	}
	
	private static void deserializeTimers(List<? extends String> list, int defaultAmountPerRecover) {
		
		for(String serialized : list)
		{
			String[] split = serialized.split(";");
			int key = Integer.parseInt(split[0]);
			basetimers.put(key, new Timer(Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]), split.length > 4 ? Integer.parseInt(split[4]) : defaultAmountPerRecover));
		}
	}
	
	private static void readEffectConfig(String effectStr, List<PotionEffect> list) {
		String[] values = effectStr.split(";");
		Potion effect = Potion.getPotionFromResourceLocation(values[0]);
		if(effect != null)
			list.add(new PotionEffect(effect, Integer.parseInt(values[1]), Integer.parseInt(values[2]), true, Boolean.parseBoolean(values[3])));
	}
	
	private static int[] defaultWarnings() {
		return new int[] {1220,620};
	}
	
	private static int[] defaultBlackList() {
		return new int[] {0};
	}
	
	
	private static String[] defaultEmergencyEffect() {
		return new String[] {
			EffectToString(new PotionEffect(MobEffects.SLOWNESS, 120, 1)),
			EffectToString(new PotionEffect(MobEffects.MINING_FATIGUE, 120, 0)),
			EffectToString(new PotionEffect(MobEffects.POISON, 120, 0)),
			EffectToString(new PotionEffect(MobEffects.HUNGER, 120, 0)),
			EffectToString(new PotionEffect(ItemRegistry.frailtyEffect, 120, 0))
		};
	}
	
	private static String[] defaultDangerousEffect() {
		return new String[] {
				EffectToString(new PotionEffect(MobEffects.SLOWNESS, 120, 1))
		};
	}
	
	private static String[] defaultTimeIsUpEffect() {
		return new String[] {
			EffectToString(new PotionEffect(MobEffects.SLOWNESS, 120, 3)),
			EffectToString(new PotionEffect(MobEffects.MINING_FATIGUE, 120, 1)),
			EffectToString(new PotionEffect(MobEffects.POISON, 120, 2)),
			EffectToString(new PotionEffect(MobEffects.HUNGER, 120, 1)),
			EffectToString(new PotionEffect(ItemRegistry.frailtyEffect, 120, 1))
		};
	}
	
	private static String EffectToString(PotionEffect effect) {
		String id = effect.getPotion().getRegistryName().toString();
		return id+";"+effect.getDuration()+";"+effect.getAmplifier()+";"+effect.doesShowParticles();
	}
	
}
