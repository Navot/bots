import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

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

    public void CreatePathTest(Screen currentScreen) {
        System.out.println("Creating Functional Test For - "+currentScreen.screenName);
        PrintWriter out = null;
        try {
            out = new PrintWriter("Results\\tests\\FT_"+currentScreen.screenName+"_"+currentScreen.routes.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<String> route = currentScreen.routes.get(currentScreen.routes.size()-1);
        for (int i = 0; i < route.size(); i++) {
            out.println(route.get(i));
            out.println("---------------------------------------------------------------------------------------------------------------------------------------");

        }
        out.close();
        System.out.println("Done Creating Functional Test - Tests\\FT_"+currentScreen.screenName);
    }

    public void CreateLayoutTest(Screen currentScreen) {
        System.out.println("Creating Layout Test For - "+currentScreen.screenName);
        PrintWriter out = null;
        try {
            out = new PrintWriter("Results\\tests\\LT_"+currentScreen.screenName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, Element> UIElement : currentScreen.elementsMap.entrySet()) {

            out.println(UIElement.getKey());
            try {
                out.println(printElement(UIElement.getValue()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            }
            out.println("---------------------------------------------------------------------------------------------------------------------------------------");

        }
        out.close();
        System.out.println("Done Creating Layout Test - Tests\\LT_"+currentScreen.screenName);
    }
    public static String printElement(Element node) throws IOException, TransformerException {
        DOMImplementationLS lsImpl = (DOMImplementationLS)node.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
        LSSerializer serializer = lsImpl.createLSSerializer();
        serializer.getDomConfig().setParameter("xml-declaration", false); //by default its true, so set it to false to get String without xml-declaration
        return serializer.writeToString(node);
    }
}
