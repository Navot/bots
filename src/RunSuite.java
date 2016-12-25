import com.experitest.client.Client;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;

/**
 * Created by navot on 12/25/2016.
 */
public class RunSuite {
    static Map<String,String> MAP= null;
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        System.out.println("Let's Go");
        System.out.println("deleting history");
        FileUtils.cleanDirectory(new File("Results\\reportFiles"));
        MAP = RunSetUp.getRunDetails();
        Collection<File> testsFound = FileUtils.listFiles(new File("Results\\tests"), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File test: testsFound) {
            if (test.getName().startsWith("FT")) {
                System.out.println("Got A Functional Test File");
                List<String> testRouteList = GetList(test);

                System.out.println(testRouteList);
                Map<String,Object> result = startTest(test.getName(),testRouteList);

            } else {
                System.out.println("Got A LT - "+test.getName());
            }
        }
    }

    private static Map<String, Object> startTest(String testName, List<String> testRouteList) {
        Map<String,Object> report = new HashMap<String,Object>();

        Client client = CreateClient();
        client.setReporter("xml","C:\\Users\\navot\\IdeaProjects\\bots\\Results\\reportFiles",testName);
        System.out.println("Starting test " +  testName);
        for (int i = 0; i < testRouteList.size(); i++) {
            try{
                client.startStepsGroup(testRouteList.get(i));
                executeCommand(testRouteList.get(i),client);
                client.stopStepsGroup();
            }catch (Exception e){
                System.err.println("Something happened in the execution of - " +testRouteList.get(i));
                Map<String, Object> lastCommandResultMap = client.getLastCommandResultMap();
                report.put("result",false);
                report.put("cause","Something happened in the execution of - " +testRouteList.get(i));
            }
        }
        if(!executeLayoutTest(testName)){
            report.put("result",false);
            System.out.println("Layout Changed!!");
            report.put("cause","Layout Changed!!");

        }
        if(report.get("result")==null){
            report.put("result",true);
        }
        String reportPath= client.generateReport();
        client.releaseClient();

        try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

        report.put("reportPath",reportPath);
        return report;
    }

    private static boolean executeLayoutTest(String testName) {

        File layoutTestFile = new File("Results\\tests\\LT_"+testName.substring(3,testName.lastIndexOf("_")));
        List<String> elementslist = GetList(layoutTestFile);

        return false;
    }

    private static Client CreateClient() {
        String host = "localhost";
        int port = 8889;
        Client client = new Client(host, port, false);
        client.waitForDevice("@os='android'",10000);
        return client;
    }

    private static List<String> GetList(File test) {

        BufferedReader br = null; FileReader fr = null;
        List<String> testRoute = new ArrayList<>();
        try {
            fr = new FileReader(test);
            br = new BufferedReader(fr);
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                System.out.println(sCurrentLine);
                if (!sCurrentLine.contains("----------"))
                    testRoute.add(sCurrentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return testRoute;
    }

    private static void executeCommand(String command, Client client) {

        if (command.contains("Launching")){
            client.launch(MAP.get("appString"),true,true);
            client.sleep(1000);

        }
        else if (command.contains("Click")){
            client.click("NATIVE","//*[@"+command.substring(command.indexOf('-')+2,command.indexOf('=')+1)+"'"+command.substring(command.indexOf('=')+2)+"']",0,1);
            client.syncElements(500,20000);
        }
        else if (command.contains("SendText")){
            client.click("NATIVE","//*[@"+command.substring(command.indexOf('-')+2,command.indexOf('=')+1)+"'"+command.substring(command.indexOf('=')+2)+"']",0,1);
            client.sendText("company");
            client.closeKeyboard();
            client.syncElements(2000,20000);
        }
    }
}
