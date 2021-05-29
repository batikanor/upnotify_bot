package utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

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
		 ImageComparisonResult imageComparisonResult = new ImageComparison(newIm, oldIm).compareImages();
		 ImageDifferenceData idd = new ImageDifferenceData();
		 idd.diffIm = imageComparisonResult.getResult();
		 idd.diffPercentage = imageComparisonResult.getDifferencePercent();
	
		 return idd;
	 }
	 
	 public InputStream convertBufferedImageIntoInputStream(BufferedImage im) {
		 
		 if (im == null) {
			 return null;
		 }
		 ByteArrayOutputStream os = new ByteArrayOutputStream();
		 try {
			ImageIO.write(im, "png", os);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		 InputStream is = new ByteArrayInputStream(os.toByteArray());
		 //System.out.println("inputstream in string form" + is.readAllBytes().toString());
		 
		 return is;
		 
	 }
	 public BufferedImage convertInputStreamIntoBufferedImage(InputStream is) {
		 try {
			BufferedImage imBuff = ImageIO.read(is);
			return imBuff;
		 } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	 }

	public byte[] getByteData(BufferedImage userSpaceImage) {
		WritableRaster raster = userSpaceImage.getRaster();
		DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
		return buffer.getData();
	}
}
