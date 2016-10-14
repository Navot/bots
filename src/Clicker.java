import com.experitest.client.Client;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by navot on 12/10/2016.
 */
public class Clicker {
    private Client client=null;
    String command="";
    public Clicker(Client client){
        this.client=client;
    }

    public Map<String, String> ClickingOnElement(Map.Entry<String, Element> entry) throws IOException, ParserConfigurationException, SAXException {
        Map<String, String> clickResult = new HashMap<>();
       if(ClickingOnElementWithProperty(entry,"id")){
           clickResult.put("result","true");
           clickResult.put("step",command);
           return clickResult;
       }
        if (ClickingOnElementWithProperty(entry,"contentDescription")){
            clickResult.put("result","true");
            clickResult.put("step",command);
            return clickResult;
        }
        if (ClickingOnElementWithTEXT(entry)){
            clickResult.put("result","true");
            clickResult.put("step",command);
            return clickResult;
        }
        clickResult.put("result","false");
        clickResult.put("step",command);
        return clickResult;
    }

    public Boolean ClickingOnElementWithProperty(Map.Entry<String, Element> entry, String property) throws IOException, ParserConfigurationException, SAXException {
       if(!entry.getValue().getAttribute(property).equals("")){
            System.out.println("ClickingOnElement - "+property+"="+entry.getValue().getAttribute(property));
            try{
                client.click("Native",property+"="+entry.getValue().getAttribute(property),0,1);
                command = ("ClickingOnElement - "+property+"="+entry.getValue().getAttribute(property));
                return true;
            }catch (Exception e){
                System.out.println("Could not click on "+property+"="+entry.getValue().getAttribute(property));
                System.out.println("Returning FALSE");
                return false;
            }
        }
        return false;
    }

    public Boolean ClickingOnElementWithTEXT(Map.Entry<String, Element> entry) throws IOException, ParserConfigurationException, SAXException {

        if (entry.getValue().getTextContent() != null) {
            if (!entry.getValue().getTextContent().equals("")) {
                System.out.println("ClickingOnElementWithTEXT - text=" + entry.getValue().getTextContent());
                try {
                    client.click("Native", "text=" + entry.getValue().getTextContent(), 0, 1);
                    command = ("ClickingOnElementWithTEXT - text="+entry.getValue().getTextContent());
                    return true;

                } catch (Exception e) {
                    System.out.println("Could not click on text=" + entry.getValue().getTextContent());
                    System.out.println("Returning FALSE");
                    return false;
                }
            }
        }
       return false;

    }
}
