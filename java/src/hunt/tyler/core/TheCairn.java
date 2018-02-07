package hunt.tyler.core;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TheCairn {

    private HashMap<String, String> metadata;
    private ArrayList<HashMap<String, String>> trackPoints;

    TheCairn(JSONObject jsonObject) {
        metadata = new HashMap<>();
        trackPoints = new ArrayList<>();

        // Add points
        JSONArray jsonPoints = jsonObject.getJSONArray("points");
        for (int i = 0; i < jsonPoints.length(); i++) {
            JSONObject jsonPoint = jsonPoints.getJSONObject(i);
            HashMap<String, String> pointMap = new HashMap<>();
            pointMap.put("b", "" + ((int) jsonPoint.remove("b")));
            pointMap.put("ha", "" + ((int) jsonPoint.remove("ha")));
            pointMap.put("s", "" + ((int) jsonPoint.remove("s")));
            for (String item : jsonPoint.keySet()) {
                try {
                    pointMap.put(item, jsonPoint.getString(item));
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
            trackPoints.add(pointMap);
        }

        // Special cases
        metadata.put("safe", jsonObject.getBoolean("safe") ? "true" : "false");
        jsonObject.remove("safe");
        jsonObject.remove("points");
        jsonObject.remove("recipients");

        // Add metadata
        for (String item : jsonObject.keySet()) {
            try {
                metadata.put(item, jsonObject.getString(item));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public HashMap<String, String> getMetadata() {
        return metadata;
    }

    public ArrayList<HashMap<String, String>> getTrackPoints() {
        return trackPoints;
    }

    public String toString() {
        StringBuilder sBuffer = new StringBuilder();

        // Print metadata
        sBuffer.append("metadata\n{\n");
        for (String key : metadata.keySet()) {
            sBuffer.append("  \"" + key + "\":\"" + metadata.get(key) + "\"\n");
        }
        sBuffer.append("}\n\n");

        return sBuffer.toString();
    }

    public String toPointString() {
        StringBuilder sBuffer = new StringBuilder();

        // Build points
        sBuffer.append("points\n{\n");
        for (HashMap<String, String> point : trackPoints) {
            sBuffer.append("  pt\n  {\n");
            for (String key : point.keySet()) {
                sBuffer.append("    \"" + key + "\":\"" + point.get(key) + "\"\n");
            }
            sBuffer.append("  }\n");
        }
        sBuffer.append("}\n");

        return sBuffer.toString();
    }
}