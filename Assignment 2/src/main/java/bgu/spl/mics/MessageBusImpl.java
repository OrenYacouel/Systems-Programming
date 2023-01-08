package bgu.spl.mics;

import bgu.spl.mics.example.messages.ExampleBroadcast;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private final ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> ms_MessageQueues;
	private final ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> ms_EventMap;
	private final ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> ms_BroadcastMap;
	private final ConcurrentHashMap<Event<?>, Future> futureMap;
	private final Object roundRobinLock = new Object();

	private MessageBusImpl() {
		ms_MessageQueues = new ConcurrentHashMap<>();
		ms_EventMap = new ConcurrentHashMap<>();
		ms_BroadcastMap = new ConcurrentHashMap<>();
		futureMap = new ConcurrentHashMap<>();
	}

	// Initialization on demand holder
	private static class MessageBusHolder {
		private static final MessageBusImpl instance = new MessageBusImpl();
	}

	// Singleton implementation
	public static MessageBus getInstance() {
		return MessageBusHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if(!ms_MessageQueues.containsKey(m))
			throw new IllegalArgumentException("Microservice" + m.getName()+" is not registered");
		synchronized (ms_EventMap){ // Prevent duplicate puts of same event type
			if(!this.ms_EventMap.containsKey(type))
				this.ms_EventMap.put(type, new ConcurrentLinkedQueue<>());
		}
		this.ms_EventMap.get(type).add(m);

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (!ms_MessageQueues.containsKey(m))
			throw new IllegalArgumentException("Microservice " + m.getName() + " is not registered");
		synchronized (ms_BroadcastMap) { // Prevent duplicate puts of same event type
			if (!this.ms_BroadcastMap.containsKey(type))
				this.ms_BroadcastMap.put(type, new ConcurrentLinkedQueue<>());
			this.ms_BroadcastMap.get(type).add(m);
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		if(futureMap.containsKey(e))
			futureMap.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if(ms_BroadcastMap.get(b.getClass()) != null){
			for(MicroService ms : ms_BroadcastMap.get(b.getClass())){
				ms_MessageQueues.get(ms).add(b);
			}
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> future = null;
		synchronized (roundRobinLock){
			//Ensure round robin performed by correct order.  won't allow additional ms insertion
			ConcurrentLinkedQueue<MicroService> eQueue = ms_EventMap.get(e.getClass());
			if(eQueue != null && !eQueue.isEmpty()){
				MicroService nextMS = eQueue.poll();
				if(nextMS != null && !nextMS.hasBeenTerminated()){
					future = new Future<>();
					futureMap.put(e, future);
					eQueue.add(nextMS); // return microservice to back of RoundRobin
					ms_MessageQueues.get(nextMS).add(e); //
				}
			}
		}
		return future;
	}

	@Override
	public void register(MicroService m) {
		ms_MessageQueues.putIfAbsent(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		for(Class<? extends Event> e: ms_EventMap.keySet())
			ms_EventMap.get(e).remove(m);

		for(Class<? extends Broadcast> b : ms_BroadcastMap.keySet())
			ms_BroadcastMap.get(b).remove(m);
		ms_MessageQueues.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if(!ms_MessageQueues.containsKey(m))
			throw new IllegalArgumentException("Microservice "+ m.getName()+" is not registered");
		try{
			//blocking function
			return ms_MessageQueues.get(m).take();
		}
		catch (InterruptedException e){
			throw e;
		}
	}

	public boolean isMSSubscribedToBroadcast(MicroService microService, Class<? extends ExampleBroadcast> broadcast_class) {
		return ms_BroadcastMap.get(microService).contains(broadcast_class);
	}

	public boolean isMSSubscribedToEvent(MicroService microService, Class<? extends Event> event_class){
		return ms_EventMap.get(microService).contains(event_class);
	}

	public boolean isMicroServiceRegistered (MicroService microService){
		return ms_MessageQueues.containsKey(microService);
	}
	public ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> getMs_MessageQueues() {
		return ms_MessageQueues;
	}

	public ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> getMs_EventMap() {
		return ms_EventMap;
	}

	public ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> getMs_BroadcastMap() {
		return ms_BroadcastMap;
	}

	public ConcurrentHashMap<Event<?>, Future> getFutureMap() {
		return futureMap;
	}
}
