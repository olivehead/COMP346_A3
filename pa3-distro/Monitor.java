import java.awt.font.TextHitInfo;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor
{
	/*
	 * ------------
	 * Data members
	 * ------------
	 */
	public enum States {THINKING, SLEEPY, PHILSLEEPING, HUNGRY, EATING, WANTTOTALK, TALKING};
	public States[] state;
//	public int chopsticks;
    int numPhilosophers;

	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers)
	{
		// TODO: set appropriate number of chopsticks based on the # of philosopher
		state = new States[piNumberOfPhilosophers];
		for(int i=0; i<piNumberOfPhilosophers; i++) {
			state[i] = States.THINKING;
		}
		numPhilosophers = piNumberOfPhilosophers;
	}

	/*
	 * -------------------------------
	 * User-defined monitor procedures
	 * -------------------------------
	 */

	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public synchronized void pickUp(final int piTID) {
		state[piTID] = States.HUNGRY;
		test(piTID, States.EATING);
		while(state[piTID] != States.EATING) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int piTID) {
		state[piTID] = States.THINKING;
		test((piTID + numPhilosophers - 1) % numPhilosophers, States.EATING);
		test((piTID + 1) % numPhilosophers, States.EATING);
	}

	/**
	 * Only one philopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public synchronized void requestTalk(int piTID) {
		state[piTID] = States.WANTTOTALK;
		test(piTID, States.TALKING);
		while(state[piTID] != States.TALKING) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk(int piTID) {
		state[piTID] = States.THINKING;
		for(int i = 0; i < numPhilosophers; i++) {
			test(i, States.TALKING);
		}
	}

	public synchronized void startSleep(int piTID) {
		state[piTID] = States.SLEEPY;
		test(piTID, States.PHILSLEEPING);
		while(state[piTID] != States.PHILSLEEPING) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void endSleep(int piTID) {
		state[piTID] = States.THINKING;
		for(int i = 0; i < numPhilosophers; i++) {
			test(i, States.PHILSLEEPING);
		}
	}

	public synchronized void test(int i, States aState) {
		if(aState == States.EATING) {
			if (state[(i + numPhilosophers - 1) % numPhilosophers] != States.EATING &&
					state[(i + 1) % numPhilosophers] != States.EATING &&
					state[i] == States.HUNGRY) {
				state[i] = States.EATING;
				this.notifyAll();
			}
		}
		else if (aState == States.PHILSLEEPING) {
			for	(int x=0; x<numPhilosophers; x++) {
				if	(state[x] != States.TALKING &&
						state[x] != States.WANTTOTALK &&
						state[i] == States.SLEEPY) {
					state[i] = States.PHILSLEEPING;
					this.notifyAll();
				}
			}
		}
		else if (aState == States.TALKING) {
			for (int x=0; x<numPhilosophers; x++) {
				if (state[x] != States.PHILSLEEPING &&
						state[x] != States.TALKING &&
						state[i] == States.WANTTOTALK) {
					state[i] = States.TALKING;
					this.notifyAll();
				}
			}

		}
	}
}

// EOF
