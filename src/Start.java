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
public class Start implements Runnable {

    protected Client client = null;
    private String host = "localhost";
    private int port = 8889;
    public Map<String,String> MAP=null;
    String appNameFromDevice="";
    public Start(Map<String,String> MAP){
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
            BotObject bot = new BotObject("Launching",client,appNameFromDevice);
            bot.BotRun("Launching = First_Screen");
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
        //client.install(appPath,false,false);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client.launch(appString,false,true);
       /* client.click("NATIVE", "xpath=/*//*[@text='Username']", 0, 1);
        client.sendText("company");
        client.closeKeyboard();
        client.click("NATIVE", "xpath=/*//*[@id='passwordTextField']", 0, 1);
        client.sendText("company");

        client.click("NATIVE", "xpath=/*//*[@id='loginButton']", 0, 1);*/

        String s = client.getCurrentApplicationName();
        client.syncElements(2000,10000);
        return s;
    }
}
