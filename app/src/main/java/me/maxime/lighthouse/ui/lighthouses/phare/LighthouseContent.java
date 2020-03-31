package me.maxime.lighthouse.ui.lighthouses.phare;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.maxime.lighthouse.JSONParser;
import me.maxime.lighthouse.MainActivity;

public class LighthouseContent {

    public static final List<LighthouseItem> ITEMS = new ArrayList<>();
    public static final Map<String, LighthouseItem> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 25;

    static final String DEFAULT_AUTHOR = "";
    static final String DEFAULT_COLOR = "#22EEEE00";
    static final String DEFAULT_COLOR_STR = "blanc";
    static final String RED_COLOR = "#22DD0000";
    static final String RED_COLOR_STR = "rouge";
    static final String GREEN_COLOR = "#2200DD00";
    static final String GREEN_COLOR_STR = "vert";

    private static String URL = "http://www.laurent-freund.fr/cours/android/phares/web/data/phares_all.json";

    private static void addItem(LighthouseItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static LighthouseItem createPhareItem(String id, String name, String filename, String region, int construction, int hauteur, int nbEclat, int periode, int portee, int automatisation, double lat, double lon, String couleur, String auteur) {
        return new LighthouseItem(id, name, filename, region, construction, hauteur, nbEclat, periode, portee, automatisation, lat, lon, couleur, auteur);
    }

    private static LighthouseItem createPhareItem(String id, String name, String filename, String region, int construction, int hauteur, int nbEclat, int periode, int portee, int automatisation, double lat, double lon) {
        return createPhareItem(id, name, filename, region, construction, hauteur, nbEclat, periode, portee, automatisation, lat, lon, DEFAULT_COLOR, DEFAULT_AUTHOR);
    }

    public static LighthouseItem findById(int paramInt) {
        for (LighthouseItem lighthouseItem : ITEMS) {
            if (Integer.parseInt(lighthouseItem.id) == paramInt)
                return lighthouseItem;
        }
        return null;
    }

    public static void loadPhareAllJson() {
        new ConnectAsyncTask().execute();
        String str = loadStrJson("phares_all.json");
        try {
            JSONObject jSONObject = new JSONObject(str);
            JSONArray jsonA = jSONObject.getJSONObject("phares").getJSONArray("liste");
            for (int i = 0; i < jsonA.length(); i++) {
                JSONObject msg = (JSONObject) jsonA.get(i);
                Log.d("LighthouseContent", "Name: " + msg.getString("name"));

                String couleur;
                try {
                    couleur = msg.getString("couleur");
                    if (couleur.equals(RED_COLOR_STR)) {
                        couleur = RED_COLOR;
                    } else if (couleur.equals(GREEN_COLOR_STR)) {
                        couleur = GREEN_COLOR;
                    } else {
                        couleur = DEFAULT_COLOR;
                    }
                } catch (JSONException e) {
                    couleur = DEFAULT_COLOR;
                }

                String auteur;
                try {
                    auteur = msg.getString("auteur");
                } catch (JSONException e) {
                    auteur = DEFAULT_AUTHOR;
                }

                addItem(new LighthouseItem(
                        msg.getString("id"),
                        msg.getString("name"),
                        msg.getString("filename"),
                        msg.getString("region"),
                        msg.getInt("construction"),
                        msg.getInt("hauteur"),
                        msg.getInt("eclat"),
                        msg.getInt("periode"),
                        msg.getInt("portee"),
                        msg.getInt("automatisation"),
                        msg.getDouble("lat"),
                        msg.getDouble("lon"),
                        couleur,
                        auteur));
            }
        } catch (JSONException jSONException) {
            jSONException.printStackTrace();
        }
    }

    private static String loadStrJson(String fileName) {
        String str = new String();
        try {
            BufferedReader br = new BufferedReader(new
                    InputStreamReader(MainActivity.getContext().getAssets().open(fileName)));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            str = new String(sb.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    private static class ConnectAsyncTask extends AsyncTask<String, String, JSONObject> {
        private ConnectAsyncTask() {
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.getJSONFromUrl(URL);
            Log.d("ConnectAsyncTask", "JSON downloaded");
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                JSONArray jsonA = jsonObject.getJSONObject("phares").getJSONArray("liste");
                for (int i = 0; i < jsonA.length(); i++) {
                    JSONObject msg = (JSONObject) jsonA.get(i);
                    Log.d("ConnectAsyncTask", "Name: " + msg.getString("name"));
                    String couleur;
                    try {
                        couleur = msg.getString("couleur");
                        if (couleur.equals(RED_COLOR_STR)) {
                            couleur = RED_COLOR;
                        } else if (couleur.equals(GREEN_COLOR_STR)) {
                            couleur = GREEN_COLOR;
                        } else {
                            couleur = DEFAULT_COLOR;
                        }
                    } catch (JSONException e) {
                        couleur = DEFAULT_COLOR;
                    }

                    String auteur;
                    try {
                        auteur = msg.getString("auteur");
                    } catch (JSONException e) {
                        auteur = DEFAULT_AUTHOR;
                    }

                    addItem(new LighthouseItem(
                            msg.getString("id"),
                            msg.getString("name"),
                            msg.getString("filename"),
                            msg.getString("region"),
                            msg.getInt("construction"),
                            msg.getInt("hauteur"),
                            msg.getInt("eclat"),
                            msg.getInt("periode"),
                            msg.getInt("portee"),
                            msg.getInt("automatisation"),
                            msg.getDouble("lat"),
                            msg.getDouble("lon"),
                            couleur,
                            auteur));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public static class LighthouseItem {
        public final int automatisation;
        public final int construction;
        public final String couleur;
        public final int hauteur;
        public final String id;
        public final String imgFile;
        public final double lat;
        public final double lon;
        public final String name;
        public final int nbEclat;
        public final int periode;
        public final int portee;
        public final String region;
        public final String auteur;

        public LighthouseItem(String id, String name, String filename, String region, int construction, int hauteur, int nbEclat, int periode, int portee, int automatisation, double lat, double lon, String couleur, String auteur) {
            this.id = id;
            this.name = name;
            this.imgFile = filename;
            this.region = region;
            this.construction = construction;
            this.hauteur = hauteur;
            this.nbEclat = nbEclat;
            this.periode = periode;
            this.portee = portee;
            this.automatisation = automatisation;
            this.lat = lat;
            this.lon = lon;
            this.couleur = couleur;
            this.auteur = auteur;
        }

        public LighthouseItem(String id, String name, String filename, String region, int construction, int hauteur, int nbEclat, int periode, int portee, int automatisation, double lat, double lon) {
            this(id, name, filename, region, construction, hauteur, nbEclat, periode, portee, automatisation, lat, lon, DEFAULT_COLOR, DEFAULT_AUTHOR);
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.name);
            stringBuilder.append(",");
            stringBuilder.append(this.region);
            return stringBuilder.toString();
        }
    }
}