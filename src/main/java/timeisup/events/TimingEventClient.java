package timeisup.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import timeisup.Configs;
import timeisup.ItemRegistry;
import timeisup.TimeIsUp;

@EventBusSubscriber(modid = TimeIsUp.MODID, value = Side.CLIENT)
public class TimingEventClient {
	
	private final static ResourceLocation CLOCK = new ResourceLocation(TimeIsUp.MODID, "textures/gui/clock.png");
	private final static ResourceLocation VIGNETTE_TEX_PATH = new ResourceLocation(TimeIsUp.MODID, "textures/misc/vignette.png");
	private final static ResourceLocation WARD_VIGNETTE_TEX_PATH = new ResourceLocation(TimeIsUp.MODID, "textures/misc/ward_vignette.png");
	private final static ResourceLocation END_WARD_VIGNETTE_TEX_PATH = new ResourceLocation(TimeIsUp.MODID, "textures/misc/end_ward_vignette.png");
	public static int ticksDuration = -1;
	public static int ward = 0;
	public static boolean warded = false;
	public static int recovered = 0;
	public static int cooldown = 0;
	public static int maxDuration = 0;
	private static int ticksRecover = 0;
	
	@SubscribeEvent
	public static void onPlayerJoin(EntityJoinWorldEvent event) {
		if(event.getEntity() == Minecraft.getMinecraft().player)
			warded = false;
	}
	
	@SubscribeEvent
	public static void TimerDraw(RenderGameOverlayEvent.Pre event) 
	{
		if(event.getType() == ElementType.ALL)
		{
			if(ticksDuration > -1)
			{
				Minecraft mc = Minecraft.getMinecraft();
				FontRenderer font = TimeIsUp.fontrenderer;
				String timer = StringUtils.ticksToElapsedTime(ticksDuration);
				ScaledResolution window = event.getResolution();			
				//text
				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();
				
				if(ward > 0)
				{
					if(ward >= 35)
						drawVignette(window, mc, 0xddaa22, ((float)(70 - ward))/35, warded ? WARD_VIGNETTE_TEX_PATH : END_WARD_VIGNETTE_TEX_PATH);
					else
						drawVignette(window, mc, 0xddaa22, ((float)(ward))/35, warded ? WARD_VIGNETTE_TEX_PATH : END_WARD_VIGNETTE_TEX_PATH);
				}
				
				
				if(ticksDuration >= Configs.dangerous)
				{
					drawTimer(timer, warded ? TextFormatting.GOLD : TextFormatting.DARK_GREEN, mc, font, window);
				}
				else
				{
					drawVignette(window, mc, mc.world.getBiome(mc.player.getPosition()).getFoliageColorAtPos(mc.player.getPosition()), ((float)(Configs.dangerous-ticksDuration))/Configs.dangerous, VIGNETTE_TEX_PATH);
					if(ticksDuration >= Configs.emergency)
					{
						drawTimer(timer, warded ? TextFormatting.GOLD : TextFormatting.RED, mc, font, window);
					}
					else if(ticksDuration > 0)
					{
						
						GlStateManager.scale(4.0f, 4.0f, 1.0f);
						drawClock(window.getScaledWidth()/8 - (font.getStringWidth(timer)-1)/2 - 6, window.getScaledHeight()/14 - font.FONT_HEIGHT/2, 0.3f, mc);
						font.drawStringWithShadow((warded ? TextFormatting.GOLD : TextFormatting.DARK_RED) + StringUtils.ticksToElapsedTime(ticksDuration), window.getScaledWidth()/8 - (font.getStringWidth(timer)-1)/2 + 6, window.getScaledHeight()/14 - font.FONT_HEIGHT/2 + 1, 0);
					}
				}
				GlStateManager.popMatrix();
			}
		}
	}
	
	private static void drawTimer(String timer, TextFormatting color, Minecraft mc, FontRenderer font, ScaledResolution window) {
		int left = Configs.Left.apply(window, font, timer);
		int top = Configs.Top.apply(window, font, timer);
		drawClock(left, top, 0.3f, mc);
		font.drawString(color+timer, left+13, top + 1, 0);	
	}
	
