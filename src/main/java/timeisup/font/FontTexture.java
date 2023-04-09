package timeisup.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import timeisup.TimeIsUp;

public class FontTexture extends AbstractTexture
{

    public void loadTexture(IResourceManager resourceManager) throws IOException
    {
    	
        this.deleteGlTexture();
        IResource iresource = null;
        IResource iresource2 = null;
        InputStreamReader reader = null;
        try {
        	
        	CustomFontRenderer.fontname = null;
        	CustomFontRenderer.filepath = null;
    
    			iresource = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(TimeIsUp.MODID, "font/timer_font.json"));
    			JsonParser parser = new JsonParser();
    			reader = new InputStreamReader(iresource.getInputStream(), "UTF-8");
    			JsonObject json = (JsonObject) parser.parse(reader);
    			String font_name = json.get("name").getAsString();
    			if(font_name.isEmpty()) {
    				TimeIsUp.fontrenderer = Minecraft.getMinecraft().fontRenderer;
    				return;
    			}
    			else { 
    				CustomFontRenderer.oversample = json.has("oversample") ? json.get("oversample").getAsInt() : 1;
    				CustomFontRenderer.enlarge = json.has("scale") ? json.get("scale").getAsFloat() : 1.0f;
    				float[] offsets = json.has("offsets") ? new Gson().fromJson(json.get("offsets").getAsJsonArray(), float[].class) : null;
    				CustomFontRenderer.offsets = offsets != null && offsets.length >= 2 ? offsets  : new float[2];
	    			if(font_name.contains(".")) {
	    				CustomFontRenderer.filepath = new ResourceLocation(TimeIsUp.MODID, "font/"+font_name);
	    				iresource2 = Minecraft.getMinecraft().getResourceManager().getResource(CustomFontRenderer.filepath);
	    	        	TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), processFont(iresource2, CustomFontRenderer.oversample, CustomFontRenderer.instance.getCharWidth()), false, false); 
	    			} else {
	    				CustomFontRenderer.fontname = font_name;
	    				TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), processFont(CustomFontRenderer.fontname, CustomFontRenderer.oversample, CustomFontRenderer.instance.getCharWidth()), false, false);
	    			}
    			}
    			TimeIsUp.fontrenderer = CustomFontRenderer.instance;
        	
        } catch (FontFormatException e) {
			e.printStackTrace();
		}
        finally
        {
            IOUtils.closeQuietly((Closeable)iresource);
            IOUtils.closeQuietly((Closeable)iresource2);
            IOUtils.closeQuietly((Closeable)reader);
        }
    }
    
    private static BufferedImage processFont(Font font, int oversampling, int[] widths) throws FontFormatException, IOException {
    	int height = oversampling*8;
		font = font.deriveFont((float)height);
		BufferedImage img = new BufferedImage(128*oversampling, 128*oversampling, BufferedImage.TYPE_INT_ARGB);
		Graphics graph = img.getGraphics();
		FontMetrics m= graph.getFontMetrics(font); // g is your current Graphics object
		float size = (float)height / (float)m.getHeight() * (float)height;
		font = font.deriveFont(size);
		graph.setFont(font);
		graph.setColor(Color.WHITE);
		char[] table = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".toCharArray();
		for(int i = 0;i < 16; i++) {
			for(int j = 0;j < 16; j++) {
				graph.drawChars(table, j+i*16, 1, j*height, (i)*height+graph.getFontMetrics().getAscent());
				widths[j+i*16] = (int) (CustomFontRenderer.enlarge*8*graph.getFontMetrics().charsWidth(table, j+i*16, 1)/size+0.5f*CustomFontRenderer.enlarge);
			}
			
		}
		
		graph.dispose();
		return img;
	
    }
    
    public static BufferedImage processFont(IResource ttf, int oversampling, int[] widths) throws FontFormatException, IOException {
		Font font = Font.createFont(Font.TRUETYPE_FONT, ttf.getInputStream());
		return processFont(font, oversampling, widths);	
	}
    
    public static BufferedImage processFont(String name, int oversampling, int[] widths) throws FontFormatException, IOException {
		Font font = new Font(name, Font.PLAIN, 8);
		return processFont(font, oversampling, widths);
	}
}
