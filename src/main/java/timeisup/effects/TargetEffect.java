package timeisup.effects;

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import timeisup.TimeIsUp;

public class TargetEffect extends Potion {

	private static final ResourceLocation ICON = new ResourceLocation(TimeIsUp.MODID,"textures/mob_effect/target_effect.png");
	
	public TargetEffect() {
		super(false, 0xFFFFFF);
		this.setRegistryName(TimeIsUp.MODID, "target_effect");
		this.setPotionName(TimeIsUp.MODID+".target_effect");
	}

	
	@Override
	public boolean isReady(int duration, int amplifier) {
           return duration % 40 == 0;
	}

	@Override
	public void performEffect(EntityLivingBase entity, int amplifier) {
		for(EntityMob mob : entity.getEntityWorld().getEntitiesWithinAABB(EntityMob.class, entity.getEntityBoundingBox().grow(16.0D*amplifier))) {
			mob.setAttackTarget(entity);
			mob.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D*amplifier+4.0D);
		}
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
