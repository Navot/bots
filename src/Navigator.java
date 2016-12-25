

import com.experitest.client.Client;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by navot on 12/2/2016.
 */
public class Navigator {

    private Client client = null;
    
    public Navigator(Client client) {
        System.out.println("Building A Navigator");
        this.client = client;
    }
    
    public boolean Navigate(Screen destinationScreen, Screen sourceScreen) {
        System.out.println("The " + destinationScreen.screenName + " Routes are:");
        System.out.println(destinationScreen.allRoutes);
        List<String> commandList = destinationScreen.getShortestRoute();

        if (sourceScreen!=null) {
            System.out.println("We Are on - " + sourceScreen.screenName + " and we are going to - " + destinationScreen.screenName);
            List<List<String>> candidates = new ArrayList<>();
            int routeLength = commandList.size();
            for (List<String> route : destinationScreen.allRoutes) {
                for (String screenName : route) {
                    if (screenName.contains(sourceScreen.screenName.substring(0,sourceScreen.screenName.lastIndexOf("_")))) {
                        candidates.add(route);
                        break;
                    }
                }
            }

            for (List<String> route : candidates) {
                int index = FindLastSourceScreenInstance(route, sourceScreen.screenName);
                if (route.size() - index < routeLength) {
                    System.out.println("Found A Shorter Route - ");
                    System.out.println(route.subList(index, route.size()));
                    commandList = route.subList(index, route.size());
                }
            }
        }
        else {
            System.out.println("No Previous Screen - Starting from the top");
        }
        for (int i = 0; i < commandList.size(); i++) {
            executeCommand(commandList.get(i));
        }
        return false;
    }

    private int FindLastSourceScreenInstance(List<String> route, String screenName) {
        int index=-1;
        for (int i = 0; i < route.size(); i++) {
            if (route.get(i).contains(screenName.substring(0,screenName.lastIndexOf("_"))))
                index = i;
        }
        return (index+1);
    }

    private void executeCommand(String command) {
        if (command.contains("Launching")){
            client.launch(Runner.getMAP().get("appString"),true,true);
            client.sleep(1000);
            client.syncElements(2000,20000);

        }
        if (command.contains("Click")){
            client.click("NATIVE","//*[@"+command.substring(command.indexOf('-')+2,command.indexOf('=')+1)+"'"+command.substring(command.indexOf('=')+2)+"']",0,1);
            client.syncElements(2000,20000);
        }
        if (command.contains("SendText")){
            client.click("NATIVE","//*[@"+command.substring(command.indexOf('-')+2,command.indexOf('=')+1)+"'"+command.substring(command.indexOf('=')+2)+"']",0,1);
            client.sendText("company");
            client.closeKeyboard();
            client.syncElements(2000,20000);
        }
    }

}
