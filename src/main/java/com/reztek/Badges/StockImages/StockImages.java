package com.reztek.Badges.StockImages;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * Stock Image retriever for images that are included in the Bot
 * @author Craig Vella
 *
 */
public class StockImages {
	private static HashMap<String, BufferedImage>p_StockImages = new HashMap<String, BufferedImage>();
	
	/**
	 * Retrieves stock images based on name
	 * @param imageName - the name of the stock image to retrieve
	 * @return BufferedImage of requested image
	 */
	public static BufferedImage ImageFromName(String imageName) {
		return ImageFromName(imageName, StockImages.class);
	}
	
	/**
	 * Retrieves stock images based on name
	 * @param imageName - the name of the stock image to retrieve
	 * @param callingContext - the class whose package has the Stock Image
	 * @return BufferedImage of requested image
	 */
	public static BufferedImage ImageFromName(String imageName, Class<?> callingContext) {
		BufferedImage bi = p_StockImages.get(imageName);
		if (bi == null) {
			try {
				bi = ImageIO.read(callingContext.getResourceAsStream(imageName));
				p_StockImages.put(imageName, bi);
				return bi;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return bi;
		}
	}
}
