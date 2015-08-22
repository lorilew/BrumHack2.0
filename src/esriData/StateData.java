package esriData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.tasks.query.QueryParameters;
import com.esri.core.tasks.query.QueryTask;


public class StateData {
	  final static String URL_STATES = "http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/Demographics/ESRI_Census_USA/MapServer/5";
	  
	  public static List<String> getStateNames(){
		  List<String> names = new ArrayList<String>();
		  	// create a query that gets all states name.
		    QueryParameters query = new QueryParameters();
		    // specify STATE_NAME as the attribute to be fetched.
		    query.setOutFields(new String[] {"STATE_NAME"});
		    // the geometry of the state is not required here. this will reduce the amount of data
		    // retrieved from the server.
		    query.setReturnGeometry(false);
		    // select all records.
		    query.setWhere("1=1");

		    try {
		      // execute the query.
		      QueryTask task = new QueryTask(URL_STATES);
		      FeatureResult queryResult = task.execute(query);

		      // add the query result to the list.
		      Iterator<Object> it = queryResult.iterator();
		      while (it.hasNext()) {
		        Object o = it.next();
		        if (o instanceof Feature) {
		          Feature feature = (Feature) o;
		          if (String.class.isInstance(feature.getAttributeValue("STATE_NAME"))) {
		            String stateName = (String) feature.getAttributeValue("STATE_NAME");
		            if (stateName != null && stateName.length() > 0) {
		            	names.add((String)feature.getAttributeValue("STATE_NAME"));
//		              System.out.println((String)feature.getAttributeValue("STATE_NAME"));
		            }
		          }
		        }
		      }
		    } catch (Exception ex) {
		      ex.printStackTrace();
		    }
			return names;
	  }
	  
	  public static Map<String, Object> getStateData(String stateName){
		  Map<String, Object> stateData = new HashMap<String, Object>();
		  	// -----------------------------------------------------------------------------------------
		    // Query to get attributes of a state
		    // -----------------------------------------------------------------------------------------
		    // create query parameters
		    QueryParameters query = new QueryParameters();

		    // specify * to fetch all attributes.
		    query.setOutFields(new String[] {"*"});
		    // get information of the input state only.
		    query.setWhere("STATE_NAME='" + stateName + "'");

		    Feature selectedState = null;

		    try {
		      // execute the query
		      QueryTask task = new QueryTask(URL_STATES);
		      FeatureResult queryResult = task.execute(query);

		      // query result should have only 1 record per state
		      if (queryResult.featureCount() != 1) {
		        System.err.println("Error! There should be exactly 1 record per state.");
		        return null;
		      }
		      selectedState = (Feature) queryResult.iterator().next();

		      // get all attributes of the state and add them to the Map
		      for (Map.Entry<String, Object> attr : selectedState.getAttributes().entrySet()) {
		    	  stateData.put(attr.getKey(), attr.getValue());
		    	  //System.out.println(attr.getKey() + ": " + attr.getValue());
		      }

		    } catch (Exception ex) {
		      ex.printStackTrace();
		    }
		  
		  return stateData;
	  }
	  
	  public static void main(String[] args) {
		  List<String> stateNames = getStateNames();		  	
		  Map<String, Object> stateData = getStateData(stateNames.get(1));
	  }

}
