import com.experitest.client.Client;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Map;

/**
 * Created by navot on 12/10/2016.
 */
public class Clicker {
    private Client client=null;

    public Clicker(Client client){
        this.client=client;
    }

    public Boolean ClickingOnElementWithProperty(Map.Entry<String, Element> entry) throws IOException, ParserConfigurationException, SAXException {
        boolean success = ClickingOnElementWithProperty(entry,"id");
        if (!success)
            success = ClickingOnElementWithProperty(entry,"contentDescription");
        if (!success)
            ClickingOnElementWithTEXT(entry);
        return success;
    }

    public Boolean ClickingOnElementWithProperty(Map.Entry<String, Element> entry, String property) throws IOException, ParserConfigurationException, SAXException {
       if(!entry.getValue().getAttribute(property).equals("")){
            System.out.println("ClickingOnElementWithProperty - "+property+"="+entry.getValue().getAttribute(property));
            try{
                client.click("Native",property+"="+entry.getValue().getAttribute(property),0,1);

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
