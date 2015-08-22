package data;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;









import com.esri.core.map.Feature;
import com.esri.map.GraphicsLayer;
import com.esri.map.MapEventListenerAdapter;
import com.esri.toolkit.overlays.HitTestEvent;
import com.esri.toolkit.overlays.HitTestListener;
import com.esri.toolkit.overlays.HitTestOverlay;


public class StateSelecter implements HitTestListener {

	StateMap stateMap;
	boolean initialChoice;

	public StateSelecter(StateMap stateMap)
	{
		this.stateMap = stateMap;
		this.initialChoice = true;
	}
	
	public void setInitialChoice(boolean value)
	{
		initialChoice = value;
	}

	@Override
	public void featureHit(HitTestEvent event) {

		HitTestOverlay overlay = (HitTestOverlay) event.getSource();
		List<Feature> hitFeatures = overlay.getHitFeatures();
		GraphicsLayer graphicsLayer = (GraphicsLayer) overlay.getLayer();
		//select or de-select each hit graphic
		if(initialChoice)
		{
			for (Feature feature : hitFeatures) {
				if (graphicsLayer.isGraphicSelected((int) feature.getId())) {
					graphicsLayer.unselect((int) feature.getId());
					this.stateMap.fivePicked(null);
				}
				else if(graphicsLayer.getSelectionIDs().length < 5) {
					graphicsLayer.select((int) feature.getId());
					this.stateMap.fivePicked(graphicsLayer.getSelectionIDs());
				}
			}
		}
		else
		{
			for (Feature feature : hitFeatures) {
				if(graphicsLayer.isGraphicSelected((int) feature.getId()))
				{
					graphicsLayer.clearSelection();
				}
				else
				{
					graphicsLayer.clearSelection();
					graphicsLayer.select((int) feature.getId());
					stateMap.select((int) feature.getId());
				}
			}
		}
	}
}