	private static void drawClock(int left, int top, float scale, Minecraft mc) {
		mc.getTextureManager().bindTexture(CLOCK);
		GlStateManager.pushMatrix();
		GlStateManager.scale(scale, scale, 1.0f);
		Gui.drawModalRectWithCustomSizedTexture((int)(left/scale), (int)(top/scale), 32.0f, 32.0f, 32, 32, 32, 32);
		GlStateManager.popMatrix();
	}
	
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) 
	{
		if(event.phase == Phase.END && event.side == Side.CLIENT) {
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayer player = mc.player;
			if(event.player == player) {
				if(!player.isCreative() && !player.isSpectator()) {		
						
					if(warded)
					{
						if(ticksDuration < maxDuration) {
							ticksRecover++;
							if(ticksRecover >= cooldown)
							{
								ticksRecover = 0;
								ticksDuration += recovered;
							}
						}
					}
					else if(ticksDuration > 0)
					{
						if(Configs.warnings.stream().anyMatch(tick -> tick == ticksDuration) || (ticksDuration <= Configs.emergency && ticksDuration % 20 == 0))
						{
							mc.getSoundHandler().playSound(new PositionedSoundRecord(ItemRegistry.TIMER_SOUND, SoundCategory.BLOCKS, 1.0f, 1.0f, player.getPosition()));
						}
						
						PotionEffect exileInstance = player.getActivePotionEffect(ItemRegistry.exileEffect);
						if(exileInstance != null) {
							ticksDuration -= exileInstance.getAmplifier()+1;
						}
						ticksDuration--;
					}
					else if(Configs.hardcoreTimer && !player.getEntityWorld().getWorldInfo().isHardcoreModeEnabled()) player.getEntityWorld().getWorldInfo().setHardcore(true);
				}
				if(ward > 0)
				{
					if(ward == 65)
						mc.getSoundHandler().playSound(new PositionedSoundRecord(warded ? ItemRegistry.WARD_ON_SOUND : ItemRegistry.WARD_OFF_SOUND, SoundCategory.BLOCKS, 1.0f, 1.0f, player.getPosition()));
					ward--;
				}
			}
		}
	}
	
	private static void drawVignette(ScaledResolution window, Minecraft mc, int color, float alpha, ResourceLocation picture) {
		mc.getTextureManager().bindTexture(picture);
		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
		innerBlit(0, window.getScaledWidth(), 0, window.getScaledHeight(), -90, 0.0f, 1.0f, 0.0f, 1.0f, (float)(color >> 16 & 255) / 255.0F, (float)(color >> 8 & 255) / 255.0F, (float)(color & 255) / 255.0F, alpha);
		GlStateManager.depthMask(true);
		GlStateManager.enableDepth();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	}
	
	private static void innerBlit(int x1, int x2, int y1, int y2, int blitOffset, float minU, float maxU, float minV, float maxV, float r, float g, float b, float alpha) {
		  GlStateManager.color(r, g, b, alpha*1.25F);
		  Tessellator tessellator = Tessellator.getInstance();
		  BufferBuilder bufferbuilder = tessellator.getBuffer();
	      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
	      bufferbuilder.pos((float)x1, (float)y2, (float)blitOffset).tex(minU, maxV).endVertex();
	      bufferbuilder.pos((float)x2, (float)y2, (float)blitOffset).tex(maxU, maxV).endVertex();
	      bufferbuilder.pos((float)x2, (float)y1, (float)blitOffset).tex(maxU, minV).endVertex();
	      bufferbuilder.pos((float)x1, (float)y1, (float)blitOffset).tex(minU, minV).endVertex();
	      GlStateManager.enableAlpha();
	      tessellator.draw();
	  }

	
	@SubscribeEvent
	public static void modelRegistry(ModelRegistryEvent ev) {
		registerItem(ItemRegistry.ward_item);
		registerItem(ItemRegistry.timer_anchor);
		registerItem(ItemRegistry.timer_bonus);
		registerItem(ItemRegistry.ward_head);
	}
	
	private static void registerItem(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
	
}
