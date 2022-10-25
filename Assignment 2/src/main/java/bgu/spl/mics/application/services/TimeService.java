package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 0
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private int ticksPassed;
	private int numMsPerTick;
	private int numTicksBeforeTerminate;
	private boolean wantToTerminate;
	//we will use the MB hashMap in order to see who are the relevant MS

	public TimeService(int _numMsPerTick, int _numTicksBeforeTerminate) {
		super("TimeService");
		numMsPerTick = _numMsPerTick;
		numTicksBeforeTerminate = _numTicksBeforeTerminate;
		ticksPassed = 0;
		wantToTerminate = false;
	}

	@Override
	protected void initialize() {
		while (!wantToTerminate){ //we reached the limit of ticks before termination
			if (ticksPassed == numTicksBeforeTerminate) { //maybe numTicksBeforeTerminate - 1
				wantToTerminate = true;
				System.out.println("about to send terminate");
				sendBroadcast(new TerminateBroadcast()); //send terminate broadcast in order to terminate the system
				//TODO: or added this at : 16:02
				this.terminate();
			}
			else {
				sendBroadcast(new TickBroadcast(ticksPassed + 1)); //send new tick
				ticksPassed ++;
				synchronized (this) {
					try {
						wait(numMsPerTick); //sleeps for param(numMsPerTick) milliseconds
					} catch (InterruptedException e) {}
				}
			}
		}
	}

}
