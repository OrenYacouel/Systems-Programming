package bgu.spl.mics;

import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.Model;

import java.util.List;


import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static class SingletonHolder{
		private static MessageBusImpl instance = new MessageBusImpl();
	}

//	registration
	private ConcurrentHashMap<MicroService, BlockingQueue<Message>> serviceToQueueMap;
	private ConcurrentHashMap<MicroService, ArrayList<Class<? extends Message>>> serviceToMessageTypesMap;

//	sending
	private ConcurrentHashMap<Class<? extends Event>, ArrayList<MicroService>> eventSubscribers;
	private ConcurrentHashMap<Class<? extends Broadcast>, ArrayList<MicroService>> broadcastSubscribers;

//	future
	private ConcurrentHashMap<Event, Future> eventToFutureMap;

//	locks
	Object serviceQueueLock, serviceMessageTypeLock, eventSubscribersLock, broadcastSubscribersLock, eventFutureLock;

	private  MessageBusImpl(){
		serviceToQueueMap = new ConcurrentHashMap <>();
		serviceToMessageTypesMap = new ConcurrentHashMap <>();
		eventSubscribers = new ConcurrentHashMap <>();
		broadcastSubscribers =new ConcurrentHashMap <>();
		eventToFutureMap = new ConcurrentHashMap<>();

		serviceQueueLock = new Object();
		serviceMessageTypeLock = new Object();
		eventSubscribersLock = new Object();
		broadcastSubscribersLock = new Object();
		eventFutureLock = new Object();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static MessageBusImpl getInstance(){
		return SingletonHolder.instance;
	}


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
//		synchronized (eventSubscribersLock) {
			if (!this.isRegistered(m)) {
				return; //if the MS is not registered it cannot subscribe to events
			}
			if( !eventSubscribers.containsKey(type)){ //checks if there are MS which are subscribed to this type of event
				eventSubscribers.put(type, new ArrayList<MicroService>()); //in this case add new list
			}
			eventSubscribers.get(type).add(m);
		//}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
//		synchronized (broadcastSubscribersLock){
			if(!this.isRegistered(m)) { //if the MS is not registered it cannot subscribe to broadcasts
				return;
			}
			if(!broadcastSubscribers.containsKey(type)){ //checks if there are MS which are subscribed to this type of broadcast
				broadcastSubscribers.put(type, new ArrayList<MicroService>()); //in this case add new list
			}
			broadcastSubscribers.get(type).add(m);
		//}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// we should take the future from the map and resolve it
		//
//		synchronized (eventToFutureMap){
			Future <T> future = eventToFutureMap.get(e);
			future.resolve(result);
		//}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if( b instanceof TerminateBroadcast){
//			List<MicroService> l = new ArrayList<MicroService>(serviceToMessageTypesMap.keySet());
			List<MicroService> l = new ArrayList<MicroService>(serviceToQueueMap.keySet());
			//TODO: or changed it at 16:00
			for( MicroService m : l ){
				m.terminate();
			}
		}
		List<MicroService> subscribed;
//		synchronized (broadcastSubscribersLock) {
			if (broadcastSubscribers.containsKey(b.getClass()) && !broadcastSubscribers.get(b.getClass()).isEmpty()) { //checks there is a MS which subscribed to the event
				subscribed = broadcastSubscribers.get(b.getClass()); //the list of the subscribed MS
				synchronized (serviceQueueLock) {
					for (MicroService m : subscribed) {
						serviceToQueueMap.get(m).add(b); //adds to the queue of each MS the broadcast
					}
					serviceQueueLock.notifyAll();
				//}
				}
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {

		List<MicroService> subscribed;
		Future<T> future = new Future<T>();
//		synchronized (eventSubscribersLock) {
			if (eventSubscribers.containsKey(e.getClass()) && !eventSubscribers.get(e.getClass()).isEmpty()) {
				subscribed = eventSubscribers.get(e.getClass()); //the list of the subscribed MS
				MicroService m = subscribed.get(0); //the MS which is first in line
//				synchronized (serviceToQueueMap) {
				if(serviceToQueueMap.get(m)!= null ) //checks the queue of the MS exists
					serviceToQueueMap.get(m).add(e); //adds to the first MS the event to the queue
//					serviceQueueLock.notifyAll();
//					synchronized (eventFutureLock) {
						eventToFutureMap.put(e, future); //adds the future to the futures map
					//}
				//}
				eventSubscribers.get(e.getClass()).remove(0); //removes the MS which got the event from the round-robin
				eventSubscribers.get(e.getClass()).add(m); //adds the MS which got the event to the end of the round-robin
			}
		//}
		return future;
	}

	@Override
	public void register(MicroService m) {
//		synchronized (serviceQueueLock){
			serviceToQueueMap.put(m, new LinkedBlockingQueue<>());
		//}
	}

	@Override
	public void unregister(MicroService m) {
//		synchronized (eventSubscribersLock){
			for ( List l : eventSubscribers.values()) //deletes the MS from each event he subscribed to
				l.remove(m);
		//}
//		synchronized (broadcastSubscribersLock){
			for( List l : broadcastSubscribers.values()){ //deletes the MS from each broadcast he subscribed to
				l.remove(m);
			}
		//}
//		synchronized ( serviceQueueLock ){ //deletes the queue of the MS
			serviceToQueueMap.remove(m);
		//}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		BlockingQueue<Message> queue = null;
		synchronized (this){
			if( m == null || !this.isRegistered(m)){
				System.out.println(m.getName() + " sent exception");
				throw new IllegalArgumentException(); //TODO here it throws the illegal argument exception but its doesnt stop the program
			}

			queue = this.serviceToQueueMap.get(m);
		}

		return queue.take();

//		Message firstMsg = null;
//		try{
//			firstMsg = serviceToQueueMap.get(m).take();
//		} catch (InterruptedException e){}
//		return firstMsg;

//		synchronized (serviceQueueLock){ //TODO i think we should delete all the synchronized cuz we used the concurrent HashMap
//			synchronized(m) {
//				while (serviceToQueueMap.get(m).isEmpty()) {
//					m.wait();
//				}
//				return serviceToQueueMap.get(m).remove();
//			}
		}
//	}


	public boolean isRegistered( MicroService m) {
		return (serviceToQueueMap.get(m) != null);
	}

	public boolean isSubscribedEvent(Class<? extends Event> type, MicroService m){
		return (isRegistered(m)&& serviceToMessageTypesMap.get(m).contains(type)&& eventSubscribers.containsKey(type));
	}

	public boolean isSubscribedBroadcast(Class<? extends Broadcast> broadcast, MicroService m) {
		return (isRegistered(m)&& serviceToMessageTypesMap.get(m).contains(broadcast.getClass())&& broadcastSubscribers.containsKey(broadcast.getClass()));
	}

	public void setEventToFutureMap(Model model){
		List<Event> eventList = new ArrayList<Event>(eventToFutureMap.keySet());
		int i=0;
		while( i < eventList.size() ){
			if( eventList.get(i) instanceof TrainModelEvent && ((TrainModelEvent) eventList.get(i)).getModel() == model){
				complete(eventList.get(i), model);
			}
		i++;
		}
	}

//	GETTERS

	public ConcurrentHashMap<Event, Future> getEventToFutureMap() {
		return eventToFutureMap;
	}
}
