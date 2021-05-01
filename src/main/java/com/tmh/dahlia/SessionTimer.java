package com.tmh.dahlia;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Duration;


public class SessionTimer {

	private static final Logger LOGGER = LogManager.getLogger(SessionTimer.class);

	public enum EventType {
		TIMER_STARTED, TIMER_STOPPED,
		SESSION_START, SESSION_FINISH,
		HALF_START, HALF_FINISH,
		FIRST_HALF_START, FIRST_HALF_FINISH,
		SECOND_HALF_START, SECOND_HALF_FINISH,
		HALF_PERIODIC_CHECK
	}

	public interface EventListener { void work(); }

	private static final LocalTime FIRST_HALF_BEGINNING = LocalTime.of(9, 0, 0, 0);
	private static final LocalTime FIRST_HALF_ENDING = LocalTime.of(11, 30, 0, 0);
	private static final LocalTime SECOND_HALF_BEGINNING = LocalTime.of(13, 0, 0, 0);
	private static final LocalTime SECOND_HALF_ENDING = LocalTime.of(14, 45, 0, 0);
	private static final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
	private static final long PERIODIC_TIMER_DELAY = 100000;
	
	private Timer sessionEventTimer;
	private Timer periodicTimer;

	private final Map<EventType, HashSet<EventListener>> eventListeners = new HashMap<>();

	SessionTimer() {
		for (EventType event : EventType.class.getEnumConstants()) eventListeners.put(event, new HashSet<>());
	}

	public void start() {
		LOGGER.debug("Starting ...");
		sessionEventTimer = new Timer();
		sessionEventTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				halfStartTimerCb();
			}
		}, getNextHalfBeginningDuration(LocalDateTime.now()));

		triggerListenerCallbacks(EventType.TIMER_STARTED);
	}
	
	public void stop() {
		sessionEventTimer.cancel();
		if (periodicTimer != null) {
			periodicTimer.cancel();
		}
		triggerListenerCallbacks(EventType.TIMER_STOPPED);
	}

	public void addEventListener(EventType eventType, EventListener listener) {
		eventListeners.get(eventType).add(listener);
	}

	public void removeEventListener(EventType eventType, EventListener listener) {
		eventListeners.get(eventType).remove(listener);
	}

	private void triggerListenerCallbacks(EventType eventType) {
		for (EventListener listener : eventListeners.get(eventType)) {
			Thread t = new Thread(() -> listener.work(), "");
			t.start();
		};
	}

	private void halfStartTimerCb() {
		triggerListenerCallbacks(EventType.HALF_START);
		int checkFirstHalfResult = checkFirstHalf(LocalDateTime.now().toLocalTime());
		if (checkFirstHalfResult == 0) { // first half
			sessionEventTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					halfEndTimerCb();
				}
			}, Duration.between(LocalDateTime.now().toLocalTime(), FIRST_HALF_ENDING).toMillis());
			triggerListenerCallbacks(EventType.SESSION_START);
			triggerListenerCallbacks(EventType.FIRST_HALF_START);
		} else { // second half
			sessionEventTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					halfEndTimerCb();
				}
			}, Duration.between(LocalDateTime.now().toLocalTime(), SECOND_HALF_ENDING).toMillis());
			triggerListenerCallbacks(EventType.SECOND_HALF_START);
		}

		periodicTimer = new Timer();
		periodicTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				periodicTimerCb();
			}
		}, 0, PERIODIC_TIMER_DELAY);
	}

	private void halfEndTimerCb() {
		triggerListenerCallbacks(EventType.HALF_FINISH);
		if (periodicTimer != null) {
			periodicTimer.cancel();
		}

		sessionEventTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				halfStartTimerCb();
			}
		}, getNextHalfBeginningDuration(LocalDateTime.now()));

		int checkSecondHalfResult = checkSecondHalf(LocalDateTime.now().toLocalTime());
		if (checkSecondHalfResult == 1) {
			triggerListenerCallbacks(EventType.SECOND_HALF_FINISH);
			triggerListenerCallbacks(EventType.SESSION_FINISH);
		} else {
			triggerListenerCallbacks(EventType.FIRST_HALF_FINISH);
		}
	}

	private void periodicTimerCb() {
		periodicTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				periodicTimerCb();
			}
		}, PERIODIC_TIMER_DELAY);

		triggerListenerCallbacks(EventType.HALF_PERIODIC_CHECK);

	}

	private static long getNextHalfBeginningDuration(LocalDateTime dateTime) {
		// is working day
		if ((dateTime.getDayOfWeek() != DayOfWeek.SATURDAY) && (dateTime.getDayOfWeek() != DayOfWeek.SUNDAY)) {
			int checkFirstHalfResult = checkFirstHalf(dateTime.toLocalTime());

			// before the beginning of first half
			if (checkFirstHalfResult == -1) return Duration.between(dateTime.toLocalTime(), FIRST_HALF_BEGINNING).toMillis();

			if (checkFirstHalfResult == 0) return 0;

			// == 1, after the ending of first half
			int checkSecondHalfResult = checkSecondHalf(dateTime.toLocalTime());

			// break time between 2 halves
			if (checkSecondHalfResult == -1) return Duration.between(dateTime.toLocalTime(), SECOND_HALF_BEGINNING).toMillis();

			if (checkSecondHalfResult == 0) return 0;

			// == 1, after the ending of second half
			if (dateTime.getDayOfWeek() == DayOfWeek.FRIDAY)
				return (MILLIS_PER_DAY - Duration.between(LocalTime.of(0, 0, 0, 0), dateTime.toLocalTime()).toMillis()) // to midnight
					+ MILLIS_PER_DAY * 2 // SAT, SUN
					+ (Duration.between(LocalTime.of(0, 0, 0, 0), FIRST_HALF_BEGINNING).toMillis()); // from 00:00 to 9:00 MONDAY

			return (MILLIS_PER_DAY - Duration.between(LocalTime.of(0, 0, 0, 0), dateTime.toLocalTime()).toMillis()) // to midnight
				+ (Duration.between(LocalTime.of(0, 0, 0, 0), FIRST_HALF_BEGINNING).toMillis()); // from 00:00 to 9:00 the next day
		}

		// is SATURDAY
		if (dateTime.getDayOfWeek() == DayOfWeek.SATURDAY)
			return (MILLIS_PER_DAY - Duration.between(LocalTime.of(0, 0, 0, 0), dateTime.toLocalTime()).toMillis()) // to midnight
				+ MILLIS_PER_DAY // SUN
				+ (Duration.between(LocalTime.of(0, 0, 0, 0), FIRST_HALF_BEGINNING).toMillis()); // from 00:00 to 9:00 MONDAY
		
		// is SUNDAY
		return (MILLIS_PER_DAY - Duration.between(LocalTime.of(0, 0, 0, 0), dateTime.toLocalTime()).toMillis()) // to midnight
			+ (Duration.between(LocalTime.of(0, 0, 0, 0), FIRST_HALF_BEGINNING).toMillis()); // from 00:00 to 9:00 the next day
		
	}	
	
	private static int checkFirstHalf(LocalTime time) {
		return compareTime(time, FIRST_HALF_BEGINNING, FIRST_HALF_ENDING);
	}
	
	private static int checkSecondHalf(LocalTime time) {
		return compareTime(time, SECOND_HALF_BEGINNING, SECOND_HALF_ENDING);
	}
	
	private static int compareTime(LocalTime time, LocalTime begin, LocalTime end) {
		if (time.isBefore(begin)) return -1;
		if (time.isAfter(end)) return 1;
		return 0;
	}
	
}