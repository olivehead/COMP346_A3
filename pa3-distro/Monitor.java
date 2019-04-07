import java.util.concurrent.locks.Condition;

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

	private enum status{eating, hungry, thinking, sleeping, talking}
	private status[] state;
	private int numPhilosophers;

	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers) {
		// TODO: set appropriate number of chopsticks based on the # of philosophers
		state = new status[piNumberOfPhilosophers];
		for(int i = 0; i < piNumberOfPhilosophers; i++) {
			state[i] = status.thinking;
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
		state[piTID] = status.hungry;
		test(piTID);
		while(state[piTID] != status.eating) {
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
		state[piTID] = status.thinking;
		test((piTID + numPhilosophers - 1) % numPhilosophers);
		test((piTID + 1) % numPhilosophers);
	}

	/**
	 * Only one philopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public synchronized void requestTalk() {
		// ...
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk() {
		// ...
	}

	public synchronized void test(int i) {
		if(state[(i + numPhilosophers - 1) % numPhilosophers] != status.eating &&
		state[(i + 1) % state.length] != status.eating &&
		state[i] == status.hungry) {
			state[i] = status.eating;
			this.notifyAll();
		}
	}
}

// EOF
