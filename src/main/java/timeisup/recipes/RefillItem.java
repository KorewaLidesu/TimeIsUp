package timeisup.recipes;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import timeisup.items.TimerAnchor;

public class RefillItem extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		  boolean anchor_found = false;
		  boolean pearl_found = false;

	      for(int i = 0; i < inv.getSizeInventory(); ++i) {
	         ItemStack itemstack = inv.getStackInSlot(i);
	         if (!itemstack.isEmpty()) {
	        	 if(itemstack.getItem() instanceof TimerAnchor) {
	        		 if(anchor_found)
	        			 return false;
	        		 anchor_found = true;
	        	 } else if(itemstack.getItem() == Items.ENDER_PEARL) {
	        		 pearl_found = true;
	        	 } else {
	        		 return false;
	        	 }
	            
	         }
	      }

	      return anchor_found && pearl_found;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		  ItemStack anchor = null;
		  int pearl_amount = 0;

	      for(int i = 0; i < inv.getSizeInventory(); ++i) {
	         ItemStack itemstack = inv.getStackInSlot(i);
	         if (!itemstack.isEmpty()) {
	        	 if(itemstack.getItem() instanceof TimerAnchor) {
	        		 anchor = itemstack;
	        		 if(!anchor.isItemDamaged())
	        			 return ItemStack.EMPTY;
	        	 } else if(itemstack.getItem() == Items.ENDER_PEARL) {
	        		 pearl_amount++;
	        	 }
	            
	         }
	      }
	      
	      if(anchor != null) {
	    	  int result_dmg = anchor.getItemDamage() - pearl_amount;
	    	  if(result_dmg >= 0) {
		    	  ItemStack result = anchor.copy();
		    	  result.setItemDamage(result_dmg);
		    	  return result;
	    	  }
	      }   
	      return ItemStack.EMPTY;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 2;
	}


	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

}
