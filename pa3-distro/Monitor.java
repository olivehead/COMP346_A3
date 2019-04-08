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
	public enum States {THINKING, SLEEPY, PHILSLEEPING, HUNGRY, EATING, WANTTOTALK, TALKING, REQUESTSHAKER, SHAKING}
	private States[] state;
//	public int chopsticks;
    private int numPhilosophers;
    private static int shakerCounter;
    private final static int MAX_SHAKER_NUMBER = 2;

	/**
	 * Constructor
	 */
    Monitor(int piNumberOfPhilosophers)
	{
		// TODO: set appropriate number of chopsticks based on the # of philosopher
		state = new States[piNumberOfPhilosophers];
		for(int i=0; i<piNumberOfPhilosophers; i++) {
			state[i] = States.THINKING;
		}
		numPhilosophers = piNumberOfPhilosophers;
		shakerCounter = 0;
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

	public synchronized void requestShaker(int piTID) {
        if(state[piTID] == States.EATING) {
            state[piTID] = States.REQUESTSHAKER;
            test(piTID, States.SHAKING);
            while(state[piTID] != States.SHAKING) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
	}

	public synchronized void endShaker(int piTID) {
	    state[piTID] = States.THINKING;
	    shakerCounter--;
        for(int i = 0; i < numPhilosophers; i++) {
            test(i, States.REQUESTSHAKER);
        }
    }

	//IMPLEMENT RETURNSHAKER

	private synchronized void test(int i, States aState) {
		if(aState == States.EATING) {
			int prevPhilosopher = (i + numPhilosophers - 1) % numPhilosophers;
			int nextPhilosopher = (i + 1) % numPhilosophers;
			if (state[prevPhilosopher] != States.EATING &&
					state[nextPhilosopher] != States.EATING &&
                    state[prevPhilosopher] != States.SHAKING &&
                    state[nextPhilosopher] != States.SHAKING &&
					state[i] == States.HUNGRY) {
				//if ((state[prevPhilosopher] != States.HUNGRY && i < prevPhilosopher) ||
                //        (state[nextPhilosopher] != States.HUNGRY && i < nextPhilosopher)) {
					state[i] = States.EATING;
					notifyAll();
				//}
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
		else if(aState == States.SHAKING) {
		    if(state[i] == States.REQUESTSHAKER && shakerCounter < MAX_SHAKER_NUMBER) {
		        shakerCounter++;
		        state[i] = States.SHAKING;
		        notifyAll();
            }
        }
	}
}

// EOF
