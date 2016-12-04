import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;


import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;

/**
 * Created by navot.dako on 10/10/2016.
 */
public class main {



    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, InterruptedException {

        System.out.println("Let's Go");
        System.out.println("deleting history");
        FileUtils.cleanDirectory(new File("Results\\dumps"));
        FileUtils.cleanDirectory(new File("Results\\captures"));
        FileUtils.cleanDirectory(new File("Results\\tests"));
        Map<String,String> MAP = RunSetUp.getRunDetails();

        Thread thread = new Thread(new Runner(MAP));
        thread.start();

        while(thread.isAlive()){
            Thread.sleep(3000);

        }

        System.out.println("---------DONE-------------");
    }

















}
