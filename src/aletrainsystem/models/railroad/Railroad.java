package aletrainsystem.models.railroad;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import aletrainsystem.enums.PointSwitchConnectorEnum;
import aletrainsystem.models.PointSwitchId;

public class Railroad {
	private Map<Long, PointSwitch> pointSwitches;
	private Map<String, RailLeg> railLegs;
	private Set<PointSwitchConnector> connectedPointSwitchConnector;
	private PointSwitchConnector railSystemEntryPoint;
	private byte[] md5sum;
	
	protected Railroad() {
		pointSwitches = new HashMap<>();
		railLegs = new HashMap<>();
		connectedPointSwitchConnector = new HashSet<>();
	}
	
	public byte[] getMd5sum() {
		return md5sum;
	}
	
	protected void setMd5sum(byte[] md5sum) {
		this.md5sum = md5sum;
	}
	
	Map<String, RailLeg> getRailLegs() {
		return railLegs;
	}
	
	Map<Long, PointSwitch> getPointSwitches() {
		return pointSwitches;
	}
	
	protected void addPointSwitch(PointSwitch pointSwitch) {
		pointSwitches.put(pointSwitch.getId().value(), pointSwitch);
	}
	
	protected void addRailLeg(RailLeg railLeg){
		railLegs.put(railLeg.getId().value(), railLeg);
		railLeg.getConnectors().forEach(c -> connectedPointSwitchConnector.add(c));
	}
	
	protected void setRailSystemEntryPoint(PointSwitchConnector railSystemEntryPoint) {
		this.railSystemEntryPoint = railSystemEntryPoint;
	}
	
	public PointSwitchConnector getRailSystemEntryPoint() {
		return railSystemEntryPoint;
	}
	
	public boolean isStation(RailLegId railLegId) {
		return isStation(railLegId.value());
	}
	
	public boolean isStation(String railLegId) {
		return isStation(findRailLeg(railLegId));
	}
		
	public boolean isStation(RailLeg railLeg){
		if (railLeg == null || !railLegs.containsKey(railLeg.getId().value())){
			return false;
		}
		
		ConnectorPair connectors = railLeg.getConnectors();
		if (connectors.bothOfType(PointSwitchConnectorEnum.DIVERT)){
			RailLegId parallelRailLegId = new RailLegId(
					connectors.first().getPointSwitch().getConnector(PointSwitchConnectorEnum.THROUGH), 
					connectors.second().getPointSwitch().getConnector(PointSwitchConnectorEnum.THROUGH));
			if (findRailLeg(parallelRailLegId) != null){
				return true;
			}
		}
		
		return false;
	}
	
	public RailLeg findRailLeg(String railLegId) {
		return railLegs.get(railLegId);
	}
	
	public RailLeg findRailLeg(RailLegId railLegId){
		return findRailLeg(railLegId.value());
	}
	
	protected boolean hasRailLegWithConnector(PointSwitchConnector connector) {
		return connectedPointSwitchConnector.contains(connector);
	}
	
	public PointSwitch findPointSwitch(PointSwitchId pointSwitchId) {
		return pointSwitches.get(pointSwitchId.value());
	}
	
	public PointSwitch findOrAddPointSwitch(long pointSwitchId) {
		PointSwitch result = pointSwitches.get(pointSwitchId);
		if (result == null) {
			result = new PointSwitch(new PointSwitchId(pointSwitchId));
			pointSwitches.put(pointSwitchId, result);
		}
		return result;
	}
}
