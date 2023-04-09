package timeisup.font;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import timeisup.TimeIsUp;

public class CustomFontRenderer extends FontRenderer {

	public static int oversample = 1;
	public static ResourceLocation filepath;
	public static String fontname;
	private static final ResourceLocation TIME_FONT = new ResourceLocation(TimeIsUp.MODID, "custom_font");
	public static CustomFontRenderer instance;
	public static float enlarge;
	public static float[] offsets = new float[2];
	
	public CustomFontRenderer(GameSettings gameSettingsIn, TextureManager textureManagerIn) {
		super(gameSettingsIn, null, textureManagerIn, false);
	}
	
	public int[] getCharWidth() {
		return this.charWidth;
	}
	
	@Override
	public void bindTexture(ResourceLocation resource)
    {
		if(instance == null)
			instance = this;
		TextureManager render = Minecraft.getMinecraft().getTextureManager();
        ITextureObject itextureobject = render.getTexture(TIME_FONT);

        if (itextureobject == null)
        {
            itextureobject = new FontTexture();
            render.loadTexture(TIME_FONT, itextureobject);         
        }

        GlStateManager.bindTexture(itextureobject.getGlTextureId());
    }
	
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
    {
    }

	
	@Override
	protected float renderDefaultChar(int ch, boolean italic)
    {
        float i = this.charWidth[ch]/enlarge ;
      
        float img_size = 128.0F * oversample;
        
        this.bindTexture(this.locationFontTexture);
        float f = i / 16;
        float f1 = (float)((i+0.5f) * oversample);
        float f2 = (float)(ch % 16 * 8 * oversample) + f;
        float f3 = (float)((ch & 255) / 16 * 8 * oversample);
        float f4 = f1 - f - 0.01F * oversample;
        float f5 = italic ? 1.0F : 0.0F;
        GlStateManager.glBegin(5);
        GlStateManager.glTexCoord2f(f2 / img_size, f3 / img_size);
        GlStateManager.glVertex3f(this.posX + f5 + offsets[0], this.posY - 7.99F*(enlarge-1)/2 + offsets[1], 0.0F);
        GlStateManager.glTexCoord2f(f2 / img_size, (f3 + 7.99F * oversample) / img_size);
        GlStateManager.glVertex3f(this.posX - f5 + offsets[0], this.posY + 7.99F*(enlarge+1)/2 + offsets[1], 0.0F);
        GlStateManager.glTexCoord2f((f2 + f4) / img_size, f3 / img_size);
        GlStateManager.glVertex3f(this.posX + (f4 / (float)oversample + f5) * enlarge + offsets[0], this.posY - 7.99F*(enlarge-1)/2 + offsets[1], 0.0F);
        GlStateManager.glTexCoord2f((f2 + f4) / img_size, (f3 + 7.99F * oversample) / img_size);
        GlStateManager.glVertex3f(this.posX + (f4 / (float)oversample - f5) *enlarge + offsets[0], this.posY + 7.99F*(enlarge+1)/2 + offsets[1], 0.0F);
        GlStateManager.glEnd();
        return enlarge*(f1 - f) / (float)oversample;
    }
	

}
