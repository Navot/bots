import com.experitest.client.Client;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by navot on 12/10/2016.
 */
public class ScreensManager {
    private Client client=null;
    private static List<Screen> screenList = null;
    private int index = 0;

    public ScreensManager(Client client){
        this.client=client;
        screenList = new ArrayList<>();
    }

    public static Screen GetScreenByName(String screenName){
        for (int i = 0; i < screenList.size(); i++) {
            if (screenList.get(i).screenName.equals(screenName)){
                return screenList.get(i);
            }
        }
        return null;
    }

    public Screen CheckIfBeenHereBefore(Screen currentScreen) {
        System.out.println("Checking If We Have Been Here Before");
        Screen VisitedScreen=null;
        for (Screen repoScreen : screenList) {
            if (!ChangeChecker.IsDumpDifferent(currentScreen,repoScreen.screenName))
            {
                VisitedScreen = repoScreen;
                System.out.println("We Were Here Before - It Was Called "+VisitedScreen.screenName);
                return VisitedScreen;
            }
        }

        System.out.println("We Weren't Here");
        return null;

    }

    public boolean AddScreen(Screen currentScreen) throws IOException, SAXException, ParserConfigurationException {

        screenList.add(currentScreen);
        AddToRepo(currentScreen);

        return true;
    }

    public boolean AddToRepo(Screen currentScreen)  {
        System.out.println("Writing Files To Repo");
        File file = new File("C:\\Users\\navot\\IdeaProjects\\bots\\Results\\dumps\\"+currentScreen.screenName+"_"+index+".xml");

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(currentScreen.screenElements);
            writer.close();
            System.out.println("Done Writing - "+file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try {
            Thread.sleep(200);
            String capture = client.capture();
            File repoFile =new File("C:\\Users\\navot\\IdeaProjects\\bots\\Results\\captures\\"+currentScreen.screenName+"_"+index+".png");
            FileUtils.copyFile(new File (capture), repoFile);
            System.out.println("Done Writing - "+repoFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        index++;
        return true;
    }




}
