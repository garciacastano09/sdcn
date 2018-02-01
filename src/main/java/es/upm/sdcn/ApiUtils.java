package es.upm.sdcn;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ApiUtils {

    public static String fromInputStreamToString(InputStream inputStream){
        StringBuilder crunchifyBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = in.readLine()) != null) {
                crunchifyBuilder.append(line);
            }
        } catch (Exception e) {
            System.out.println("Error Parsing in fromInputStreamToString" + e.getMessage());
        }
        String result = crunchifyBuilder.toString();
        System.out.println("Data Received: " + result);
        return result;
    }
}
