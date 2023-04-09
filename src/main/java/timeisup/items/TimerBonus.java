package timeisup.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import timeisup.ItemRegistry;
import timeisup.TimeIsUp;
import timeisup.capabilities.Timer;
import timeisup.capabilities.TimerCapability;
import timeisup.network.PacketHandler;
import timeisup.network.TimerPacket;

public class TimerBonus extends Item {

	public TimerBonus() {
		super();
		this.setRegistryName(TimeIsUp.MODID, "timer_bonus");
		this.setUnlocalizedName(TimeIsUp.MODID+".timer_bonus");
		this.setCreativeTab(ItemRegistry.TAB_TIMEISUP);
	}
		
	@Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);		
		TimerCapability cap = playerIn.getCapability(TimeIsUp.TIMER, null);
		if(cap != null)
		{
			Timer player_timer = cap.getOrCreate(worldIn);
			if(player_timer != null) {
				player_timer.addDuration(1200);
				PacketHandler.INSTANCE.sendTo(new TimerPacket(player_timer.getDuration()), ((EntityPlayerMP)playerIn));
				if(!playerIn.isCreative()) {
					playerIn.getCooldownTracker().setCooldown(this, 1800);
					itemstack.shrink(1);
				}
			}
		}
	         
	    return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);
	      
	 }
	
	 @Override
	 @SideOnly(Side.CLIENT)
	 public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		 super.addInformation(stack, worldIn, tooltip, flagIn);
		 tooltip.add(TextFormatting.GREEN+"Add 1:00 to your timer");
		 tooltip.add(TextFormatting.GOLD+"Cooldown : 1:30");
	 }
	 

}
