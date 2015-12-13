package aletrainsystem.models.railroad;

import java.util.ArrayList;

import aletrainsystem.models.TrainId;
import aletrainsystem.models.locking.Lockable;
import aletrainsystem.models.navigation.RouteElement;
import aletrainsystem.pointswitch.PointConnector;

public abstract class RailLeg extends RouteElement implements Lockable {
	
	protected ArrayList<RailBrick> railBricks;
	private TrainId lockedBy = null;
	private TrainId reservedBy = null;
	private RailLegId id = null;
	protected RailLeg() {
		railBricks = new ArrayList<>();
	}
	
	public abstract RailLegId id();
	
	public int length(){
		return railBricks.size();
	}
	
	public int getSleepersCount(){
		int sleepers = 0;
		for (RailBrick railBrick : railBricks) {
			sleepers += railBrick.sleepers();
		}
		
		return sleepers;
	}
	
	public void addRailBrick(RailBrick railBrick) {
		railBricks.add(railBrick);
	}
	
	public abstract RailComponent getNextComponent(RailComponent previous, PointConnector direction);
	
	public abstract PointConnector getOppositeConnector(PointConnector connector);
	
	@Override
	public synchronized TrainId checkLock() {
		return lockedBy;
	}
		
	@Override
	public synchronized TrainId checkReservation() {
		return reservedBy;
	}
	
	@Override
	public synchronized void reserveLock(TrainId owner) {
		if (reservedBy == null)
			reservedBy = owner;
	}
	
	@Override
	public synchronized void releaseReservation() {
		reservedBy = null;		
	}
	
	@Override
	public synchronized void performLock(TrainId owner) {
		if (reservedBy == null || reservedBy.equals(owner))
			lockedBy = owner;		
	}
	
	@Override
	public synchronized void unLock() {
		reservedBy = null;
		lockedBy = null;
	}
	
	@Override
	public Lockable getLockableResource() {
		return this;
	}
	
	public String toString() {
		return id().toString();
	}
}
