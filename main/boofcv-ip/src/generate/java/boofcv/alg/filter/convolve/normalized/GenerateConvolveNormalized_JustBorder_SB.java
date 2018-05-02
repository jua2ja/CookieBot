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

package boofcv.alg.filter.convolve.normalized;

import boofcv.misc.AutoTypeImage;
import boofcv.misc.CodeGeneratorBase;

import java.io.FileNotFoundException;

/**
 * Code generator which creates re-normalizing convolution code
 *
 * @author Peter Abeles
 */
public class GenerateConvolveNormalized_JustBorder_SB extends CodeGeneratorBase  {

	String kernelType;
	String inputType;
	String outputType;
	String kernelData;
	String inputData;
	String outputData;
	String sumType;
	String bitWiseOp;
	String divide;

	public void generate() {
		printPreamble();

		printAllOps(AutoTypeImage.F32, AutoTypeImage.F32);
		printAllOps(AutoTypeImage.F64, AutoTypeImage.F64);
		printAllOps(AutoTypeImage.U8, AutoTypeImage.I8);
		printAllOps(AutoTypeImage.S16,AutoTypeImage.I16);
		printAllOps(AutoTypeImage.S32,AutoTypeImage.S32);

		printVertical2Int(AutoTypeImage.U16, AutoTypeImage.I8);
		printVertical2Int(AutoTypeImage.S32, AutoTypeImage.I16);

		out.println("}");
	}

	private void printPreamble() {
		out.print("import boofcv.struct.convolve.*;\n" +
				"import boofcv.struct.image.*;\n"+
				"import javax.annotation.Generated;\n"+
				"\n" +
				"/**\n" +
				" * <p>\n" +
				" * Covolves a 1D kernel in the horizontal or vertical direction across an image's border only, while re-normalizing the\n" +
				" * kernel sum to one.  The kernel MUST be smaller than the image.\n" +
				" * </p>\n" +
				" * \n" +
				" * <p>\n" +
				" * NOTE: Do not modify.  Automatically generated by "+getClass().getSimpleName()+"\n" +
				" * </p>\n" +
				" * \n" +
				" * @author Peter Abeles\n" +
				" */\n" +
				"@Generated({\""+getClass().getCanonicalName()+"\"})\n" +
				"@SuppressWarnings({\"ForLoopReplaceableByForEach\"})\n" +
				"public class "+className+" {\n\n");
	}

	private void printAllOps( AutoTypeImage input , AutoTypeImage output )
	{
		boolean isInteger = input.isInteger();

		kernelType = input.getKernelType();
		inputType = input.getSingleBandName();
		outputType = output.getSingleBandName();
		kernelData = input.getSumType();
		inputData = input.getDataType();
		outputData = output.getDataType();
		sumType = input.getSumType();
		bitWiseOp = input.getBitWise();

		divide = isInteger ? "(total+weight/2)/weight" : "total/weight";

		printHorizontal();
		printVertical();
		printConvolve();
	}

	private void printVertical2Int( AutoTypeImage input , AutoTypeImage output )
	{
		kernelType = input.getKernelType();
		inputType = input.getSingleBandName();
		outputType = output.getSingleBandName();
		kernelData = input.getSumType();
		inputData = input.getDataType();
		outputData = output.getDataType();
		sumType = input.getSumType();
		bitWiseOp = input.getBitWise();

		divide = "(total+weight/2)/weight";

		printVertical2();

	}

