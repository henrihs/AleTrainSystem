package aletrainsystem.models.railroad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import bluebrick4j.conversion.BbmParser;
import bluebrick4j.model.Brick;
import bluebrick4j.model.BrickType;
import bluebrick4j.model.Connexion;
import bluebrick4j.model.Layer;
import bluebrick4j.model.Map;

public class RailroadBuilder {

	public final Logger logger = LoggerFactory.getLogger(this.getClass());

	private Railroad railroad;
	private HashMap<Connexion, Brick> connexionToBrickMapping;
	private List<Brick> bricks;
	private HashSet<Object> visited;
	private String bbmFilePath;

	public RailroadBuilder(String bbmFilePath){
		railroad = new Railroad();
		this.bbmFilePath = bbmFilePath;
	}
	
	public Railroad getRailroad() {
		convertFromBbmFile();
		return railroad;
	}

	private void convertFromBbmFile() {
		Map map = BbmParser.loadMapFromFile(bbmFilePath);
		bricks = new ArrayList<>();

		for (Layer layer : map.getLayers().getLayers()) {
			layer.getBricks().getBricks().forEach((b) -> bricks.add(b));
		}

		connexionToBrickMapping = ConnectionToBrickMapping(bricks);

		while (!bricks.isEmpty()){
			Brick brick = bricks.get(0);
			BrickType type = brick.getBrickType();
			if (visited.contains(brick) || type == BrickType.CURVED || type == BrickType.STRAIGHT) { 
				continue; 
			}
			
			PointSwitch startPoint = railroad.findOrAddPointSwitch(brick.getId());
			List<Connexion> connections = brick.getConnexions().getConnexions();
			for (int i = 0; i < 3; i++) {
				RailLeg nextLeg = new RailLeg(startPoint.getConnector(ConnectorConverter.convert(type).apply(i)));
				Connexion nextConnection = connections.get(i);
				if (!visited.contains(nextConnection)) {
					RailLeg fullLeg = stepInto(nextConnection, nextLeg);
					railroad.addRailLeg(fullLeg);
				}
			}
			
			bricks.remove(brick);
		}
	}

	private RailLeg stepInto(Connexion connexion, RailLeg currentLeg) {
		visited.add(connexion);
		Brick brick = connexionToBrickMapping.get(connexion);
		bricks.remove(brick);
		
		if (brick.getBrickType() == BrickType.STRAIGHT
				|| brick.getBrickType() == BrickType.CURVED) {
			currentLeg.setLenght(currentLeg.getLenght() + 1);
			bricks.remove(brick);
			
			for (Connexion nextConnexion : brick.getConnexions().getConnexions()) {
				if (nextConnexion != connexion) {
					return stepInto(nextConnexion.getLinkedTo(), currentLeg);
				}
			}
		}

		PointSwitch endOfLeg = railroad.findOrAddPointSwitch(brick.getId());
		int index = brick.getConnexions().getConnexions().indexOf(connexion);
		PointSwitchConnector endConnector = endOfLeg.getConnector(ConnectorConverter.convert(brick.getBrickType()).apply(index));

		return new RailLeg(currentLeg.getConnectors().first(), endConnector, currentLeg.getLenght());
	}

	private HashMap<Connexion, Brick> ConnectionToBrickMapping(List<Brick> bricks) {
		HashMap<Connexion, Brick> map = new HashMap<>();
		for (Brick brick : bricks) {
			for (Connexion connection : brick.getConnexions().getConnexions()) {
				map.put(connection, brick);
			}
		}

		return map;
	}

//	private Brick getNextConnectedBrick(Brick currentBrick, Brick previousBrick) {
//		for (Connexion connexion : currentBrick.getConnexions().getConnexions()) {
//			if (previousBrick.getConnexions().getConnexions().contains(connexion.getLinkedTo())) {
//				continue;
//			}
//			else
//				return connexionToBrickMapping.get(connexion.getLinkedTo());
//		}
//		return null;
//	}


}