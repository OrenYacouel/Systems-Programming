package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	private final long speed;
	private final long duration;
	private AtomicInteger currentTime;
	private Timer timer;
	private final TimerTask timerTask;

	public TimeService(long _tickTime, long _duration) {
		super("TimeService");
		this.speed = _tickTime;
		this.duration = _duration;
		this.currentTime = new AtomicInteger(1);
		this.timer = new Timer(true);
		this.timerTask = new TimerTask() {
			@Override
			public void run() {
				sendBroadcast(new TickBroadcast());
				if(currentTime.incrementAndGet() >= duration){
					sendBroadcast(new TerminateBroadcast());
					timer.cancel();
				}
			}
		};
	}

	@Override
	protected void initialize() {
		timer.schedule(timerTask, 0, speed);
	}
}