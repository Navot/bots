import com.experitest.client.Client;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by navot on 11/10/2016.
 */
public class Runner implements Runnable {

    private static int index=0;
    protected Client client = null;
    private String host = "localhost";
    private int port = 8889;
    public static Map<String,String> MAP=null;
    public String appNameFromDevice="";
    public static String lastScreen ="";
    public static Worker worker = null;
    public static ChangeChecker CC = null;
    public static ScreensManager SM =null;
    public static TestFactory TF = null;
    public static Navigator NGR = null;


    public Runner(Map<String,String> MAP){
        this.MAP=MAP;
    }
    @Override
    public void run() {

        try{
            client = SetUpClient(MAP.get("device"));
            appNameFromDevice = LaunchApp(MAP.get("appPath"),MAP.get("appString"));
        }catch (Exception e ){
            System.out.println("Cloud Not SetUp the Client");
            return;

        }
        try {
            SM = new ScreensManager(client);
            worker = new Worker(client);
            CC = new ChangeChecker(client,appNameFromDevice);
            TF = new TestFactory();
            NGR = new Navigator(client);
            BotObject bot = new BotObject();
            List<String> route= new ArrayList();
            route.add("Launching = LandingPage");
            bot.BotRun(route, "SpringBoard");
            System.out.println("----------- DONE WITH APP -----------");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public Client SetUpClient(String device) {
        Client client = new Client(host, port, true);
        client.waitForDevice(device,10000);
        return client;
    }

    private String LaunchApp(String appPath,String appString)  {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client.launch(appString,false,true);
        String s = client.getCurrentApplicationName();
        client.syncElements(2000,20000);
        return s;
    }

    public static int GetIndex() {
        index++;
        return index-1;
    }

    public static Map<String, String> getMAP() {
        return MAP;
    }

}
