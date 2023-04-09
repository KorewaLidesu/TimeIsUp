package timeisup;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import timeisup.capabilities.TimerCapability;
import timeisup.commands.CommandTimer;
import timeisup.compat.crafttweaker.CraftTweakerEventHandler;
import timeisup.font.CustomFontRenderer;
import timeisup.network.PacketHandler;

@Mod(modid = TimeIsUp.MODID, version = TimeIsUp.VERSION)
public class TimeIsUp {

	
	
	public static final String MODID = "timeisup";
	public static final String VERSION = "1.5.2";
		
	@CapabilityInject(TimerCapability.class)
	public final static Capability<TimerCapability> TIMER = null;
	
	@EventHandler
	private void preinit(final FMLPreInitializationEvent event) {
		TimerCapability.register();
		PacketHandler.register();
	}
	
	@EventHandler
	private void init(final FMLInitializationEvent event) {
		Configs.loadConfigs();
		if(event.getSide() == Side.CLIENT) {
			Configs.loadClientConfigs();
		}
		
		ItemRegistry.init();
		
		if(Loader.isModLoaded("crafttweaker"))
			MinecraftForge.EVENT_BUS.register(new CraftTweakerEventHandler());
	}
	
	@EventHandler
	private void initServer(final FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandTimer());
	}
	
	@SideOnly(Side.CLIENT)
	public static FontRenderer fontrenderer;
	
	@SideOnly(Side.CLIENT)
	@EventHandler
	private void initClient(final FMLInitializationEvent event) {
		FontRenderer fontrenderer = new CustomFontRenderer(Minecraft.getMinecraft().gameSettings, Minecraft.getMinecraft().getTextureManager());
		((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(fontrenderer);
	}
	
}
