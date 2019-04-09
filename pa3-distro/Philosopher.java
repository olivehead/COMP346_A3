import common.BaseThread;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Class Philosopher.
 * Outlines main subroutines of our virtual philosopher.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Philosopher extends BaseThread {
	/**
	 * Max time an action can take (in milliseconds)
	 */
	private static final long TIME_TO_WASTE = 1000;
	private Random r = new Random();

	private void philSleep() {
		try {
			System.out.println("Philosopher " + this.getTID() + " has started sleeping.");
			yield();
			sleep((long)(Math.random() * TIME_TO_WASTE)); // define variable TIME_TO_WASTE
			yield();
			System.out.println("Philosopher " + this.getTID() + " has finished sleeping.");
		}
		catch(InterruptedException e) {
			System.err.println("Philosopher.sleep():");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
	}

	/**
	 * The act of eating.
	 * - Print the fact that a given phil (their TID) has started eating.
	 * - yield
	 * - Then sleep() for a random interval.
	 * - yield
	 * - The print that they are done eating.
	 */
	private void eat() {
		try {
			System.out.println("Philosopher " + this.getTID() + " has started eating.");
			yield();
			sleep((long)(Math.random() * TIME_TO_WASTE)); // define variable TIME_TO_WASTE
			yield();
			System.out.println("Philosopher " + this.getTID() + " has finished eating.");
		}
		catch(InterruptedException e) {
			System.err.println("Philosopher.eat():");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
	}

	/**
	 * The act of thinking.
	 * - Print the fact that a given phil (their TID) has started thinking.
	 * - yield
	 * - Then sleep() for a random interval.
	 * - yield
	 * - The print that they are done thinking.
	 */
	private void think() {
		try {
			System.out.println("Philosopher " + this.getTID() + " has started thinking.");
			yield();
			sleep((long)(Math.random() * TIME_TO_WASTE)); // define variable TIME_TO_WASTE
			yield();
			System.out.println("Philosopher " + this.getTID() + " has finished thinking.");
		}
		catch(InterruptedException e) {
			System.err.println("Philosopher.think():");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
	}

	/**
	 * The act of talking.
	 * - Print the fact that a given phil (their TID) has started talking.
	 * - yield
	 * - Say something brilliant at random
	 * - yield
	 * - The print that they are done talking.
	 */
	private void talk() {
		System.out.println("Philosopher " + this.getTID() + " has started talking.");
		yield();
		saySomething();
		yield();
		System.out.println("Philosopher " + this.getTID() + " has finished talking.");
	}

	private void shake() {
		try {
			System.out.println("Philosopher " + this.getTID() + " has started using the pepper shaker.");
			yield();
			sleep((long)(Math.random() * TIME_TO_WASTE)); // define variable TIME_TO_WASTE
			yield();
			System.out.println("Philosopher " + this.getTID() + " has finished using the pepper shaker.");
		}
		catch(InterruptedException e) {
			System.err.println("Philosopher.think():");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
	}

	/**
	 * No, this is not the act of running, just the overridden Thread.run()
	 */
	public void run() {
		for(int i = 0; i < DiningPhilosophers.DINING_STEPS; i++) {
			DiningPhilosophers.soMonitor.pickUp(getTID() - 1);
			DiningPhilosophers.soMonitor.requestShaker(getTID() - 1);
			shake();
			DiningPhilosophers.soMonitor.endShaker(getTID() - 1);
			eat();
			DiningPhilosophers.soMonitor.putDown(getTID() - 1);
			think();

			/*
			 * TODO:
			 * A decision is made at random whether this particular
			 * philosopher is about to say something terribly useful.
			 */
			int n = r.nextInt(10);
			if(n == 4) {
				DiningPhilosophers.soMonitor.requestTalk(getTID() - 1);
				talk();
				DiningPhilosophers.soMonitor.endTalk(getTID() - 1);
				think();
			}
			else if(n == 6) {
				DiningPhilosophers.soMonitor.startSleep(getTID() - 1);
				philSleep();
				DiningPhilosophers.soMonitor.endSleep(getTID() - 1);
				think();
			}
			yield();
		}
	} // run()

	/**
	 * Prints out a phrase from the array of phrases at random.
	 * Feel free to add your own phrases.
	 */
	private void saySomething() {
		String[] astrPhrases = {
			"Eh, it's not easy to be a philosopher: eat, think, talk, eat...",
			"You know, true is false and false is true if you think of it",
			"2 + 2 = 5 for extremely large values of 2...",
			"If thee cannot speak, thee must be silent",
			"My number is " + getTID() + ""
		};

		System.out.println(
			"Philosopher " + getTID() + " says: " +
			astrPhrases[(int)(Math.random() * astrPhrases.length)]

		);
		for(int i = 0; i < 1000000000; i++) {

		}
	}
}

// EOF