	private void printHorizontal() {
		
		String typeCast = outputData.compareTo(sumType) == 0 ? "" : "("+outputData+")";

		out.print("\tpublic static void horizontal(Kernel1D_"+kernelType+" kernel, "+inputType+" input, "+outputType+" output ) {\n" +
				"\t\tfinal "+inputData+"[] dataSrc = input.data;\n" +
				"\t\tfinal "+outputData+"[] dataDst = output.data;\n" +
				"\t\tfinal "+kernelData+"[] dataKer = kernel.data;\n" +
				"\n" +
				"\t\tfinal int kernelWidth = kernel.getWidth();\n" +
				"\t\tfinal int offsetL = kernel.getOffset();\n" +
				"\t\tfinal int offsetR = kernelWidth-offsetL-1;\n" +
				"\n" +
				"\t\tfinal int width = input.getWidth();\n" +
				"\t\tfinal int height = input.getHeight();\n" +
				"\n" +
				"\t\tfor (int i = 0; i < height; i++) {\n" +
				"\t\t\tint indexDest = output.startIndex + i * output.stride;\n" +
				"\t\t\tint j = input.startIndex + i * input.stride;\n" +
				"\t\t\tfinal int jStart = j;\n" +
				"\t\t\tint jEnd = j + offsetL;\n" +
				"\n" +
				"\t\t\tfor (; j < jEnd; j++) {\n" +
				"\t\t\t\t"+sumType+" total = 0;\n" +
				"\t\t\t\t"+sumType+" weight = 0;\n" +
				"\t\t\t\tint indexSrc = jStart;\n" +
				"\t\t\t\tfor (int k = kernelWidth - (offsetR + 1 + j - jStart); k < kernelWidth; k++) {\n" +
				"\t\t\t\t\t"+kernelData+" w = dataKer[k];\n" +
				"\t\t\t\t\tweight += w;\n" +
				"\t\t\t\t\ttotal += (dataSrc[indexSrc++]"+bitWiseOp+") * w;\n" +
				"\t\t\t\t}\n" +
				"\t\t\t\tdataDst[indexDest++] = "+typeCast+"("+divide+");\n" +
				"\t\t\t}\n" +
				"\n" +
				"\t\t\tj += width - (offsetL+offsetR);\n" +
				"\t\t\tindexDest += width - (offsetL+offsetR);\n" +
				"\n" +
				"\t\t\tjEnd = jStart + width;\n" +
				"\t\t\tfor (; j < jEnd; j++) {\n" +
				"\t\t\t\t"+sumType+" total = 0;\n" +
				"\t\t\t\t"+sumType+" weight = 0;\n" +
				"\t\t\t\tint indexSrc = j - offsetL;\n" +
				"\t\t\t\tfinal int kEnd = jEnd - indexSrc;\n" +
				"\n" +
				"\t\t\t\tfor (int k = 0; k < kEnd; k++) {\n" +
				"\t\t\t\t\t"+kernelData+" w = dataKer[k];\n" +
				"\t\t\t\t\tweight += w;\n" +
				"\t\t\t\t\ttotal += (dataSrc[indexSrc++]"+bitWiseOp+") * w;\n" +
				"\t\t\t\t}\n" +
				"\t\t\t\tdataDst[indexDest++] = "+typeCast+"("+divide+");\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	private void printVertical() {

		String typeCast = outputData.compareTo(sumType) == 0 ? "" : "("+outputData+")";

		out.print("\tpublic static void vertical(Kernel1D_"+kernelType+" kernel, "+inputType+" input, "+outputType+" output ) {\n" +
				"\t\tfinal "+inputData+"[] dataSrc = input.data;\n" +
				"\t\tfinal "+outputData+"[] dataDst = output.data;\n" +
				"\t\tfinal "+kernelData+"[] dataKer = kernel.data;\n" +
				"\n" +
				"\t\tfinal int kernelWidth = kernel.getWidth();\n" +
				"\t\tfinal int offsetL = kernel.getOffset();\n" +
				"\t\tfinal int offsetR = kernelWidth-offsetL-1;\n" +
				"\n" +
				"\t\tfinal int imgWidth = output.getWidth();\n" +
				"\t\tfinal int imgHeight = output.getHeight();\n" +
				"\n" +
				"\t\tfinal int yEnd = imgHeight - offsetR;\n" +
				"\n" +
				"\t\tfor (int y = 0; y < offsetL; y++) {\n" +
				"\t\t\tint indexDst = output.startIndex + y * output.stride;\n" +
				"\t\t\tint i = input.startIndex + y * input.stride;\n" +
				"\t\t\tfinal int iEnd = i + imgWidth;\n" +
				"\n" +
				"\t\t\tint kStart = offsetL - y;\n" +
				"\n" +
				"\t\t\t"+sumType+" weight = 0;\n" +
				"\t\t\tfor (int k = kStart; k < kernelWidth; k++) {\n" +
				"\t\t\t\tweight += dataKer[k];\n" +
				"\t\t\t}\n" +
				"\n" +
				"\t\t\tfor ( ; i < iEnd; i++) {\n" +
				"\t\t\t\t"+sumType+" total = 0;\n" +
				"\t\t\t\tint indexSrc = i - y * input.stride;\n" +
				"\t\t\t\tfor (int k = kStart; k < kernelWidth; k++, indexSrc += input.stride) {\n" +
				"\t\t\t\t\ttotal += (dataSrc[indexSrc]"+bitWiseOp+") * dataKer[k];\n" +
				"\t\t\t\t}\n" +
				"\t\t\t\tdataDst[indexDst++] = "+typeCast+"("+divide+");\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\n" +
				"\t\tfor (int y = yEnd; y < imgHeight; y++) {\n" +
				"\t\t\tint indexDst = output.startIndex + y * output.stride;\n" +
				"\t\t\tint i = input.startIndex + y * input.stride;\n" +
				"\t\t\tfinal int iEnd = i + imgWidth;\n" +
				"\n" +
				"\t\t\tint kEnd = imgHeight - (y - offsetL);\n" +
				"\n" +
				"\t\t\t"+sumType+" weight = 0;\n" +
				"\t\t\tfor (int k = 0; k < kEnd; k++) {\n" +
				"\t\t\t\tweight += dataKer[k];\n" +
				"\t\t\t}\n" +
				"\n" +
				"\t\t\tfor ( ; i < iEnd; i++) {\n" +
				"\t\t\t\t"+sumType+" total = 0;\n" +
				"\t\t\t\tint indexSrc = i - offsetL * input.stride;\n" +
				"\t\t\t\tfor (int k = 0; k < kEnd; k++, indexSrc += input.stride) {\n" +
				"\t\t\t\t\ttotal += (dataSrc[indexSrc]"+bitWiseOp+") * dataKer[k];\n" +
				"\t\t\t\t}\n" +
				"\t\t\t\tdataDst[indexDst++] = "+typeCast+"("+divide+");\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	private void printVertical2() {

		String typeCast = outputData.compareTo(sumType) == 0 ? "" : "("+outputData+")";

		out.print("\tpublic static void vertical(Kernel1D_"+kernelType+" kernelX, Kernel1D_"+kernelType+" kernelY,\n" +
				"\t\t\t\t\t\t\t\t"+inputType+" input, "+outputType+" output ) {\n" +
				"\t\tfinal "+inputData+"[] dataSrc = input.data;\n" +
				"\t\tfinal "+outputData+"[] dataDst = output.data;\n" +
				"\t\tfinal "+kernelData+"[] dataKer = kernelY.data;\n" +
				"\n" +
				"\t\tfinal int offsetY = kernelY.getOffset();\n" +
				"\t\tfinal int kernelWidthY = kernelY.getWidth();\n" +
				"\n" +
				"\t\tfinal int offsetX = kernelX.getOffset();\n" +
				"\t\tfinal int kernelWidthX = kernelX.getWidth();\n" +
				"\t\tfinal int offsetX1 = kernelWidthX-offsetX-1;\n" +
				"\n" +
				"\t\tfinal int imgWidth = output.getWidth();\n" +
				"\t\tfinal int imgHeight = output.getHeight();\n" +
				"\n" +
				"\t\tfinal int yEnd = imgHeight - (kernelWidthY-offsetY-1);\n" +
				"\n" +
				"\t\tint startWeightX = 0;\n" +
				"\t\tfor (int k = offsetX; k < kernelWidthX; k++) {\n" +
				"\t\t\tstartWeightX += kernelX.data[k];\n" +
				"\t\t}\n" +
				"\n" +
				"\t\tfor (int y = 0; y < offsetY; y++) {\n" +
				"\t\t\tint indexDst = output.startIndex + y * output.stride;\n" +
				"\t\t\tint i = input.startIndex + y * input.stride;\n" +
				"\t\t\tfinal int iEnd = i + imgWidth;\n" +
				"\n" +
				"\t\t\tint kStart = offsetY - y;\n" +
				"\n" +
				"\t\t\tint weightY = 0;\n" +
				"\t\t\tfor (int k = kStart; k < kernelWidthY; k++) {\n" +
				"\t\t\t\tweightY += dataKer[k];\n" +
				"\t\t\t}\n" +
				"\t\t\tint weightX = startWeightX;\n" +
				"\n" +
				"\t\t\tfor ( int x = 0; i < iEnd; i++, x++ ) {\n" +
				"\t\t\t\tint weight = weightX*weightY;\n" +
				"\t\t\t\tint total = 0;\n" +
				"\t\t\t\tint indexSrc = i - y * input.stride;\n" +
				"\t\t\t\tfor (int k = kStart; k < kernelWidthY; k++, indexSrc += input.stride) {\n" +
				"\t\t\t\t\ttotal += (dataSrc[indexSrc]"+bitWiseOp+") * dataKer[k];\n" +
				"\t\t\t\t}\n" +
				"\t\t\t\tdataDst[indexDst++] = "+typeCast+"("+divide+");\n" +
				"\t\t\t\tif( x < offsetX ) {\n" +
				"\t\t\t\t\tweightX += kernelX.data[offsetX-x-1];\n" +
				"\t\t\t\t} else if( x >= input.width-(kernelWidthX-offsetX) ) {\n" +
				"\t\t\t\t\tweightX -= kernelX.data[input.width-x+offsetX-1];\n" +
				"\t\t\t\t}\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\n" +
				"\t\tfor (int y = yEnd; y < imgHeight; y++) {\n" +
				"\t\t\tint indexDst = output.startIndex + y * output.stride;\n" +
				"\t\t\tint i = input.startIndex + y * input.stride;\n" +
				"\t\t\tfinal int iEnd = i + imgWidth;\n" +
				"\n" +
				"\t\t\tint kEnd = imgHeight - (y - offsetY);\n" +
				"\n" +
				"\t\t\tint weightY = 0;\n" +
				"\t\t\tfor (int k = 0; k < kEnd; k++) {\n" +
				"\t\t\t\tweightY += dataKer[k];\n" +
				"\t\t\t}\n" +
				"\t\t\tint weightX = startWeightX;\n" +
				"\n" +
				"\t\t\tfor ( int x = 0; i < iEnd; i++, x++ ) {\n" +
				"\t\t\t\tint weight = weightX*weightY;\n" +
				"\t\t\t\tint total = 0;\n" +
				"\t\t\t\tint indexSrc = i - offsetY * input.stride;\n" +
				"\t\t\t\tfor (int k = 0; k < kEnd; k++, indexSrc += input.stride) {\n" +
				"\t\t\t\t\ttotal += (dataSrc[indexSrc]"+bitWiseOp+") * dataKer[k];\n" +
				"\t\t\t\t}\n" +
				"\t\t\t\tdataDst[indexDst++] = "+typeCast+"("+divide+");\n" +
				"\t\t\t\tif( x < offsetX ) {\n" +
				"\t\t\t\t\tweightX += kernelX.data[offsetX-x-1];\n" +
				"\t\t\t\t} else if( x >= input.width-(kernelWidthX-offsetX) ) {\n" +
				"\t\t\t\t\tweightX -= kernelX.data[input.width-x+offsetX-1];\n" +
				"\t\t\t\t}\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\n" +
				"\t\t// left and right border\n" +
				"\t\tint weightY = kernelY.computeSum();\n" +
				"\t\tfor (int y = offsetY; y < yEnd; y++) {\n" +
				"\t\t\tint indexDst = output.startIndex + y * output.stride;\n" +
				"\t\t\tint i = input.startIndex + y * input.stride;\n" +
				"\n" +
				"\t\t\t// left side\n" +
				"\t\t\tint iEnd = i + offsetY;\n" +
				"\t\t\tint weightX = startWeightX;\n" +
				"\t\t\tfor ( int x = 0; i < iEnd; i++, x++ ) {\n" +
				"\t\t\t\tint weight = weightX*weightY;\n" +
				"\t\t\t\tint total = 0;\n" +
				"\t\t\t\tint indexSrc = i - offsetY * input.stride;\n" +
				"\t\t\t\tfor (int k = 0; k < kernelWidthY; k++, indexSrc += input.stride) {\n" +
				"\t\t\t\t\ttotal += (dataSrc[indexSrc]"+bitWiseOp+") * dataKer[k];\n" +
				"\t\t\t\t}\n" +
				"\t\t\t\tdataDst[indexDst++] = "+typeCast+"("+divide+");\n" +
				"\t\t\t\tweightX += kernelX.data[offsetX-x-1];\n" +
				"\t\t\t}\n" +
				"\n" +
				"\t\t\t// right side\n" +
				"\t\t\tint startX = input.width-offsetX1;\n" +
				"\t\t\tindexDst = output.startIndex + y * output.stride + startX;\n" +
				"\t\t\ti = input.startIndex + y * input.stride + startX;\n" +
				"\t\t\tiEnd = input.startIndex + y * input.stride + input.width;\n" +
				"\t\t\tfor ( int x = startX; i < iEnd; i++, x++ ) {\n" +
				"\t\t\t\tweightX -= kernelX.data[input.width-x+offsetX];\n" +
				"\t\t\t\tint weight = weightX*weightY;\n" +
				"\t\t\t\tint total = 0;\n" +
				"\t\t\t\tint indexSrc = i - offsetY * input.stride;\n" +
				"\t\t\t\tfor (int k = 0; k < kernelWidthY; k++, indexSrc += input.stride) {\n" +
				"\t\t\t\t\ttotal += (dataSrc[indexSrc]"+bitWiseOp+") * dataKer[k];\n" +
				"\t\t\t\t}\n" +
				"\t\t\t\tdataDst[indexDst++] = "+typeCast+"("+divide+");\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	public void printConvolve() {

		String typeCast = outputData.compareTo(sumType) == 0 ? "" : "("+outputData+")";

		out.print("\tpublic static void convolve(Kernel2D_"+kernelType+" kernel, "+inputType+" input, "+outputType+" output ) {\n" +
				"\t\tfinal "+inputData+"[] dataSrc = input.data;\n" +
				"\t\tfinal "+outputData+"[] dataDst = output.data;\n" +
				"\t\tfinal "+kernelData+"[] dataKer = kernel.data;\n" +
				"\n" +
				"\t\tfinal int kernelWidth = kernel.getWidth();\n" +
				"\t\tfinal int offsetL = kernel.getOffset();\n" +
				"\t\tfinal int offsetR = kernelWidth-offsetL-1;\n" +
				"\n" +
				"\t\tfinal int width = input.getWidth();\n" +
				"\t\tfinal int height = input.getHeight();\n" +
				"\n" +
				"\t\t// convolve across the left and right borders\n" +
				"\t\tfor (int y = 0; y < height; y++) {\n" +
				"\n" +
				"\t\t\tint minI = y >= offsetL ? -offsetL : -y;\n" +
				"\t\t\tint maxI = y < height - offsetR ?  offsetR : height - y - 1;\n" +
				"\n" +
				"\t\t\tint indexDst = output.startIndex + y* output.stride;\n" +
				"\n" +
				"\t\t\tfor( int x = 0; x < offsetL; x++ ) {\n" +
				"\n" +
				"\t\t\t\t"+sumType+" total = 0;\n" +
				"\t\t\t\t"+sumType+" weight = 0;\n" +
				"\n" +
				"\t\t\t\tfor( int i = minI; i <= maxI; i++ ) {\n" +
				"\t\t\t\t\tint indexSrc = input.startIndex + (y+i)* input.stride+x;\n" +
				"\t\t\t\t\tint indexKer = (i+offsetL)*kernelWidth;\n" +
				"\n" +
				"\t\t\t\t\tfor( int j = -x; j <= offsetR; j++ ) {\n" +
				"\t\t\t\t\t\t"+kernelData+" w = dataKer[indexKer+j+offsetL];\n" +
				"\t\t\t\t\t\tweight += w;\n" +
				"\t\t\t\t\t\ttotal += (dataSrc[indexSrc+j]"+bitWiseOp+") * w;\n" +
				"\t\t\t\t\t}\n" +
				"\t\t\t\t}\n" +
				"\n" +
				"\t\t\t\tdataDst[indexDst++] = "+typeCast+"("+divide+");\n" +
				"\t\t\t}\n" +
				"\n" +
				"\t\t\tindexDst = output.startIndex + y* output.stride + width-offsetR;\n" +
				"\t\t\tfor( int x = width-offsetR; x < width; x++ ) {\n" +
				"\n" +
				"\t\t\t\tint maxJ = width-x-1;\n" +
				"\n" +
				"\t\t\t\t"+sumType+" total = 0;\n" +
				"\t\t\t\t"+sumType+" weight = 0;\n" +
				"\n" +
				"\t\t\t\tfor( int i = minI; i <= maxI; i++ ) {\n" +
				"\t\t\t\t\tint indexSrc = input.startIndex + (y+i)* input.stride+x;\n" +
				"\t\t\t\t\tint indexKer = (i+offsetL)*kernelWidth;\n" +
				"\n" +
				"\t\t\t\t\tfor( int j = -offsetL; j <= maxJ; j++ ) {\n" +
				"\t\t\t\t\t\t"+kernelData+" w = dataKer[indexKer+j+offsetL];\n" +
				"\t\t\t\t\t\tweight += w;\n" +
				"\t\t\t\t\t\ttotal += (dataSrc[indexSrc+j]"+bitWiseOp+") * w;\n" +
				"\t\t\t\t\t}\n" +
				"\t\t\t\t}\n" +
				"\n" +
				"\t\t\t\tdataDst[indexDst++] = "+typeCast+"("+divide+");\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\n" +
				"\t\t// convolve across the top border while avoiding convolving the corners again\n" +
				"\t\tfor (int y = 0; y < offsetL; y++) {\n" +
				"\n" +
				"\t\t\tint indexDst = output.startIndex + y* output.stride+offsetL;\n" +
				"\n" +
				"\t\t\tfor( int x = offsetL; x < width-offsetR; x++ ) {\n" +
				"\n" +
				"\t\t\t\t"+sumType+" total = 0;\n" +
				"\t\t\t\t"+sumType+" weight = 0;\n" +
				"\n" +
				"\t\t\t\tfor( int i = -y; i <= offsetR; i++ ) {\n" +
				"\t\t\t\t\tint indexSrc = input.startIndex + (y+i)* input.stride+x;\n" +
				"\t\t\t\t\tint indexKer = (i+offsetL)*kernelWidth;\n" +
				"\n" +
				"\t\t\t\t\tfor( int j = -offsetL; j <= offsetR; j++ ) {\n" +
				"\t\t\t\t\t\t"+kernelData+" w = dataKer[indexKer+j+offsetL];\n" +
				"\t\t\t\t\t\tweight += w;\n" +
				"\t\t\t\t\t\ttotal += (dataSrc[indexSrc+j]"+bitWiseOp+") * w;\n" +
				"\t\t\t\t\t}\n" +
				"\t\t\t\t}\n" +
				"\t\t\t\tdataDst[indexDst++] = "+typeCast+"("+divide+");\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\n" +
				"\t\t// convolve across the bottom border\n" +
				"\t\tfor (int y = height-offsetR; y < height; y++) {\n" +
				"\n" +
				"\t\t\tint maxI = height - y - 1;\n" +
				"\t\t\tint indexDst = output.startIndex + y* output.stride+offsetL;\n" +
				"\n" +
				"\t\t\tfor( int x = offsetL; x < width-offsetR; x++ ) {\n" +
				"\n" +
				"\t\t\t\t"+sumType+" total = 0;\n" +
				"\t\t\t\t"+sumType+" weight = 0;\n" +
				"\n" +
				"\t\t\t\tfor( int i = -offsetL; i <= maxI; i++ ) {\n" +
				"\t\t\t\t\tint indexSrc = input.startIndex + (y+i)* input.stride+x;\n" +
				"\t\t\t\t\tint indexKer = (i+offsetL)*kernelWidth;\n" +
				"\n" +
				"\t\t\t\t\tfor( int j = -offsetL; j <= offsetR; j++ ) {\n" +
				"\t\t\t\t\t\t"+kernelData+" w = dataKer[indexKer+j+offsetL];\n" +
				"\t\t\t\t\t\tweight += w;\n" +
				"\t\t\t\t\t\ttotal += (dataSrc[indexSrc+j]"+bitWiseOp+") * w;\n" +
				"\t\t\t\t\t}\n" +
				"\t\t\t\t}\n" +
				"\t\t\t\tdataDst[indexDst++] = "+typeCast+"("+divide+");\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	public static void main(String args[]) throws FileNotFoundException {
		GenerateConvolveNormalized_JustBorder_SB gen = new GenerateConvolveNormalized_JustBorder_SB();
		gen.generate();
	}
}
