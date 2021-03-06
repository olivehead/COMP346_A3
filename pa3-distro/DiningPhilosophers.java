
import java.util.Scanner;

/**
 * Class DiningPhilosophers
 * The main starter.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class DiningPhilosophers
{
	/*
	 * ------------
	 * Data members
	 * ------------
	 */

	/**
	 * This default may be overridden from the command line
	 */
	private static final int DEFAULT_NUMBER_OF_PHILOSOPHERS = 4;

	/**
	 * Dining "iterations" per philosopher thread
	 * while they are socializing there
	 */
	static final int DINING_STEPS = 10;

	/**
	 * Our shared monitor for the philosophers to consult
	 */
	static Monitor soMonitor = null;

	//public static Philosopher aoPhilosophers[] = null;

	/*
	 * -------
	 * Methods
	 * -------
	 */

	/**
	 * Main system starts up right here
	 */
	public static void main(String[] argv)
	{
		try
		{
			/*
			 * TODO:
			 * Should be settable from the command line
			 * or the default if no arguments supplied.
			 */
			Scanner keyboard = new Scanner(System.in);
			int iPhilosophers;

			System.out.print("%java DiningPhilosophers ");
			String answer = keyboard.nextLine();
			try {
				iPhilosophers = Integer.parseInt(answer);
				if(iPhilosophers > 0) {
					System.out.println("Usage: java DiningPhilosophers[" + iPhilosophers + "]\n%\n");
				}
				else {
					iPhilosophers = DEFAULT_NUMBER_OF_PHILOSOPHERS;
					System.out.println("\"" + answer + "\" is not a positive decimal integer\n");
					System.out.println("Usage: java DiningPhilosophers[" + DEFAULT_NUMBER_OF_PHILOSOPHERS + "]\n%\n");
				}
			}
			catch(Exception e) {
				iPhilosophers = DEFAULT_NUMBER_OF_PHILOSOPHERS;
				System.out.println("\"" + answer + "\" is not a positive decimal integer\n");
				System.out.println("Usage: java DiningPhilosophers[" + DEFAULT_NUMBER_OF_PHILOSOPHERS + "]\n%\n");
			}
//			System.out.println("Use default number of philosophers? (y/n)");
//			String answer = keyboard.next();
//			if (answer.equals("y")) {
//				iPhilosophers = DEFAULT_NUMBER_OF_PHILOSOPHERS;
//			}
//			else if (answer.equals("n")) {
//				System.out.println("Enter number of philosophers: ");
//				iPhilosophers = keyboard.nextInt();
//			}
//			else {
//				System.out.println("Not a valid input. Ending program.");
//				return;
//			}


			// Make the monitor aware of how many philosophers there are
			soMonitor = new Monitor(iPhilosophers);

			// Space for all the philosophers
			Philosopher[] aoPhilosophers = new Philosopher[iPhilosophers];
			//aoPhilosophers = new Philosopher[iPhilosophers];

			// Let 'em sit down
			for(int j = 0; j < iPhilosophers; j++)
			{
				aoPhilosophers[j] = new Philosopher();
				aoPhilosophers[j].start();
			}

			System.out.println
			(
				iPhilosophers +
				" philosopher(s) came in for a dinner."
			);

			// Main waits for all its children to die...
			// I mean, philosophers to finish their dinner.
			for(int j = 0; j < iPhilosophers; j++)
				aoPhilosophers[j].join();

			System.out.println("All philosophers have left. System terminates normally.");
		}
		catch(InterruptedException e)
		{
			System.err.println("main():");
			reportException(e);
			System.exit(1);
		}
	} // main()

	/**
	 * Outputs exception information to STDERR
	 * @param poException Exception object to dump to STDERR
	 */
	static void reportException(Exception poException)
	{
		System.err.println("Caught exception : " + poException.getClass().getName());
		System.err.println("Message          : " + poException.getMessage());
		System.err.println("Stack Trace      : ");
		poException.printStackTrace(System.err);
	}
}

// EOF
