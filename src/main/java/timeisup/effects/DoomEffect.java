package timeisup.effects;

import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import timeisup.Configs;
import timeisup.TimeIsUp;

public class DoomEffect extends Potion {

	private static final ResourceLocation ICON = new ResourceLocation(TimeIsUp.MODID,"textures/mob_effect/doom_effect.png");
	
	public DoomEffect() {
		super(false, 0xED5B34);
		this.setRegistryName(TimeIsUp.MODID, "doom_effect");
		this.setPotionName(TimeIsUp.MODID+".doom_effect");
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
	
	@Override
	public boolean isReady(int duration, int amplifier) {
		int k = 80 >> amplifier;
        if (k > 0) {
           return duration % k == 0;
        } else {
           return true;
        }
	}
	
	@Override
	public void performEffect(EntityLivingBase entity, int amplifier) {
		if(entity instanceof EntityPlayerMP) {
			 WorldServer world = (WorldServer) entity.world;		
			 Random rand = world.rand;
			 if(rand.nextFloat() < Configs.mobSpawningChance) {
				 Biome biome = world.getBiome(entity.getPosition());
				 
				 List<SpawnListEntry> spawners = Configs.DoomSpawners.get(biome.getRegistryName());
				 if(spawners == null)
					 spawners = Configs.DoomSpawners.get(new ResourceLocation(world.provider.getDimensionType().getName()));
				 if(spawners == null)
					 spawners = biome.getSpawnableList(EnumCreatureType.MONSTER); //biome default if no config
				 
				 SpawnListEntry spawner = WeightedRandom.getRandomItem(rand, spawners);
				 int k = spawner.minGroupCount + rand.nextInt(1 + spawner.maxGroupCount - spawner.minGroupCount);
				 for(int count = 0; count < k; count++) {
					 int posX = (int)entity.posX + 8 - (int)(rand.nextFloat()*16.0F);
					 int posZ = (int)entity.posZ + 8 - (int)(rand.nextFloat()*16.0F);
					 int posY = (int)entity.posY + 6 - (int)(rand.nextFloat()*10.0F);
					 BlockPos pos = getTopSolidOrLiquidBlock(world, new BlockPos(posX, posY, posZ));
					 if (!WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.getPlacementForEntity(spawner.entityClass), world, pos))
					 {
						 return;
					 }
					 
					 EntityLiving mobentity;
					 
					 try {
							mobentity = (EntityLiving) spawner.newInstance(world);
					 } catch (Exception e) {
							e.printStackTrace();
							continue;
					 }
					 
					 mobentity.setLocationAndAngles(posX, pos.getY(), posZ, rand.nextFloat() * 360.0F, 0.0F);
					 world.spawnEntity(mobentity);
					 mobentity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(mobentity)), null);
					 mobentity.setAttackTarget(entity);
				 }
			 }
		}
	}
	
	private static BlockPos getTopSolidOrLiquidBlock(World world, BlockPos pos)
    {
        Chunk chunk = world.getChunkFromBlockCoords(pos);
        BlockPos blockpos;    
        blockpos = new BlockPos(pos.getX(), Math.min(chunk.getTopFilledSegment() + 16,pos.getY()) + 1, pos.getZ()); 
        int bottom = Math.max(0, pos.getY()-12);
    	IBlockState state = world.getBlockState(blockpos);
    	
    	while(!state.getBlock().isAir(state, world, blockpos) && blockpos.getY() > bottom) {
        	 blockpos = blockpos.down();
        	 state = world.getBlockState(blockpos);
    	}

    	while(!state.getMaterial().blocksMovement() && blockpos.getY() > 0) {
        	 blockpos = blockpos.down();
        	 state = world.getBlockState(blockpos);
        } 

        return blockpos.up();
    }
	
}
