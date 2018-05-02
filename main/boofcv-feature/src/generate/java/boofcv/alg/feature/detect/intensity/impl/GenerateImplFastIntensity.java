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

package boofcv.alg.feature.detect.intensity.impl;

import boofcv.misc.CodeGeneratorBase;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Peter Abeles
 */
public class GenerateImplFastIntensity extends CodeGeneratorBase {

	private final int TOTAL_CIRCLE = 16;
	private final int ALL = 0xFFFF;
	// bit field which indicates which pixels cannot be the first pixel in a corner if this one is false
	// 1 = still possible and 0 = not possible
	private int[]masks;

	// minimum number of edge points in a row to make a corner
	private int minContinuous;

	@Override
	public void generate() throws FileNotFoundException {
		createFile(9);
		createFile(10);
		createFile(11);
		createFile(12);
	}

	public void createFile( int minContinuous ) throws FileNotFoundException {
		className = "ImplFastIntensity"+minContinuous;

		this.minContinuous = minContinuous;

		createMasks();
		printPreamble();
		printCheck(true);
		printCheck(false);

		out.println("}");
	}

	/**
	 * Create the set of masks for each pixel in the circle
	 */
	private void createMasks() {
		masks = new int[TOTAL_CIRCLE];
		for( int i = 0; i < TOTAL_CIRCLE; i++ ) {
			int mask = 0;
			for( int j = 0; j < minContinuous; j++ ) {
				int v = i-j < 0 ? TOTAL_CIRCLE+i-j : i-j;
				mask |= 1 << v;
			}
			masks[i] = ~mask;
			System.out.println("Mask["+i+"]: "+fieldToString(masks[i]));
		}
	}


	private void printPreamble() throws FileNotFoundException {
		setOutputFile(className);

		out.print("import boofcv.alg.feature.detect.intensity.FastCornerIntensity;\n" +
				"import boofcv.struct.image.ImageGray;\n" +
				"\n" +
				"/**\n" +
				" * <p>\n" +
				" * Contains logic for detecting fast corners. Pixels are sampled such that they can eliminate the most\n" +
				" * number of possible corners, reducing the number of samples required.\n" +
				" * </p>\n" +
				" *\n" +
				" * <p>\n" +
				" * DO NOT MODIFY. Generated by {@link "+getClass().getSimpleName()+"}.\n" +
				" * </p>\n" +
				" *\n" +
				" * @author Peter Abeles\n" +
				" */\n" +
				"public class "+className+"<T extends ImageGray<T>> extends FastCornerIntensity<T>\n" +
				"{\n" +
				"\n" +
				"\t/**\n" +
				"\t * @param helper Provide the image type specific helper.\n" +
				"\t */\n" +
				"\tpublic "+className+"(FastHelper<T> helper) {\n" +
				"\t\tsuper(helper);\n" +
				"\t}\n\n");
	}

	private void printCheck( boolean isLower ) {
		String type = isLower ? "Lower" : "Upper";

		out.print("\t@Override\n" +
				"\tprotected boolean check"+type+"( int index )\n" +
				"\t{\n");

		// bit field keeps tracks of which circle index could be the start of a corner
		// value of 1 means it could still be a corner
		handleDecisionPoint(type, ALL, 0, 0, 2, false);

		out.print("\t}\n\n");
	}

	/**
	 * Recursive function which prints out decision tree for detecting a corner. Designed to eliminate
	 * candidates quickly
	 *
	 * @param type Lower or Upper
	 * @param candidateField Bitfield indicating if a pixel is a candidate for a starting point
	 * @param positiveField Bitfield indicating if a pixel as been proven to be an edge or not
	 * @param testedField Bitfield indicating which pixels have been tested for being a corner.
	 * @param depth How many tabs there are in front
	 */
	private void handleDecisionPoint( String type , int candidateField , int positiveField , int testedField , int depth , boolean fromElse) {

		System.out.println("----------------   Depth "+depth);
		System.out.println("Candidates: "+fieldToString(candidateField));
		System.out.println("Positive:   " + fieldToString(positiveField));
		System.out.println("Tested:     "+fieldToString(testedField));

		if( checkFinished(positiveField) ) {
			out.print(tabs(depth)+"return true;\n");
			return;
		} else if( candidateField == 0 ) {
			out.print(tabs(depth)+"return false;\n");
			return;
		}

		List<Candidate> candidates = new ArrayList<>();

		for( int i = 0; i < TOTAL_CIRCLE; i++ ) {
			if( (testedField & (1 << i)) != 0 )
				continue;
			Candidate c = new Candidate();
			c.candidateFields = candidateField & masks[i];
			c.score = countCandidates(c.candidateFields);
			c.index = i;
			candidates.add(c);
		}

		Collections.sort(candidates);

		// select the candidate which will eliminate the most pixels
		Candidate c = candidates.get(0);
		System.out.println("  considering "+c.index);
		if( !fromElse)
			out.print(tabs(depth));
		out.print("if( helper.checkPixel"+type+"(index + offsets["+c.index+"]) ) {\n");

		int adjustedTested = testedField | (1 << c.index);

		// positive case with edge being detected
		int adjustedPositive = positiveField | (1 << c.index);
		handleDecisionPoint(type, candidateField, adjustedPositive, adjustedTested, depth + 1, false);

		// handle negative case
		if( c.candidateFields == 0 ) {
			out.print(tabs(depth)+"} else {\n");
			out.print(tabs(depth+1)+"return false;\n");
			out.print(tabs(depth)+"}\n");
		} else {
			out.print(tabs(depth)+"} else ");
			handleDecisionPoint(type,c.candidateFields,positiveField,adjustedTested,depth,true);
		}
	}

	private String fieldToString( int field ) {
		String s = "";
		for( int i = 0; i < TOTAL_CIRCLE; i++ ) {
			if( (field & (1 << i)) != 0 )
				s += 1;
			else
				s += 0;
		}
		return s;
	}

	/**
	 * Prints tabs to ensure proper formatting
	 */
	private String tabs( int depth ) {
		String ret = "";
		for( int i = 0; i < depth; i++ ) {
			ret += "\t";
		}
		return ret;
	}

	/**
	 * Counts the number of candidates which remain
	 */
	private int countCandidates( int candidateField ) {
		int count = 0;
		for( int i = 0; i < TOTAL_CIRCLE; i++ ) {
			if( (candidateField & (1 << i)) != 0 )
				count++;
		}
		return count;
	}

	/**
	 * Checks to see if a corner has been proven
	 */
	private boolean checkFinished( int positive ) {
		for( int i = 0; i < TOTAL_CIRCLE; i++ ) {
			boolean match = true;
			for( int j = 0; j < minContinuous; j++ ) {
				int w = (i+j) % TOTAL_CIRCLE;
				if( (positive & (1 << w)) == 0 ) {
					match = false;
					break;
				}
			}
			if( match )
				return true;
		}
		return false;
	}

	private static class Candidate implements Comparable<Candidate> {
		int score;
		int candidateFields;
		int index = 0;

		@Override
		public int compareTo(Candidate o) {
			if( score < o.score )
				return -1;
			else if( score > o.score )
				return 1;
			else if( index < o.index )
				return -1;
			else if( index > o.index )
				return 1;
			else
				return 0;
		}
	}

	public static void main( String args[] ) throws FileNotFoundException {
		GenerateImplFastIntensity gen = new GenerateImplFastIntensity();
		gen.generate();
	}

}
