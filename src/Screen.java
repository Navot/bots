import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Screen {
    String screenName;
    String screenElementsString;
    String command;
    Map<String, Element>  elementsMap = null;
    List<List<String>> allRoutes = null;
    public List<String> liveRoute;
    Screen backScreen = null;


    public Screen(List<String> route, String elementsString) throws IOException, SAXException, ParserConfigurationException {
        this.command =route.get(route.size()-1);

        this.screenName = getName(command);

        this.screenElementsString = (elementsString.equals("")) ? Runner.CC.GetElements() : elementsString;

        Document elementsDoc = Utilities.getDocumentFromString(screenElementsString);
        elementsMap = ScreensManager.GetVIPElementsFromDoc(elementsDoc);

        allRoutes = new ArrayList<>();
        allRoutes.add(route);
        liveRoute = route;

        ScrollProtocol();
        NewScreenProtocol();
        if (!screenName.contains("LandingPage") && !screenName.contains("Text")) {
            backScreen = getBackScreen();
            Runner.NGR.Navigate(this,backScreen);
        }



    }

    private void ScrollProtocol() {
        /*if(ScreenIsScrollable()){
            scroll();
            while (ScreenDIDScrolled()){
                System.out.println("Screen Was Scrolled !!");
                System.out.println("Getting new Elements");
                AddElementsToElementsMap();
                scroll();
            }
            System.out.println("Screen Does not Scroll now");

        }*/
    }

    private void NewScreenProtocol() throws IOException, SAXException, ParserConfigurationException {
        System.out.println("Adding a new Screen: "+screenName);
        Runner.SM.AddScreen(this);
        Runner.TF.CreatePathTest(this);
        Runner.TF.CreateLayoutTest(this);

        System.out.println("All Elements On the Screen:\n"+Utilities.MapToString(elementsMap));

    }

    private Screen getBackScreen() throws ParserConfigurationException, SAXException, IOException {
        Runner.worker.deviceAction("back");
        Screen temp = Runner.SM.CheckIfBeenHereBefore(Runner.CC.GetElements());
        if (temp!=null){
            System.out.println("The BACK screen for "+screenName + " is - "+temp.screenName);
        }
        return  temp;
    }

    private String getName(String command) {
        return command.substring(command.indexOf("=")+1).replace("'","").replace(":","_").trim()+"_"+ Runner.GetIndex();
    }

    private int GetIndex() {
        return 0;
    }

    public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc),
                new StreamResult(new OutputStreamWriter(out, "UTF-8")));
    }

    public static String printElement(Element node) throws IOException, TransformerException {
        DOMImplementationLS lsImpl = (DOMImplementationLS)node.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
        LSSerializer serializer = lsImpl.createLSSerializer();
        serializer.getDomConfig().setParameter("xml-declaration", false); //by default its true, so set it to false to get String without xml-declaration
       return serializer.writeToString(node);
    }

    public void AddRoute(List<String> route){
        allRoutes.add(route);
    }

    public List<String> getShortestRoute() {
        List<String> shortestRoute = null;
        int minRouteLength = 1000;
        for ( List<String> route : allRoutes) {
            if (route.size()<minRouteLength){
                minRouteLength=route.size();
                shortestRoute = route;
            }

        }
        liveRoute=shortestRoute;
        return shortestRoute;
    }
}
