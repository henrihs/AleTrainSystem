package aletrainsystem.models.railroad;

public class RailroadBuilderTests {
	public static void main(String[] args) {
		RailroadBuilder builder = new RailroadBuilder("resources/maps/map.bbm");
		Railroad railroad = builder.getRailroad();
		
		boolean station = railroad.isStation("lol");
		System.out.println(station);
	}
}
