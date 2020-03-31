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

import me.maxime.lighthouse.MainActivity;

public class LighthouseContent {

    public static final List<LighthouseItem> ITEMS = new ArrayList<>();
    public static final Map<String, LighthouseItem> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 25;

    static final String DEFAULT_AUTHOR = "";
    static final String DEFAULT_COLOR = "#22EEEE00";
    static final String DEFAULT_COLOR_STR = "blanc";
    static final String ROUGE_COLOR = "#22DD0000";
    static final String ROUGE_COLOR_STR = "rouge";
    static final String VERT_COLOR = "#2200DD00";
    static final String VERT_COLOR_STR = "vert";

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
                    if (couleur.equals(ROUGE_COLOR_STR)) {
                        couleur = ROUGE_COLOR;
                    } else if (couleur.equals(VERT_COLOR_STR)) {
                        couleur = VERT_COLOR;
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

    public static void loadPhareJson() {
        String str = loadStrJson("phares.json");
        try {
            JSONObject jObjConnection = null;
            jObjConnection = new JSONObject(str);
            JSONObject jsonBix = jObjConnection.getJSONObject("phares");
            JSONArray jsonA = jsonBix.getJSONArray("liste");
            for (int i = 0; i < jsonA.length(); i++) {
                JSONObject msg = (JSONObject) jsonA.get(i);
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
                        msg.getDouble("lon")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
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

    private static class ConnectAsyncTask extends AsyncTask<String, Void, String> {
        private ConnectAsyncTask() {
        }

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }
    
    /*protected String doInBackground(String... param1VarArgs) {
      Scanner scanner1 = null;
      param1VarArgs = null;
      String str2 = param1VarArgs;
      scanner2 = scanner1;
      try {
        URL uRL = new URL();
        str2 = param1VarArgs;
        scanner2 = scanner1;
        this("http://www.laurent-freund.fr/cours/android/phares/versions.json");
        str2 = param1VarArgs;
        scanner2 = scanner1;
        httpURLConnection = (HttpURLConnection)uRL.openConnection();
        try {
          BufferedInputStream bufferedInputStream = new BufferedInputStream();
          this(httpURLConnection.getInputStream());
          scanner2 = new Scanner();
          this(bufferedInputStream, "UTF-8");
        } finally {
          str2 = str1;
          scanner2 = scanner1;
          httpURLConnection.disconnect();
          str2 = str1;
          scanner2 = scanner1;
        } 
      } catch (MalformedURLException b) {
        Log.d("PhareContent", "Prb URL database connection");
      } catch (IOException a) {
        Log.d("PhareContent", "Prb I/O database connection");
      }
      return str1;
    }*/
/*
        protected void onPostExecute(String param1String) {
            Log.d("PhareContent", param1String);
            try {
                JSONObject jSONObject2 = new JSONObject();
                JSONObject jSONObject1 = jSONObject2.getJSONObject("version");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(MainActivity.getContext().getResources().getString(2131951686));
                stringBuilder.append(" ");
                stringBuilder.append(jSONObject1.getString("author"));
                stringBuilder.append(", ");
                stringBuilder.append(jSONObject1.getString("date"));
                String str = stringBuilder.toString();
                Toast.makeText(MainActivity.getContext(), str, 0).show();
            } catch (JSONException a) {
                a.printStackTrace();
            }
        }*/
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