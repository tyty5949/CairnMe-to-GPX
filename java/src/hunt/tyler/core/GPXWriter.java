package hunt.tyler.core;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.HashMap;

public class GPXWriter {
    public static void writeTheCairn(String pathname, TheCairn theCairn) {
        boolean error = false;
        long startTime = System.currentTimeMillis();
        System.out.println("\nStarting writing Cairn to GPX...");

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // Create root element
            Document gpxDoc = documentBuilder.newDocument();
            Element rootElement = gpxDoc.createElement("gpx");
            gpxDoc.appendChild(rootElement);
            rootElement.setAttribute("version", "1.1");
            rootElement.setAttribute("creator",
                    "CairnMe to GPX - https://github.com/tyty5949/CairnMe-to-GPX");
            rootElement.setAttribute("xmlns", "http://www.topografix.com/GPX/1/1");
            rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            rootElement.setAttribute("xsi:schemaLocation",
                    "http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd");

            // Build GPX file
            System.out.println("Building file...");
            buildTheCairnGPX(gpxDoc, rootElement, theCairn);

            // Write to file
            System.out.println("Writing file...");
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(gpxDoc);
            StreamResult streamResult = new StreamResult(new File(pathname));
            transformer.transform(source, streamResult);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
            error = true;
        }

        if (error) {
            System.out.println("Unable to scrape cairn!");
        }
        double completionTime = (System.currentTimeMillis() - startTime) / 1000.0;
        System.out.printf("Completed writing GPX in %.4f seconds!\n\n", completionTime);
    }

    private static void buildTheCairnGPX(Document doc, Element root, TheCairn theCairn) {
        // Build root
        buildTheCairnRootElement(doc, root, theCairn);

        // Build tracks
        buildTheCairnTrack(doc, root, theCairn);
    }

    private static void buildTheCairnRootElement(Document doc, Element root, TheCairn theCairn) {
        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        // Destination
        if (theCairn.getMetadata().containsKey("destination")) {
            addTextElementToDoc(doc, metadata, "name", theCairn.getMetadata().get("destination"));
        }

        // Author
        String name = "";
        if (theCairn.getMetadata().containsKey("name")) {
            name += theCairn.getMetadata().get("name");
        }
        if (theCairn.getMetadata().containsKey("last_name")) {
            if (!name.equals("")) {
                name += " ";
            }
            name += theCairn.getMetadata().get("last_name");
        }
        if (!name.equals("")) {
            addTextElementToDoc(doc, metadata, "author", name);
        }

        // Email
        if (theCairn.getMetadata().containsKey("email")) {
            addTextElementToDoc(doc, metadata, "email", theCairn.getMetadata().get("email"));
        }

        // Description
        if (theCairn.getMetadata().containsKey("msg")) {
            addTextElementToDoc(doc, metadata, "desc", theCairn.getMetadata().get("msg"));
        }

        // Url
        if (theCairn.getMetadata().containsKey("cairn_id")) {
            addTextElementToDoc(doc, metadata, "url", "http://locate.cairnme.com/share?cairn_id=" +
                    theCairn.getMetadata().get("cairn_id"));
        }

        // Time
        if (theCairn.getMetadata().containsKey("cairn_start")) {
            addTextElementToDoc(doc, metadata, "time", TimeFormatter.cairnToISO8601(theCairn.getMetadata()
                    .get("cairn_start")));
        }
    }

    // TODO - Track segment splitting
    private static void buildTheCairnTrack(Document doc, Element root, TheCairn theCairn) {
        Element track = doc.createElement("trk");
        root.appendChild(track);

        Element trackSeg = doc.createElement("trkseg");
        track.appendChild(trackSeg);

        for (HashMap<String, String> point : theCairn.getTrackPoints()) {
            Element trackPoint = doc.createElement("trkpt");
            trackSeg.appendChild(trackPoint);
            trackPoint.setAttribute("lat", point.get("lt"));
            trackPoint.setAttribute("lon", point.get("lg"));
            addTextElementToDoc(doc, trackPoint, "time", TimeFormatter.cairnToISO8601(point.get("ra")
                    .substring(0, 19)));
        }
    }

    private static void addTextElementToDoc(Document doc, Element root, String tagName, String text) {
        Element child = doc.createElement(tagName);
        child.appendChild(doc.createTextNode(text));
        root.appendChild(child);
    }
}
