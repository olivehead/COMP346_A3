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
		int i = piTID - 1;
		state[i] = States.HUNGRY;
		test(i, States.EATING);
		if(state[i] != States.EATING) {
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
		int i = piTID - 1;
		state[i] = States.THINKING;
//		test((i - 1) % state.length);
//		test((i + 1) % state.length);
	}

	/**
	 * Only one philopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public synchronized void requestTalk(final int piTID) {
		int i = piTID - 1;
		state[i] = States.WANTTOTALK;
		test(i, States.TALKING);
		if(state[i] != States.TALKING) {
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
	public synchronized void endTalk() {
		// ...
	}

	public synchronized void test(int i, States aState) {
		if(aState == States.EATING) {
			if (state[(i - 1) % state.length] != States.EATING &&
					state[(i + 1) % state.length] != States.EATING &&
					state[i] == States.HUNGRY) {
				state[i] = States.EATING;
				this.notifyAll();
			}
		}
		else if (aState == States.PHILSLEEPING) {
			for	(int x=0; x<state.length; x++) {
				if	(state[x] != States.TALKING &&
						state[x] != States.WANTTOTALK &&
						state[i] == States.SLEEPY) {
					state[i] = States.PHILSLEEPING;
					this.notifyAll();
				}
			}
		}
		else if (aState == States.TALKING) {
			for (int x=0; x<state.length; x++) {
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
