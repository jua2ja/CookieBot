/*
 * Copyright (c) 2011-2017, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import boofcv.abst.feature.detdesc.DetectDescribePoint;
import boofcv.abst.feature.detect.extract.ConfigExtract;
import boofcv.abst.feature.detect.extract.NonMaxSuppression;
import boofcv.abst.feature.detect.interest.ConfigFastHessian;
import boofcv.abst.feature.orientation.OrientationIntegral;
import boofcv.alg.feature.describe.DescribePointSurf;
import boofcv.alg.feature.detect.interest.FastHessianFeatureDetector;
import boofcv.alg.transform.ii.GIntegralImageOps;
import boofcv.core.image.GeneralizedImageOps;
import boofcv.factory.feature.describe.FactoryDescribePointAlgs;
import boofcv.factory.feature.detdesc.FactoryDetectDescribe;
import boofcv.factory.feature.detect.extract.FactoryFeatureExtractor;
import boofcv.factory.feature.orientation.FactoryOrientationAlgs;
import boofcv.gui.feature.VisualizeShapes;
import boofcv.gui.image.ShowImages;
import boofcv.io.UtilIO;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.BoofDefaults;
import boofcv.struct.feature.BrightFeature;
import boofcv.struct.feature.ScalePoint;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.ImageGray;
import boofcv.core.image;
import georegression.struct.shapes.EllipseRotated_F64;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Example of how to use SURF detector and descriptors in BoofCV. 
 * 
 * @author Peter Abeles
 */
public class Test {

	/**
	 * Use generalized interfaces for working with SURF.  This removes much of the drudgery, but also reduces flexibility
	 * and slightly increases memory and computational requirements.
	 * 
	 *  @param image Input image type. DOES NOT NEED TO BE GrayF32, GrayU8 works too
	 */
	public static void easy( BufferedImage imageB ) {
		
		GrayF32 image = ConvertBufferedImage.convertFrom(imageB,(GrayF32)null);
		
		// create the detector and descriptors
		DetectDescribePoint<GrayF32,BrightFeature> surf = FactoryDetectDescribe.
				surfStable(new ConfigFastHessian(0, 2, 200, 2, 9, 4, 4), null, null,GrayF32.class);

		 // specify the image to process
		surf.detect(image);

		System.out.println("Found Features: "+surf.getNumberOfFeatures());
		Graphics2D g2 = imageB.createGraphics();
		g2.setStroke(new BasicStroke(3));

		double total;
		
		
		for(int i = 0; i < surf.getNumberOfFeatures(); i++)
		{
			total = 0;
			for(int j = 0; j < surf.getDescription(i).value.length; j++)
				total += surf.getDescription(i).value[j];
			System.out.println(total);

		}
		for(int j = 0; j < surf.getDescription(0).value.length; j++)
			System.out.println(surf.getDescription(0).value[j]);
	
		
		
//		System.out.println("First descriptor's first value: "+surf.getDescription(0).value[0]);
		for(int i = 0; i < surf.getNumberOfFeatures(); i++)
		{
//			System.out.println(surf.getLocation(i).x + " " + surf.getLocation(i).y);
			total = 0;
			for(int j = 0; j < surf.getDescription(0).value.length; j++)
				total += surf.getDescription(0).value[j];
//			System.out.println(total);
			g2.setColor(new Color((int)total / 10));
			EllipseRotated_F64 ellipse = new EllipseRotated_F64(surf.getLocation(i), surf.getRadius(i), surf.getRadius(i), surf.getRadius(i));
			VisualizeShapes.drawEllipse(ellipse , g2);
			}
		ShowImages.showWindow(imageB,"Detected Ellipses",true);

	}

	/**
	 * Configured exactly the same as the easy example above, but require a lot more code and a more in depth
	 * understanding of how SURF works and is configured.  Instead of TupleDesc_F64, SurfFeature are computed in
	 * this case.  They are almost the same as TupleDesc_F64, but contain the Laplacian's sign which can be used
	 * to speed up association. That is an example of how using less generalized interfaces can improve performance.
	 * 
	 * @param image Input image type. DOES NOT NEED TO BE GrayF32, GrayU8 works too
	 */
	public static <II extends ImageGray<II>> void harder(GrayF32 image ) {
		// SURF works off of integral images
		Class<II> integralType = GIntegralImageOps.getIntegralType(GrayF32.class);
		
		// define the feature detection algorithm
		NonMaxSuppression extractor =
				FactoryFeatureExtractor.nonmax(new ConfigExtract(2, 0, 5, true));
		FastHessianFeatureDetector<II> detector =
				new FastHessianFeatureDetector<>(extractor, 200, 2, 9, 4, 4, 6);

		// estimate orientation
		OrientationIntegral<II> orientation = 
				FactoryOrientationAlgs.sliding_ii(null, integralType);

		DescribePointSurf<II> descriptor = FactoryDescribePointAlgs.<II>surfStability(null,integralType);
		
		// compute the integral image of 'image'
		II integral = GeneralizedImageOps.createSingleBand(integralType,image.width,image.height);
		GIntegralImageOps.transform(image, integral);

		// detect fast hessian features
		detector.detect(integral);
		// tell algorithms which image to process
		orientation.setImage(integral);
		descriptor.setImage(integral);

		List<ScalePoint> points = detector.getFoundPoints();

		List<BrightFeature> descriptions = new ArrayList<>();

		for( ScalePoint p : points ) {
			// estimate orientation
			orientation.setObjectRadius( p.scale*BoofDefaults.SURF_SCALE_TO_RADIUS);
			double angle = orientation.compute(p.x,p.y);
			
			// extract the SURF description for this region
			BrightFeature desc = descriptor.createDescription();
			descriptor.describe(p.x,p.y,angle,p.scale,desc);

			// save everything for processing later on
			descriptions.add(desc);
		}
		
		System.out.println("Found Features: "+points.size());
		System.out.println("First descriptor's first value: "+descriptions.get(0).value[0]);
	}

	public static void main( String args[] ) {
		
		BufferedImage cookie = UtilImageIO.loadImage(UtilIO.pathExample("cookies/PerfectCookie.jpg"));
		BufferedImage cookieB = UtilImageIO.loadImage(UtilIO.pathExample("cookies/BackgroundCookie.png"));		
		
		BufferedImage screenPicture;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice screenDevice = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = screenDevice.getDefaultConfiguration();
		Robot screenDeviceShot = null;
		//standard screenshot taking with java.awt.Robot
		try {screenDeviceShot = new Robot(screenDevice);} catch (AWTException e) {System.exit(1);}
		screenPicture = screenDeviceShot.createScreenCapture(gc.getBounds());
		
		
		// run each example
//		Test.easy(cookie);
		Test.easy(cookieB);
		Test.easy(screenPicture);
		
		System.out.println("Done!");
		
	}
}
