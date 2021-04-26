package com.tmh.dahlia;
import java.time.LocalDateTime;

public class Main {
	public static void main(String[] args) {
			
	}

	private static void testSessionTimer() {
		SessionTimer st = new SessionTimer();
		st.addEventListener(SessionTimer.EventType.TIMER_STARTED, () -> {
			System.out.println("SessionMgr started");
			System.out.println(LocalDateTime.now());
		});

//			st.addEventListener(SessionTimer.EventType.TIMER_STOPPED, () -> {
//
//			});

		st.addEventListener(SessionTimer.EventType.SESSION_START, () -> {
			System.out.println("Session started");
			System.out.println(LocalDateTime.now());
		});

		st.addEventListener(SessionTimer.EventType.SESSION_FINISH, () -> {
			System.out.println("Session finished");
			System.out.println(LocalDateTime.now());
		});

		st.addEventListener(SessionTimer.EventType.FIRST_HALF_START, () -> {
			System.out.println("first half started");
			System.out.println(LocalDateTime.now());
		});

		st.addEventListener(SessionTimer.EventType.FIRST_HALF_FINISH, () -> {
			System.out.println("first half finished");
			System.out.println(LocalDateTime.now());
		});

		st.addEventListener(SessionTimer.EventType.SECOND_HALF_START, () -> {
			System.out.println("second half started");
			System.out.println(LocalDateTime.now());
		});

		st.addEventListener(SessionTimer.EventType.SECOND_HALF_FINISH, () -> {
			System.out.println("second half finished");
			System.out.println(LocalDateTime.now());
		});

		st.addEventListener(SessionTimer.EventType.HALF_PERIODIC_CHECK, () -> {
			System.out.println("periodic check");
			System.out.println(LocalDateTime.now());
		});

		st.start();
	}
	
}
