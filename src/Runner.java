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
    private final String appPath;
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


    public Runner(Map<String,String> MAP,String appPath){
        this.appPath = appPath;
        this.MAP=MAP;
    }
    @Override
    public void run() {

        try{
            client = SetUpClient(MAP.get("device"));
            appNameFromDevice = LaunchApp(appPath,MAP.get("appString"));
        }catch (Exception e ){
            System.out.println("Cloud Not SetUp the Client");
            return;
        }
        SM = new ScreensManager(client);
        worker = new Worker(client);
        CC = new ChangeChecker(client,appNameFromDevice);
        TF = new TestFactory(client);
        NGR = new Navigator(client);
        try {

            BotObject bot = new BotObject();
            List<String> route= new ArrayList();
            route.add("Launching = LandingPage");
            bot.BotRun(route, "");
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
        Client client = new Client(host, port, false);
        client.waitForDevice(device,10000);
        return client;
    }

    private String LaunchApp(String appPath,String appString)  {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client.install(appPath,true,false);
        client.launch(appString,true,true);
        //client.syncElements(2000,20000);
        String s = client.getCurrentApplicationName();
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
