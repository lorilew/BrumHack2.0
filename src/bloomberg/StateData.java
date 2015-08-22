package bloomberg;

// ReferenceData.java
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public class StateData {

    public static final String keyStorePW = "secure";
    public static final String trustStorePW = "secure2";
    public static final String clientCert = "client.p12";
    public static final String bbCert = "bloomberg.jks";
    
    public static Map<String,String> getIndices(){
    	
    	String apiUrl = "https://http-api.openbloomberg.com" + "/request?ns=blp&service=instruments&type=instrumentListRequest";
    	String jsonFile = "InstrumentLookup.json";
    	
    	Map<String,String> stateIndexes = new TreeMap<String,String>();
        try {
            // load the client public/private key from PKCS12
            KeyStore clientStore = KeyStore.getInstance("PKCS12");
            clientStore.load(new FileInputStream(clientCert), keyStorePW.toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(clientStore, keyStorePW.toCharArray());
            KeyManager[] kms = kmf.getKeyManagers();

            // load the public key of the CA from JKS,
            // so we can verify the server certificate.
            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(new FileInputStream(bbCert), trustStorePW.toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);
            TrustManager[] tms = tmf.getTrustManagers();

            // initialize the SSLContext with the keys,
            // KeyManager: client public/private key, TrustManager: server public key
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kms, tms, new SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            URL url = new URL(apiUrl);

            // open connection to the server
            HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();

            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("User-Agent", "blpapi-http-java-example");
            urlConn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            urlConn.setDoOutput(true);
            urlConn.setRequestProperty("Content-Type", "application/json; charset=utf8");
            

            // write the json request to the output stream
            DataOutputStream wr = new DataOutputStream(urlConn.getOutputStream());
            FileInputStream fis = new FileInputStream(jsonFile);
            byte[] buffer = new byte[1024];
            int len = fis.read(buffer);
            while (-1 < len) {
                wr.write(buffer, 0, len);
                len = fis.read(buffer);
            }
            wr.flush();
            wr.close();

                
           
            // read the whatever we get back
            int responseCode = urlConn.getResponseCode();
            
//            System.out.println();
//            out.write("\nSending 'POST' request to URL : " + url);
//            System.out.println("Response Code : " + responseCode);
//            out.write("Response Code : " + responseCode);
            
            // general method, same as with data binding
            ObjectMapper mapper = new ObjectMapper();
            // (note: can also use more specific type, like ArrayNode or ObjectNode!)
            JsonNode rootNode = mapper.readValue(new InputStreamReader(urlConn.getInputStream()), JsonNode.class); // src can be a File, URL, InputStream etc
            
            // get state names
            List<String> stateNames = esriData.StateData.getStateNames();
            for(String stateName : stateNames){
            	 for(JsonNode node : rootNode.get("data").get(0).get("results")){
//                 	System.out.println(node.get("description"));
                 	if (node.get("description").getTextValue().contains(stateName)){
                 		stateIndexes.put(stateName,(node.get("security").getTextValue()).replace("<index>", " Index"));
//                 		System.out.println(node.get("security"));
                 	}
                 }
            }
            
            
            
           
            
//            System.out.println(rootNode.get("data").get(0).get("results").get(0).get("description") );
            
//            BufferedWriter out = null;
//            
//            FileWriter fstream = new FileWriter("data.json", false); //true tells to append data.
//            out = new BufferedWriter(fstream);
//            
//            BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
//            String inputLine;
//            StringBuffer response = new StringBuffer();
//
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//            }
//            in.close();
//            
//            System.out.println(response.toString());
//            out.write(response.toString());
//            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
		return stateIndexes;
    }
    
public static Map<String,Integer> getIndexData(Map<String,String> stateIndexes){
    	
    	String apiUrl = "https://http-api.openbloomberg.com" + "/request?ns=blp&service=refdata&type=ReferenceDataRequest";
    	String requestStr = "{\"securities\": [";
    			
    	for(String index: stateIndexes.keySet()){
    		requestStr+="\""+stateIndexes.get(index)+"\",";
    	}
    	requestStr = requestStr.substring(0, requestStr.length()-1);
    	requestStr+="],\"fields\": [\"PX_LAST\"]}";
    	
    	
//    	requestStr="{\"securities\": [\"PCSTNC Index\",\"PCSTIN Index\",\"PCSTWY Index\",\"PCSTUT Index\",\"PCSTAR Index\",\"PCSTMT Index\",\"PCSTKT Index\",\"PCSTCA Index\",\"PCSTKS Index\",\"PCSTDE Index\",\"PCSTFL Index\",\"PCSTPA Index\",\"PCSTIW Index\",\"PCSTMS Index\",\"PCSTIL Index\",\"PCSTTX Index\",\"PCSTCN Index\",\"PCSTGA Index\",\"PCSTMD Index\",\"PCSTWV Index\",\"PCSTID Index\",\"PCSTVT Index\",\"PCSTOR Index\",\"PCSTME Index\",\"PCSTOK Index\",\"PCSTTN Index\",\"PCSTAL Index\",\"PCSTAK Index\",\"PCSTWA Index\",\"PCSTSC Index\",\"PCSTNB Index\",\"PCSTWV Index\",\"PCSTMA Index\",\"PCSTCO Index\",\"PCSTMO Index\",\"PCSTAA Index\",\"PCSTND Index\",\"PCSTWI Index\",\"PCSTNV Index\",\"PCSTNY Index\",\"PCSTRI Index\",\"PCSTHW Index\",\"PCSTSD Index\",\"PCSTMN Index\",\"PCSTNJ Index\",\"PCSTMI Index\",\"PCSTNM Index\",\"PCSTNH Index\",\"PCSTLA Index\",\"PCSTOH Index\"],\"fields\": [\"PX_LAST\"]}";
//    	System.out.println(requestStr);
    	Map<String,Integer> result = new TreeMap<String,Integer>();
        try {
            // load the client public/private key from PKCS12
            KeyStore clientStore = KeyStore.getInstance("PKCS12");
            clientStore.load(new FileInputStream(clientCert), keyStorePW.toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(clientStore, keyStorePW.toCharArray());
            KeyManager[] kms = kmf.getKeyManagers();

            // load the public key of the CA from JKS,
            // so we can verify the server certificate.
            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(new FileInputStream(bbCert), trustStorePW.toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);
            TrustManager[] tms = tmf.getTrustManagers();

            // initialize the SSLContext with the keys,
            // KeyManager: client public/private key, TrustManager: server public key
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kms, tms, new SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            URL url = new URL(apiUrl);

            // open connection to the server
            HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();

            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("User-Agent", "blpapi-http-java-example");
            urlConn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            urlConn.setDoOutput(true);
            urlConn.setRequestProperty("Content-Type", "application/json; charset=utf8");
            

            // write the json request to the output stream
            DataOutputStream wr = new DataOutputStream(urlConn.getOutputStream());
            
            InputStream is = new ByteArrayInputStream(requestStr.getBytes(StandardCharsets.UTF_8));
            byte[] buffer = new byte[1024];
            int len = is.read(buffer);
            while (-1 < len) {
                wr.write(buffer, 0, len);
                len = is.read(buffer);
            }
            wr.flush();
            wr.close();

                
           
            // read the whatever we get back
            int responseCode = urlConn.getResponseCode();
            
//            System.out.println();
//            out.write("\nSending 'POST' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
//            out.write("Response Code : " + responseCode);
            
            // general method, same as with data binding
            ObjectMapper mapper = new ObjectMapper();
            // (note: can also use more specific type, like ArrayNode or ObjectNode!)
            JsonNode rootNode = mapper.readValue(new InputStreamReader(urlConn.getInputStream()), JsonNode.class); // src can be a File, URL, InputStream etc
            
            // get state names
            for(JsonNode cluster : rootNode.get("data") ){
            	for(JsonNode node : cluster.get("securityData")){
//            		System.out.print(node.get("security").asText());
            		for(String key : stateIndexes.keySet()){
            			if(stateIndexes.get(key).equals(node.get("security").asText()))
            				result.put(key, Integer.parseInt(node.get("fieldData").get("PX_LAST").asText()));
            		}
//            		System.out.println(node.get("fieldData").get("PX_LAST").asText());
            	}
            }

//            	System.out.println(rootNode);

//            System.out.println(rootNode.get("data").get(0).get("securityData").get(0).get("fieldData").get("PX_LAST").asText());
            
//            System.out.println(stateData);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
		return result;
    }
    
	public static Map<String,Integer> stateData(){
		Map<String,Integer> result = new TreeMap<String,Integer>();
		Map<String,String> stateIndexes = getIndices();
//		for(String index : stateIndexes.keySet()){
//    		result.put(index, getIndexData(stateIndexes.get(index)));
//    	}
		return getIndexData(stateIndexes);
	}

    public static void main(String[] args) {
    	final long startTime = System.currentTimeMillis();
    	System.out.println(stateData());
    	System.out.println((System.currentTimeMillis() - startTime)/1000);
    }
}