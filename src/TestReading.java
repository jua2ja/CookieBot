import java.io.File;


import net.sourceforge.tess4j.*;

public class TestReading {
    public static void main(String[] args) {
    	
    	
        File image = new File("C:\\bin\\Tess4J\\test\\resources\\test-data\\eurotext.tif");
        System.out.println(image.exists());
        Tesseract tessInst = new Tesseract();
        tessInst.setDatapath("C://bin//Tess4J");
        try {
            String result= tessInst.doOCR(image);
            System.out.println(result);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
    }
}
