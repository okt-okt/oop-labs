import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ParserInf {
    private String searchQuery = "https://povar.ru/xmlsearch?query=";
    private String recipeQuery = "https://povar.ru/recipes/";

    public Map<String, String> searchRecipes(String query) throws IOException, InterruptedException {
        Map<String, String> result = new HashMap<>();
        String text = makeHttp(searchQuery + query.replace(" ", "%20").replace("+", "%2B"));
        int pos_a = 0, pos_b, pos_c;
        String v0 = "class=\"recipe\">";
        String v1 = "href=\"/recipes/";
        String a = "class=\"listRecipieTitle\">";
        String b = "<";
        String c = "class=\"txt\">";
        while((pos_a = text.indexOf(v0, pos_a)) != -1){
            pos_a = text.indexOf(v1, pos_a+1) + v1.length();
            pos_b = text.indexOf("\"", pos_a+1);
            String id = text.substring(pos_a, pos_b);
            System.out.println("id: " + id);

            pos_a = text.indexOf(a, pos_a+1) + a.length();
            pos_b = text.indexOf(b, pos_a+1);
            String title = text.substring(pos_a, pos_b).replaceAll("&quot;", "\"");
            System.out.println("title: " + title);

            result.put(id, title);
        }
        return result;
    }

    public String getInfo(String recipe) throws IOException, InterruptedException {
        String text = makeHttp(recipeQuery + recipe);
        int pos_a = 0, pos_b, pos_c;
        String v0 = "itemprop=\"name\">";
        String v1 = "itemprop=\"recipeIngredient\"";
        String a = "class=\"name\">";
        String a1 = "value";
        String a2 = "u-unit-name";
        String a4 = ">";
        String b = "<";
        String c = "class=\"detailed_step_description_big\">";
        int prev = 0;

        pos_a = text.indexOf(v0, text.indexOf("<h1")+1) + v0.length();
        pos_b = text.indexOf(b, pos_a+1);
        StringBuilder result = new StringBuilder(text.substring(pos_a, pos_b).replaceAll("&quot;","\"") + "\n\nИнгредиенты:");
        while ((pos_a = text.indexOf(v1, pos_a)) != -1) {
            pos_a = text.indexOf(a, pos_a) + a.length();
            pos_b = text.indexOf(b, pos_a);
            String i = text.substring(pos_a, pos_b).replaceAll("&quot;", "\"").trim();
            if(pos_a < prev) break;
            prev=pos_a;

            pos_a = text.indexOf(a1, pos_b);
            if(pos_a!=-1) {
                pos_a = text.indexOf(a4, pos_a + a1.length()) + a4.length();
                pos_b = text.indexOf(b, pos_a);
                i = i + " - " + text.substring(pos_a, pos_b).trim();
            }
            if(pos_a < prev) break;
            prev=pos_a;

            pos_a = text.indexOf(a2, pos_b) + a2.length();
            pos_a = text.indexOf(a4, pos_a) + a4.length();
            pos_b = text.indexOf(b, pos_a);
            i = i + " " + text.substring(pos_a, pos_b).trim();
            if(pos_a < prev) break;
            prev=pos_a;

            System.out.println(i.replace(" -  ", ""));
            result.append("\n").append(i.replace(" -  ", ""));
        }
        result.append("\n\nПриготовление:");
        pos_a = text.indexOf("itemprop=\"recipeInstructions\"");
        while ((pos_a = text.indexOf("<p>", pos_a)) != -1) {
            pos_b = text.indexOf(b, (pos_a += 3));
            result.append("\n\n").append(text.substring(pos_a, pos_b).replaceAll("&quot;", "\""));
        }

        return result.toString();
    }

    private String makeHttp(String url) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                //.header("User-Agent", "MyBot/1.0")
                .GET().build();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());
        return response.body();

    }
}
