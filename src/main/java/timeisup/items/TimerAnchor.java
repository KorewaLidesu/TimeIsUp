package timeisup.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import timeisup.ItemRegistry;
import timeisup.TimeIsUp;
import timeisup.capabilities.Timer;
import timeisup.capabilities.TimerCapability;
import timeisup.events.TimingEventClient;
import timeisup.network.PacketHandler;
import timeisup.network.TimerPacket;

@SuppressWarnings("deprecation")
public class TimerAnchor extends Item {

	public TimerAnchor() {
		super();
		this.setRegistryName(TimeIsUp.MODID, "timer_anchor");
		this.setUnlocalizedName(TimeIsUp.MODID+".timer_anchor");
		this.setCreativeTab(ItemRegistry.TAB_TIMEISUP);
		this.setMaxDamage(11);
	}
	
	private int getDuration(NBTTagCompound nbt, World worldIn, EntityPlayer playerIn, int current_duration) {
		if(nbt.hasUniqueId("anchor_player")) {
			if(playerIn.getUniqueID().equals(nbt.getUniqueId("anchor_player")) 
					&& nbt.hasKey("anchor", 99) && nbt.hasKey("anchor_dim", 99)) {		
						int dim_id = nbt.getInteger("anchor_dim");
						if(worldIn.provider.getDimension() == dim_id) {
							int time = nbt.getInteger("anchor");
							return time - current_duration;		
						}
				}
		}
		return 0;
	}
	
	private NBTTagList newDoubleNBTList(double... numbers) {
		NBTTagList listnbt = new NBTTagList();

	      for(double d0 : numbers) {
	         listnbt.appendTag(new NBTTagDouble(d0));
	      }

	      return listnbt;
	}
	
	@Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);		
		TimerCapability cap = playerIn.getCapability(TimeIsUp.TIMER, null);
		if(cap != null)
		{
			Timer player_timer = cap.getOrCreate(worldIn);
			if(player_timer != null) {
				if(!itemstack.hasTagCompound())
					itemstack.setTagCompound(new NBTTagCompound());
				NBTTagCompound nbt = itemstack.getTagCompound();
				int substract_duration;
				if(itemstack.getItemDamage() < itemstack.getMaxDamage() - 1 && (substract_duration = getDuration(nbt, worldIn, playerIn, player_timer.getDuration())) > 0 && nbt.hasKey("anchor_pos", 9)) {
					NBTTagList listnbt = nbt.getTagList("anchor_pos", 6);
					playerIn.setPositionAndUpdate(listnbt.getDoubleAt(0), listnbt.getDoubleAt(1), listnbt.getDoubleAt(2));
					player_timer.addDuration(substract_duration/2);
					PacketHandler.INSTANCE.sendTo(new TimerPacket(player_timer.getDuration()), ((EntityPlayerMP)playerIn));
					playerIn.getCooldownTracker().setCooldown(this, substract_duration*2);
					itemstack.attemptDamageItem(1, worldIn.rand, (EntityPlayerMP) playerIn);
				} else if(playerIn.isSneaking()) {
					nbt.setUniqueId("anchor_player", playerIn.getUniqueID());
					nbt.setInteger("anchor", player_timer.getDuration());
					nbt.setInteger("anchor_dim", worldIn.provider.getDimension());
					nbt.setTag("anchor_pos", newDoubleNBTList(playerIn.posX, playerIn.posY, playerIn.posZ));
				}
			}
		}
	         
		return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);
	      
	 }
	
	 @Override
	 @SideOnly(Side.CLIENT)
	 public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		 super.addInformation(stack, worldIn, tooltip, flagIn);
		 if(stack.getItemDamage() >= stack.getMaxDamage() - 1)
			 tooltip.add(TextFormatting.GREEN+"Refill with "+ I18n.translateToLocal(Items.ENDER_PEARL.getUnlocalizedName()+".name"));
		 else if(worldIn != null) {
			 Minecraft mc = Minecraft.getMinecraft();
			 int duration = 0;
			 if(stack.hasTagCompound())
				 duration = getDuration(stack.getTagCompound(), worldIn,  mc.player, TimingEventClient.ticksDuration);
			 if(duration != 0) {
				 if(duration < 0) {
					 duration = 0;
					 tooltip.add(TextFormatting.GRAY+"Shift + right click to set to current timer");
				 }
				 tooltip.add(TextFormatting.GREEN+"Right click to recover "+ StringUtils.ticksToElapsedTime(duration/2));
				 if(stack.getTagCompound().hasKey("anchor_pos", 9)) {
					 NBTTagList listnbt = stack.getTagCompound().getTagList("anchor_pos", 6);
					 tooltip.add(TextFormatting.GREEN+"and be teleported to"+TextFormatting.YELLOW + " " + (int)listnbt.getDoubleAt(0) + " " + (int)listnbt.getDoubleAt(1) + " " + (int)listnbt.getDoubleAt(2));
				 }
				 tooltip.add(TextFormatting.GOLD+"Cooldown : "+ StringUtils.ticksToElapsedTime(duration*2));
			 } else {
				 tooltip.add(TextFormatting.GREEN+"Shift + right click to set to current timer");
			 }
			 
			 
				 
		 }
	 }
	 
	 @Override
	 public double getDurabilityForDisplay(ItemStack stack) {
		return (double) (stack.getItemDamage()+1) / (double) stack.getMaxDamage(); 
	 }

}
