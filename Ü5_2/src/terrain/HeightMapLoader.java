package terrain;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class HeightMapLoader {
	public static int[][] fromImage(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		
		int[] pixels = image.getRGB(0, 0, w, h, null, 0, image.getWidth());
		int[][] grays = new int[w][h];
		
		for (int i=0; i<pixels.length; i++) {
			Color color = new Color(pixels[i]);
			int red   = color.getRed();
			int green = color.getGreen();
			int blue  = color.getBlue();
			
			grays[i % w][i / w] = (int) (red + green + blue) / 3;
		}
		
		return normalizeArray(grays);
	}
	
	private static int[][] normalizeArray(int[][] a) {
		int min = 255;
		
		for (int x=0; x<a.length; x++)
			for (int y=0; y<a[0].length; y++)
				if (a[x][y] < min) min = a[x][y];
		
		for (int x=0; x<a.length; x++)
			for (int y=0; y<a[0].length; y++)
				a[x][y] -= min;
		
		return a;
	}
}
