package utils;

import java.awt.image.BufferedImage;

import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.model.ImageComparisonResult;

import objects.ImageDifferenceData;

public class ImageUtils {
	private static ImageUtils single_instance = null;
	
	public static ImageUtils getImageUtils() {
		if (single_instance == null) {
			single_instance = new ImageUtils();
			System.out.println("Instance of 'ImageUtils' has been created");
		}
		return single_instance;
	}
	
	// Has only a private constructor, so that only one instance can exist
	private ImageUtils() {}
	
	
	
	 public ImageDifferenceData getDifferenceHighlightedResult(BufferedImage oldIm, BufferedImage newIm) {
		 ImageComparisonResult imageComparisonResult = new ImageComparison(oldIm, newIm).compareImages();
		 ImageDifferenceData idd = new ImageDifferenceData();
		 idd.diffIm = imageComparisonResult.getResult();
		 idd.diffPercentage = imageComparisonResult.getDifferencePercent();
	
		 return idd;
	 }
}
