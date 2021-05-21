package snippet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.List;
import java.io.*;

public class FullResponseBuilder {
    public static String getFullResponse(HttpURLConnection con) throws IOException {
        StringBuilder fullResponseBuilder = new StringBuilder();

        //fullResponseBuilder.append(con.getResponseCode()).append(" ").append("\n");

        // con.getHeaderFields().entrySet().stream().filter(entry -> entry.getKey() !=
        // null).forEach(entry -> {

        // fullResponseBuilder.append(entry.getKey()).append(": ");

        // List<String> headerValues = entry.getValue();

        // Iterator<String> it = headerValues.iterator();
        // if (it.hasNext()) {
        // fullResponseBuilder.append(it.next());

        // while (it.hasNext()) {
        // fullResponseBuilder.append(", ").append(it.next());
        // }
        // }
        // fullResponseBuilder.append("\n");
        // });

        Reader streamReader = null;

        System.out.println(con.getResponseCode());
        if (con.getResponseCode() == 400)
            return ("Bad Request");
        else if (con.getResponseCode() == 401)
            return ("Not Authorized");
        else if (con.getResponseCode() == 402)
            return ("Out or quota");
        else if (con.getResponseCode() == 403)
            return ("No Data");

        else {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.contains("token") && !(new File("softeng19bAPI.token").exists())) {
                    PrintWriter writer = new PrintWriter("softeng19bAPI.token", "UTF-8");
                    writer.println(inputLine.substring(10, inputLine.length() - 2));
                    writer.close();
                }
                if (inputLine.contains("Logged Out") && (new File("softeng19bAPI.token").exists())) {
                    File tokenFile = new File("softeng19bAPI.token");
                    tokenFile.delete();
                    return " ";
                }
                if (!inputLine.equals("null"))
                    content.append(inputLine);
                
            }
            in.close();
            fullResponseBuilder.append("Response: ").append(content);
            return fullResponseBuilder.toString();
        }
    }

}
