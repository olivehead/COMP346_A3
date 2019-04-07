import java.awt.font.TextHitInfo;

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
		test(piTID);
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
		test((piTID + numPhilosophers - 1) % numPhilosophers);
		test((piTID + 1) % numPhilosophers);
	}

	/**
	 * Only one philopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public synchronized void requestTalk(int piTID) {
		state[piTID] = States.WANTTOTALK;
		test(piTID);
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
			test(i);
		}
	}

	private synchronized void test(int i) {
		if(state[(i + numPhilosophers - 1) % numPhilosophers] != States.EATING &&
		state[(i + 1) % state.length] != States.EATING &&
		state[i] == States.HUNGRY) {
			state[i] = States.EATING;
			this.notifyAll();
		}
	}
}

// EOF
