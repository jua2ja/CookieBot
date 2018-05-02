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

package boofcv.abst.tracker;

import boofcv.alg.tracker.tld.TldTracker;
import boofcv.struct.image.ImageGray;
import boofcv.struct.image.ImageType;
import georegression.geometry.UtilPolygons2D_F64;
import georegression.struct.shapes.Quadrilateral_F64;
import georegression.struct.shapes.Rectangle2D_F64;

/**
 * Wrapper around {@link boofcv.alg.tracker.tld.TldTracker} for {@link TrackerObjectQuad}.
 *
 * @author Peter Abeles
 */
public class Tld_to_TrackerObjectQuad<T extends ImageGray<T>, D extends ImageGray<D>>
		implements TrackerObjectQuad<T>
{
	Rectangle2D_F64 rect = new Rectangle2D_F64();
	TldTracker<T,D> tracker;
	ImageType<T> type;

	public Tld_to_TrackerObjectQuad(TldTracker<T, D> tracker , Class<T> imageType ) {
		this.tracker = tracker;
		this.type = ImageType.single(imageType);
	}

	@Override
	public boolean initialize(T image, Quadrilateral_F64 location ) {

		UtilPolygons2D_F64.bounding(location, rect);

		tracker.initialize(image,(int)rect.p0.x,(int)rect.p0.y,(int)rect.p1.x,(int)rect.p1.y);

		return true;
	}

	@Override
	public void hint(Quadrilateral_F64 hint) {
		UtilPolygons2D_F64.bounding(hint, rect);

		tracker.setTrackerLocation((int)rect.p0.x,(int)rect.p0.y,(int)rect.p1.x,(int)rect.p1.y);
	}

	@Override
	public boolean process(T image, Quadrilateral_F64 results) {

		if( !tracker.track(image) )
			return false;

		Rectangle2D_F64 rect = tracker.getTargetRegion();

		results.a.x = rect.p0.x;
		results.a.y = rect.p0.y;
		results.b.x = rect.p1.x;
		results.b.y = rect.p0.y;
		results.c.x = rect.p1.x;
		results.c.y = rect.p1.y;
		results.d.x = rect.p0.x;
		results.d.y = rect.p1.y;

		return true;
	}

	@Override
	public ImageType<T> getImageType() {
		return type;
	}

	@Override
	public TldTracker<T, D> getLowLevelTracker() {
		return tracker;
	}
}
