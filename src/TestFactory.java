import org.w3c.dom.Element;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by navot.dako on 10/13/2016.
 */
public class TestFactory {

    List<List<Screen>> steps = null;

    public TestFactory(){
        steps=new ArrayList<>();
    }

    public void CreateFunctionalTest(Screen currentScreen, Screen lastScreen) {
        PrintWriter out = null;
        try {
            out = new PrintWriter("Tests\\"+currentScreen.screenName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        out.println(lastScreen.screenName);
        out.println(currentScreen.command);
        out.println(currentScreen.screenName);

        out.println("---------------------------------------------------------------------------------------------------------------------------------------");


        out.close();
    }

    public void CreateLayoutTest(Screen currentScreen) {
        PrintWriter out = null;
        try {
            out = new PrintWriter("Tests\\"+currentScreen.screenName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, Element> UIElement : currentScreen.elementsMap.entrySet()) {

            out.println(UIElement.getKey());
            out.println(UIElement.getValue());
            out.println("---------------------------------------------------------------------------------------------------------------------------------------");

        }
        out.close();
    }
}
