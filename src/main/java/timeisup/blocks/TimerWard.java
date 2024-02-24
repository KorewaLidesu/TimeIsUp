package timeisup.blocks;

import java.util.Random;

import net.minecraft.block.BlockTorch;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import timeisup.ItemRegistry;
import timeisup.TimeIsUp;
import timeisup.blocks.tileentities.TileTimerWard;

public class TimerWard extends BlockTorch {

	public TimerWard() {
		super();
		this.setRegistryName(TimeIsUp.MODID, "timer_ward");
		this.setTranslationKey(TimeIsUp.MODID+".timer_ward");
		this.setCreativeTab(ItemRegistry.TAB_TIMEISUP);
		this.setHardness(2.0F);
		this.setResistance(2.0F);
		this.setLightLevel(14);
		this.setSoundType(SoundType.WOOD);
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return null;
    }
		
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileTimerWard();
	}

}
