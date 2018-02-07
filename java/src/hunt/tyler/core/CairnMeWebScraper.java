package hunt.tyler.core;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class CairnMeWebScraper {

    public static TheCairn scrapeTheCairn(String cairnId) {
        System.out.println("\nStarting scrape of cairn \'" + cairnId + "\'");
        long startTime = System.currentTimeMillis();
        HashMap<String, String> metadata;
        ArrayList<HashMap<String, String>> points;
        boolean error = false;

        // Scrape the cairn json string
        String jsonString = scrapeTheCairnJSON(cairnId);
        if (jsonString == null) {
            System.err.println("Failed to scrape the cairn JSON!");
            error = true;
        }

        // Parse the cairn json
        JSONObject json = parseTheCairnJSON(jsonString);
        if (json.keySet().isEmpty()) {
            System.err.println("Failed to parse the cairn JSON!");
            error = true;
        }

        if (error) {
            System.out.println("Unable to scrape cairn!");
            return null;
        }
        double completionTime = (System.currentTimeMillis() - startTime) / 1000.0;
        System.out.printf("Completed scrape of the cairn in %.4f seconds!\n\n", completionTime);

        // Build object
        return new TheCairn(json);
    }

    private static String scrapeTheCairnJSON(String cairnId) {
        String theCairnVar;
        Document doc;

        // Download html from cairnme given cairn_id
        try {
            System.out.println("Downloading \'http://locate.cairnme.com/share?cairn_id=" + cairnId + "\'...");
            doc = Jsoup.connect("http://locate.cairnme.com/share?cairn_id=" + cairnId).get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Search for script containing var
        System.out.println("Isolating script for \'var theCairn\'...");
        Elements scripts = doc.getElementsByTag("script");
        Optional<Element> result = scripts.stream().filter(script -> script.data().startsWith("var theCairn")).findFirst();
        if (result.isPresent()) {
            theCairnVar = result.get().data();
        } else {
            System.err.println("Failed to find the script containing \'var theCairn\'!");
            return null;
        }

        // Isolate var
        theCairnVar = theCairnVar.substring(theCairnVar.indexOf('{'), theCairnVar.indexOf('\n') - 1);

        // Return
        return theCairnVar;
    }

    private static JSONObject parseTheCairnJSON(String theCairnJSON) {
        return new JSONObject(theCairnJSON);
    }
}