package utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Test;

import utils.ImageUtils;

public class ImageUtilsTest {
	
	
	/** 
	 * @throws IOException
	 */
	@Test
	public void testImageConversion() throws IOException {
		//This test case tests all 3 functions in the ImageUtils class
		BufferedImage oldImage = ImageIO.read(new File("src\\main\\resources\\IMAGES\\welcome-red-sign-760.png"));
		BufferedImage newImage = ImageUtils.getImageUtils().convertInputStreamIntoBufferedImage(ImageUtils.getImageUtils().convertBufferedImageIntoInputStream(oldImage));
		float diffPercentage = ImageUtils.getImageUtils().getDifferenceHighlightedResult(oldImage, newImage).diffPercentage;
		//Since conversion function converts the image in .jpeg format, there will be a small difference in between caused by compression artifacts
		//This difference value is often around 0.5 by the first conversion.
		System.out.println("diffPercentage =" + diffPercentage);
		Assert.assertTrue(diffPercentage < 1);
	}
}
