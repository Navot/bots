import com.experitest.client.Client;

import java.util.List;

/**
 * Created by navot on 12/2/2016.
 */
public class Navigator {
    private Client client = null;

    public Navigator(Client client) {
        this.client = client;
    }
    public boolean Navigate(Screen currentScreen) {

        System.out.println(currentScreen.routes);
        List<String> commandList = currentScreen.getShortestRoute();
        for (int i = 0; i < commandList.size(); i++) {
            executeCommand(commandList.get(i));
        }
        return false;
    }

    private void executeCommand(String command) {
        if (command.contains("Launching")){
            client.launch(Runner.getMAP().get("appString"),false,true);
            client.syncElements(2000,20000);
            client.sleep(1000);
        }
        if (command.contains("Click")){
            client.click("NATIVE","//*[@"+command.substring(command.indexOf('-')+2,command.indexOf('=')+1)+"'"+command.substring(command.indexOf('=')+2)+"']",0,1);
            client.syncElements(2000,20000);
        }
        if (command.contains("SendText")){
            client.click("NATIVE","//*[@"+command.substring(command.indexOf('-')+2,command.indexOf('=')+1)+"'"+command.substring(command.indexOf('=')+2)+"']",0,1);
            client.sendText("company");
            client.syncElements(2000,20000);
        }
    }

}
