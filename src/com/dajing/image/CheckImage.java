package com.dajing.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class CheckImage {

	// 全流程
	public static Map<Integer, Double> check360(String path, String pathSrc) {

		Map<Integer, Double> result = new HashMap<Integer, Double>();

		try {
			// 获取图像
			File imageFile = new File(path);
			if (!imageFile.exists() || imageFile.length() == 0) {
				return result;
			}

			Image image = ImageIO.read(imageFile);
			// 转换至灰度
			image = toGrayscale(image);
			// 缩小成32x32的缩略图
			image = scale(image);
			// 获取灰度像素数组
			int[] pixels = getPixels(image);
			// 获取平均灰度颜色
			int averageColor = getAverageOfPixelArray(pixels);
			// 获取灰度像素的比较数组（即图像指纹序列）
			pixels = getPixelDeviateWeightsArray(pixels, averageColor);
			// 获取两个图的汉明距离（假设另一个图也已经按上面步骤得到灰度比较数组）
			// int hammingDistance = getHammingDistance(pixels, pixels);

			// 获取图像
			File imageFileSrc = new File(pathSrc);
			if (!imageFileSrc.exists() || imageFileSrc.length() == 0) {
				return result;
			}

			for (int i = 0; i < 4; i++) {
				Image imageSrc = ImageIO.read(imageFileSrc);
				double similarityResult = 0;
				imageSrc = rotate(imageSrc, 360 - 90 * i);
				// 转换至灰度
				imageSrc = toGrayscale(imageSrc);
				// 缩小成32x32的缩略图
				imageSrc = scale(imageSrc);
				// 获取灰度像素数组
				int[] pixelsSrc = getPixels(imageSrc);
				// 获取平均灰度颜色
				int averageColorSrc = getAverageOfPixelArray(pixelsSrc);
				// 获取灰度像素的比较数组（即图像指纹序列）
				pixelsSrc = getPixelDeviateWeightsArray(pixelsSrc, averageColorSrc);

				// 获取两个图的汉明距离（假设另一个图也已经按上面步骤得到灰度比较数组）
				int hammingDistance = getHammingDistance(pixels, pixelsSrc);
				// 通过汉明距离计算相似度，取值范围 [0.0, 1.0]
				double similarity = calSimilarity(hammingDistance);
				if (similarity > similarityResult) {
					similarityResult = similarity;
				}
				result.put(i, similarityResult);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

	// 全流程
	public static double check(String pathSrc, String path) {

		double similarityResult = 0;
		try {
			// 获取图像
			File imageFile = new File(pathSrc);
			if (!imageFile.exists() || imageFile.length() == 0) {
				return similarityResult;
			}

			Image image = ImageIO.read(imageFile);
			// 转换至灰度
			image = toGrayscale(image);
			// 缩小成32x32的缩略图
			image = scale(image);
			// 获取灰度像素数组
			int[] pixels = getPixels(image);
			// 获取平均灰度颜色
			int averageColor = getAverageOfPixelArray(pixels);
			// 获取灰度像素的比较数组（即图像指纹序列）
			pixels = getPixelDeviateWeightsArray(pixels, averageColor);
			// 获取两个图的汉明距离（假设另一个图也已经按上面步骤得到灰度比较数组）
			// int hammingDistance = getHammingDistance(pixels, pixels);

			// 获取图像
			File imageFileSrc = new File(path);
			if (!imageFileSrc.exists() || imageFileSrc.length() == 0) {
				return similarityResult;
			}

			for (int i = 0; i < 4; i++) {
				Image imageSrc = ImageIO.read(imageFileSrc);
				imageSrc = rotate(imageSrc, 360 - i * 90);
				// 转换至灰度
				imageSrc = toGrayscale(imageSrc);
				// 缩小成32x32的缩略图
				imageSrc = scale(imageSrc);
				// 获取灰度像素数组
				int[] pixelsSrc = getPixels(imageSrc);
				// 获取平均灰度颜色
				int averageColorSrc = getAverageOfPixelArray(pixelsSrc);
				// 获取灰度像素的比较数组（即图像指纹序列）
				pixelsSrc = getPixelDeviateWeightsArray(pixelsSrc, averageColorSrc);
				

				// 获取两个图的汉明距离（假设另一个图也已经按上面步骤得到灰度比较数组）
				int hammingDistance = getHammingDistance(pixels, pixelsSrc);
				// 通过汉明距离计算相似度，取值范围 [0.0, 1.0]
				double similarity = calSimilarity(hammingDistance);
				if (similarity > similarityResult) {
					similarityResult = similarity;
				}
				// System.out.println(similarity);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return similarityResult;

	}

	// 将任意Image类型图像转换为BufferedImage类型，方便后续操作
	public static BufferedImage convertToBufferedFrom(Image srcImage) {
		BufferedImage bufferedImage = new BufferedImage(srcImage.getWidth(null), srcImage.getHeight(null),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bufferedImage.createGraphics();
		g.drawImage(srcImage, null, null);
		g.dispose();
		return bufferedImage;
	}

	// 转换至灰度图
	public static BufferedImage toGrayscale(Image image) {
		BufferedImage sourceBuffered = convertToBufferedFrom(image);
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		ColorConvertOp op = new ColorConvertOp(cs, null);
		BufferedImage grayBuffered = op.filter(sourceBuffered, null);
		return grayBuffered;
	}

	// 缩放至32x32像素缩略图
	public static Image scale(Image image) {
		image = image.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
		return image;
	}

	// 获取像素数组
	public static int[] getPixels(Image image) {
		int width = image.getWidth(null);
		int height = image.getHeight(null);
		int[] pixels = convertToBufferedFrom(image).getRGB(0, 0, width, height, null, 0, width);
		return pixels;
	}

	// 获取灰度图的平均像素颜色值
	public static int getAverageOfPixelArray(int[] pixels) {
		Color color;
		long sumRed = 0;
		for (int i = 0; i < pixels.length; i++) {
			color = new Color(pixels[i], true);
			sumRed += color.getRed();
		}
		int averageRed = (int) (sumRed / pixels.length);
		return averageRed;
	}

	// 获取灰度图的像素比较数组（平均值的离差）
	public static int[] getPixelDeviateWeightsArray(int[] pixels, final int averageColor) {
		Color color;
		int[] dest = new int[pixels.length];
		for (int i = 0; i < pixels.length; i++) {
			color = new Color(pixels[i], true);
			dest[i] = color.getRed() - averageColor > 0 ? 1 : 0;
		}
		return dest;
	}

	// 获取两个缩略图的平均像素比较数组的汉明距离（距离越大差异越大）
	public static int getHammingDistance(int[] a, int[] b) {
		int sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum += a[i] == b[i] ? 0 : 1;
		}
		return sum;
	}

	// 通过汉明距离计算相似度
	public static double calSimilarity(int hammingDistance) {
		int length = 32 * 32;
		double similarity = (length - hammingDistance) / (double) length;

		// 使用指数曲线调整相似度结果
		similarity = java.lang.Math.pow(similarity, 2);
		return similarity;
	}

	public static BufferedImage rotate(Image src, int angel) {
		int src_width = src.getWidth(null);
		int src_height = src.getHeight(null);
		// calculate the new image size
		Rectangle rect_des = CalcRotatedSize(new Rectangle(new Dimension(src_width, src_height)), angel);

		BufferedImage res = null;
		res = new BufferedImage(rect_des.width, rect_des.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = res.createGraphics();
		// transform
		g2.translate((rect_des.width - src_width) / 2, (rect_des.height - src_height) / 2);
		g2.rotate(Math.toRadians(angel), src_width / 2, src_height / 2);

		g2.drawImage(src, null, null);
		return res;
	}

	public static Rectangle CalcRotatedSize(Rectangle src, int angel) {
		// if angel is greater than 90 degree, we need to do some conversion
		if (angel >= 90) {
			if (angel / 90 % 2 == 1) {
				int temp = src.height;
				src.height = src.width;
				src.width = temp;
			}
			angel = angel % 90;
		}

		double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
		double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
		double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
		double angel_dalta_width = Math.atan((double) src.height / src.width);
		double angel_dalta_height = Math.atan((double) src.width / src.height);

		int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_width));
		int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_height));
		int des_width = src.width + len_dalta_width * 2;
		int des_height = src.height + len_dalta_height * 2;
		return new java.awt.Rectangle(new Dimension(des_width, des_height));
	}

	// angle：角度，图片旋转角度
	public BufferedImage rotate(BufferedImage bufferedImage, int angle) {

		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		int new_w = 0, new_h = 0;
		int new_radian = angle;
		if (angle <= 90) {
			new_w = (int) (width * Math.cos(Math.toRadians(new_radian))
					+ height * Math.sin(Math.toRadians(new_radian)));
			new_h = (int) (height * Math.cos(Math.toRadians(new_radian))
					+ width * Math.sin(Math.toRadians(new_radian)));
		} else if (angle <= 180) {
			new_radian = angle - 90;
			new_w = (int) (height * Math.cos(Math.toRadians(new_radian))
					+ width * Math.sin(Math.toRadians(new_radian)));
			new_h = (int) (width * Math.cos(Math.toRadians(new_radian))
					+ height * Math.sin(Math.toRadians(new_radian)));
		} else if (angle <= 270) {
			new_radian = angle - 180;
			new_w = (int) (width * Math.cos(Math.toRadians(new_radian))
					+ height * Math.sin(Math.toRadians(new_radian)));
			new_h = (int) (height * Math.cos(Math.toRadians(new_radian))
					+ width * Math.sin(Math.toRadians(new_radian)));
		} else {
			new_radian = angle - 270;
			new_w = (int) (height * Math.cos(Math.toRadians(new_radian))
					+ width * Math.sin(Math.toRadians(new_radian)));
			new_h = (int) (width * Math.cos(Math.toRadians(new_radian))
					+ height * Math.sin(Math.toRadians(new_radian)));
		}
		BufferedImage toStore = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = toStore.createGraphics();
		AffineTransform affineTransform = new AffineTransform();
		affineTransform.rotate(Math.toRadians(angle), width / 2, height / 2);
		if (angle != 180) {
			AffineTransform translationTransform = findTranslation(affineTransform, bufferedImage, angle);
			affineTransform.preConcatenate(translationTransform);
		}
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, new_w, new_h);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawRenderedImage(bufferedImage, affineTransform);
		g.dispose();
		bufferedImage = toStore;

		return bufferedImage;
	}

	private AffineTransform findTranslation(AffineTransform at, BufferedImage bi, int angle) {// 45
		Point2D p2din, p2dout;
		double ytrans = 0.0, xtrans = 0.0;
		if (angle <= 90) {
			p2din = new Point2D.Double(0.0, 0.0);
			p2dout = at.transform(p2din, null);
			ytrans = p2dout.getY();

			p2din = new Point2D.Double(0, bi.getHeight());
			p2dout = at.transform(p2din, null);
			xtrans = p2dout.getX();
		}
		/*
		 * else if(angle<=135){ p2din = new Point2D.Double(0.0, bi.getHeight());
		 * p2dout = at.transform(p2din, null); ytrans = p2dout.getY();
		 * 
		 * p2din = new Point2D.Double(bi.getWidth(),bi.getHeight()); p2dout =
		 * at.transform(p2din, null); xtrans = p2dout.getX();
		 * 
		 * }
		 */
		else if (angle <= 180) {
			p2din = new Point2D.Double(0.0, bi.getHeight());
			p2dout = at.transform(p2din, null);
			ytrans = p2dout.getY();

			p2din = new Point2D.Double(bi.getWidth(), bi.getHeight());
			p2dout = at.transform(p2din, null);
			xtrans = p2dout.getX();

		}
		/*
		 * else if(angle<=225){ p2din = new Point2D.Double(bi.getWidth(),
		 * bi.getHeight()); p2dout = at.transform(p2din, null); ytrans =
		 * p2dout.getY();
		 * 
		 * p2din = new Point2D.Double(bi.getWidth(),0.0); p2dout =
		 * at.transform(p2din, null); xtrans = p2dout.getX();
		 * 
		 * }
		 */
		else if (angle <= 270) {
			p2din = new Point2D.Double(bi.getWidth(), bi.getHeight());
			p2dout = at.transform(p2din, null);
			ytrans = p2dout.getY();

			p2din = new Point2D.Double(bi.getWidth(), 0.0);
			p2dout = at.transform(p2din, null);
			xtrans = p2dout.getX();

		} else {
			p2din = new Point2D.Double(bi.getWidth(), 0.0);
			p2dout = at.transform(p2din, null);
			ytrans = p2dout.getY();

			p2din = new Point2D.Double(0.0, 0.0);
			p2dout = at.transform(p2din, null);
			xtrans = p2dout.getX();

		}
		AffineTransform tat = new AffineTransform();
		tat.translate(-xtrans, -ytrans);
		return tat;
	}
}
