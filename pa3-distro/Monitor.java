/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
class Monitor
{
	/*
	 * ------------
	 * Data members
	 * ------------
	 */
	public enum States {THINKING, SLEEPY, PHILSLEEPING, HUNGRY, EATING, WANTTOTALK, TALKING, AVAILABLE, TAKEN}
	private States[] state;
	private States[] shakers;
	private int numPhilosophers;

	/**
	 * Constructor
	 */
    Monitor(int piNumberOfPhilosophers)
	{
		// TODO: set appropriate number of chopsticks based on the # of philosopher
		state = new States[piNumberOfPhilosophers];
		shakers = new States[2];
		shakers[0] = States.AVAILABLE;
		shakers[1] = States.AVAILABLE;
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
	synchronized void pickUp(final int piTID) {
		state[piTID] = States.HUNGRY;
		test(piTID, States.EATING);
		while(state[piTID] != States.EATING) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * When a given philosopher's done eating, they put the chopsticks/forks down
	 * and let others know they are available.
	 */
	synchronized void putDown(final int piTID) {
		state[piTID] = States.THINKING;
		test((piTID + numPhilosophers - 1) % numPhilosophers, States.EATING);
		test((piTID + 1) % numPhilosophers, States.EATING);
	}

	/**
	 * Only one philosopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	synchronized void requestTalk(int piTID) {
		state[piTID] = States.WANTTOTALK;
		test(piTID, States.TALKING);
		while(state[piTID] != States.TALKING) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	synchronized void endTalk(int piTID) {
		state[piTID] = States.THINKING;
		for(int i = 0; i < numPhilosophers; i++) {
			test(i, States.TALKING);
		}
	}

	synchronized void startSleep(int piTID) {
		state[piTID] = States.SLEEPY;
		test(piTID, States.PHILSLEEPING);
		while(state[piTID] != States.PHILSLEEPING) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	synchronized void endSleep(int piTID) {
		state[piTID] = States.THINKING;
		for(int i = 0; i < numPhilosophers; i++) {
			test(i, States.PHILSLEEPING);
		}
	}

	synchronized void requestShaker() {
		while (shakers[0] != States.AVAILABLE
				&& shakers[1] != States.AVAILABLE) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.err.println("Monitor.takeShaker():");
				DiningPhilosophers.reportException(e);
				System.exit(1);
			}
		}
		if (shakers[0] == States.AVAILABLE) {
			shakers[0] = States.TAKEN;
		} else {
			shakers[1] = States.TAKEN;
		}
	}

	synchronized void endShaker() {
		if (shakers[0] == States.TAKEN) {
			shakers[0] = States.AVAILABLE;
		} else if (shakers[1] == States.TAKEN) {
			shakers[1] = States.AVAILABLE;
		} else {
			System.err.println("Monitor.returnShaker(): something went wrong!");
		}

		notifyAll();
    }

	//IMPLEMENT RETURNSHAKER

	private synchronized void test(int i, States aState) {
		if(aState == States.EATING) {
			int prevPhilosopher = (i + numPhilosophers - 1) % numPhilosophers;
			int nextPhilosopher = (i + 1) % numPhilosophers;
			if (state[prevPhilosopher] != States.EATING &&
					state[nextPhilosopher] != States.EATING &&
                    //state[prevPhilosopher] != States.SHAKING &&
					//state[nextPhilosopher] != States.SHAKING &&
					state[i] == States.HUNGRY) {
				if ((state[prevPhilosopher] != States.HUNGRY || i > prevPhilosopher) &&
				     (state[nextPhilosopher] != States.HUNGRY || i > nextPhilosopher)) {
					state[i] = States.EATING;
					notifyAll();
				}
			}
		}
		else if (aState == States.PHILSLEEPING) {
			for	(int x=0; x<numPhilosophers; x++) {
				if	(state[x] != States.TALKING &&
						state[x] != States.WANTTOTALK &&
						state[i] == States.SLEEPY) {
					state[i] = States.PHILSLEEPING;
					notifyAll();
				}
			}
		}
		else if (aState == States.TALKING) {
			for (int x=0; x<numPhilosophers; x++) {
				if (state[x] != States.PHILSLEEPING &&
						state[x] != States.TALKING &&
						state[i] == States.WANTTOTALK) {
					state[i] = States.TALKING;
					notifyAll();
				}
			}
		}
	}
}

// EOF
