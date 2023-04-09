package timeisup.effects;

import net.minecraft.client.gui.Gui;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import timeisup.TimeIsUp;

public class FrailtyEffect extends Potion {

	private static final ResourceLocation ICON = new ResourceLocation(TimeIsUp.MODID,"textures/mob_effect/frailty_effect.png");
	
	public FrailtyEffect() {
		super(false, 0x66BAC5);
		this.setRegistryName(TimeIsUp.MODID, "frailty_effect");
		this.setPotionName(TimeIsUp.MODID+".frailty_effect");
	}	
	
	@SideOnly(Side.CLIENT)
	@Override
    public void renderHUDEffect(int x, int y, PotionEffect effect, net.minecraft.client.Minecraft mc, float alpha) { 
		mc.getTextureManager().bindTexture(ICON);
		Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
    public void renderInventoryEffect(int x, int y, PotionEffect effect, net.minecraft.client.Minecraft mc) { 
		mc.getTextureManager().bindTexture(ICON);
		Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
	}
	
}
