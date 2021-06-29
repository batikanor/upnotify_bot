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

/**
 * Singleton class that mainly contains image processing/ analysis related utility functions
 */
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
	/**
	 * Single private empty constructor of class
	 */
	private ImageUtils() {}
	
	
	/**
	 * Calculates image difference using ImageComparison library
	 * @param oldIm old image, the latest version of the website snip stored on DB
	 * @param newIm new image, new visual snip of tracked web page.
	 * @return Image difference data as defined in objects package
	 */
	 public ImageDifferenceData getDifferenceHighlightedResult(BufferedImage oldIm, BufferedImage newIm) {
		 ImageComparisonResult imageComparisonResult = new ImageComparison(newIm, oldIm).compareImages();
		 ImageDifferenceData idd = new ImageDifferenceData();
		 idd.diffIm = imageComparisonResult.getResult();
		 idd.diffPercentage = imageComparisonResult.getDifferencePercent();
	
		 return idd;
	 }
	 
	 /**
	  * Converts buffered image into input streram
	  * @param im buffered image
	  * @return input stream
	  */
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
	 /**
	  * Converts input stream into buffered image
	  * @param is Input stream
	  * @return Buffered image
	  */
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

	 /**
	  * Converts buffered image into byte array
	  * @param userSpaceImage buffered image
	  * @return byte array that is the data from rastered version of data buffer.
	  */
	public byte[] getByteData(BufferedImage userSpaceImage) {
		WritableRaster raster = userSpaceImage.getRaster();
		DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
		return buffer.getData();
	}
}
