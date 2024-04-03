package edu.pitt.cs;

import gov.nasa.jpf.util.test.TestJPF;
import gov.nasa.jpf.vm.Verify;
import java.util.Random;
import org.junit.Test;

/**
 * Code by @author Wonsun Ahn
 * 
 * <p>
 * Uses the Java Path Finder model checking tool to check BeanCounterLogic in
 * various modes of operation. It checks BeanCounterLogic in both "luck" and
 * "skill" modes for various numbers of slots and beans. It also goes down all
 * the possible random path taken by the beans during operation.
 */

public class JPFJUnitTest extends TestJPF {
	private static BeanCounterLogic logic; // The core logic of the program
	private static Bean[] beans; // The beans in the machine

	private static int slotCount; // The number of slots in the machine we want to test
	private static int beanCount; // The number of beans in the machine we want to test
	private static boolean isLuck; // Whether the machine we want to test is in "luck" or "skill" mode

	/**
	 * Returns the number of in-flight beans that are streaming down the machine.
	 * 
	 * @return number of beans in-flight in the machine
	 */
	private int getInFlightBeanCount() {
		int inFlight = 0;
		for (int yPos = 0; yPos < slotCount; yPos++) {
			int xPos = logic.getInFlightBeanXPos(yPos);
			if (xPos != BeanCounterLogic.NO_BEAN_IN_YPOS) {
				inFlight++;
			}
		}
		return inFlight;
	}

	/**
	 * Returns the number of beans that are collected in the slots at the bottom.
	 * 
	 * @return number of beans in slots in the machine
	 */
	private int getInSlotsBeanCount() {
		int inSlots = 0;
		for (int i = 0; i < slotCount; i++) {
			inSlots += logic.getSlotBeanCount(i);
		}
		return inSlots;
	}

	/**
	 * Sets up the test fixture.
	 */
	public void setUp() {

		slotCount = Verify.getInt(1,5);
		beanCount = Verify.getInt(0,3);
		isLuck = Verify.getBoolean();
		
		// Create the internal logic
		logic = BeanCounterLogic.createInstance(InstanceType.IMPL, slotCount);
		// Create the beans
		beans = new Bean[beanCount];
		for (int i = 0; i < beanCount; i++) {
			beans[i] = Bean.createInstance(InstanceType.IMPL, slotCount, isLuck, new Random(42));
		}
	}

	/**
	 * Test case for void reset(Bean[] beans).
	 * 
	 * <pre>
	 * Preconditions: logic has been initialized with an instance of BeanCounterLogic.
	 *                beans has been initialized with an array of Bean objects.
	 * Execution steps: Call logic.reset(beans).
	 * Invariants: If beanCount is greater than 0,
	 *             remaining bean count is beanCount - 1
	 *             in-flight bean count is 1 (the bean initially at the top)
	 *             in-slot bean count is 0.
	 *             If beanCount is 0,
	 *             remaining bean count is 0
	 *             in-flight bean count is 0
	 *             in-slot bean count is 0.
	 * </pre>
	 */
	/* */
	@Test
	public void testReset() {
		/*
		 * When host JVM encounters the verifyNoPropertyViolation(), it invokes the JPF
		 * VM on this test method. So there are effectively two virtual machines
		 * executing this method. The verifyNoPropertyViolation() method returns false
		 * if the executing VM is the host JVM and returns true if it is the JPF VM.
		 */
		if (verifyNoPropertyViolation() == false) {
			// This is the host JVM so return immediately.
			return;
		}
		// This is the JPF VM, so run the test case on top of it, starting from the setUp().
		setUp();

		logic.reset(beans);

		if(beanCount > 0){
			assertEquals(beanCount-1, logic.getRemainingBeanCount());
			assertEquals(1, getInFlightBeanCount());
			assertEquals(0, getInSlotsBeanCount());
		}

		if(beanCount == 0){
			assertEquals(0, logic.getRemainingBeanCount());
			assertEquals(0, getInFlightBeanCount());
			assertEquals(0, getInSlotsBeanCount());
		}
	}

	/**
	 * Test case for boolean advanceStep().
	 * 
	 * <pre>
	 * Preconditions: logic has been initialized with an instance of BeanCounterLogic.
	 *                beans has been initialized with an array of Bean objects.
	 * Execution steps: Call logic.reset(beans).
	 *                  Call logic.advanceStep() in a loop until it returns false (the machine terminates).
	 * Invariants: After each advanceStep(),
	 *             getInFlightBeanXPos(ypos) for all rows in machine returns a legal xpos.
	 *             (For example, getInFlightBeanXPos(0) can either return 0 or BeanCounterLogic.NO_BEAN_IN_YPOS,
	 *              and getInFlightBeanXPos(1) can return 0, 1, or BeanCounterLogic.NO_BEAN_IN_YPOS.  And so on.)
	 * </pre>
	 */
	@Test
	public void testAdvanceStepCoordinates() throws BeanOutOfBoundsException {
		if (verifyNoPropertyViolation() == false) {
			return;
		}
		setUp();

		logic.reset(beans);
		while(logic.advanceStep()){ 
			for(int i = 0; i < slotCount; i++){ //outer for loop checks each row
				boolean validRow = false; 
				if(logic.getInFlightBeanXPos(i) == BeanCounterLogic.NO_BEAN_IN_YPOS) validRow = true;
				for(int j = 0; j <= i; j++){ //checks if bean on that row has a valid x coord
					if(logic.getInFlightBeanXPos(i) == j) validRow = true;
				}
				assertTrue(validRow);
			}
		}
	}

	/**
	 * Test case for boolean advanceStep().
	 * 
	 * <pre>
	 * Preconditions: logic has been initialized with an instance of BeanCounterLogic.
	 *                beans has been initialized with an array of Bean objects.
	 * Execution steps: Call logic.reset(beans).
	 *                  Call logic.advanceStep() in a loop until it returns false (the machine terminates).
	 * Invariants: After each advanceStep(),
	 *             the sum of remaining, in-flight, and in-slot beans is equal to beanCount.
	 * </pre>
	 */
	@Test
	public void testAdvanceStepBeanCount() throws BeanOutOfBoundsException {
		if (verifyNoPropertyViolation() == false) {
			return;
		}
		setUp();
		logic.reset(beans);
		while(logic.advanceStep()){
			int total = logic.getRemainingBeanCount() + getInFlightBeanCount() + getInSlotsBeanCount();
			assertEquals(beanCount, total);
		}
	}

	/**
	 * Test case for boolean advanceStep().
	 * 
	 * <pre>
	 * Preconditions: logic has been initialized with an instance of BeanCounterLogic.
	 *                beans has been initialized with an array of Bean objects.
	 * Execution steps: Call logic.reset(beans).
	 *                  Call logic.advanceStep() in a loop until it returns false (the machine terminates).
	 * Invariants: After the machine terminates,
	 *             remaining bean count is 0
	 *             in-flight bean count is 0
	 *             in-slot bean count is beanCount.
	 * </pre>
	 */
	@Test
	public void testAdvanceStepPostCondition() throws BeanOutOfBoundsException {
		if (verifyNoPropertyViolation() == false) {
			return;
		}
		setUp();

		logic.reset(beans);
		while(logic.advanceStep()){}

		assertEquals(0, logic.getRemainingBeanCount());
		assertEquals(0, getInFlightBeanCount());
		assertEquals(beanCount, getInSlotsBeanCount());
	}

	/**
	 * Test case for void lowerHalf()().
	 * 
	 * <pre>
	 * Preconditions: logic has been initialized with an instance of BeanCounterLogic.
	 *                beans has been initialized with an array of Bean objects.
	 * Execution steps: Call logic.reset(beans).
	 *                  Call logic.advanceStep() in a loop until it returns false (the machine terminates).
	 * 	                Calculate expected bean counts for each slot after having called logic.lowerHalf(),
	 *                  from current slot bean counts, and store into an expectedSlotCounts array.
	 *                  (The total count should be N/2 or (N+1)/2 depending on whether N is even or odd,
	 *                  where N is the original bean count.)
	 *                  Call logic.lowerHalf().
	 *                  Construct an observedSlotCounts array that stores current bean counts for each slot.
	 * Invariants: expectedSlotCounts matches observedSlotCounts exactly.
	 * </pre>
	 */
	@Test
	public void testLowerHalf() throws BeanOutOfBoundsException {
		if (verifyNoPropertyViolation() == false) {
			return;
		}
		setUp();

		int[] expectedSlotCounts = new int[slotCount]; 
		logic.reset(beans);
		while(logic.advanceStep()){}

		//pulling counts before lowerHalf and calculating expected
		for(int i = 0; i < slotCount; i++){
			int current = logic.getSlotBeanCount(i);
			if (current % 2 == 0) {
				expectedSlotCounts[i] = current / 2;
			} else {
				expectedSlotCounts[i] = (current + 1) / 2;
			}
		}

		logic.lowerHalf();

		//comparing vals
		for(int i = 0; i < slotCount; i++){
			assertEquals(expectedSlotCounts[i], logic.getSlotBeanCount(i));
		}
	}

	/**
	 * Test case for void upperHalf().
	 * 
	 * <pre>
	 * Preconditions: logic has been initialized with an instance of BeanCounterLogic.
	 *                beans has been initialized with an array of Bean objects.
	 * Execution steps: Call logic.reset(beans).
	 *                  Call logic.advanceStep() in a loop until it returns false (the machine terminates).
	 *                  Calculate expected bean counts for each slot after having called logic.upperHalf(),
	 *                  from current slot bean counts, and store into an expectedSlotCounts array.
	 *                  (The total count should be N/2 or (N+1)/2 depending on whether N is even or odd,
	 *                  where N is the original bean count.)
	 *                  Call logic.upperHalf().
	 *                  Construct an observedSlotCounts array that stores current bean counts for each slot.
	 * Invariants: expectedSlotCounts matches observedSlotCounts exactly.
	 * </pre>
	 */
	@Test
	public void testUpperHalf() throws BeanOutOfBoundsException {
		if (verifyNoPropertyViolation() == false) {
			return;
		}
		setUp();

		int[] expectedSlotCounts = new int[slotCount]; 
		logic.reset(beans);
		while(logic.advanceStep()){}

		//pulling counts before lowerHalf and calculating expected
		for(int i = 0; i < slotCount; i++){
			int current = logic.getSlotBeanCount(i);
			if (current % 2 == 0) {
				expectedSlotCounts[i] = current / 2;
			} else {
				expectedSlotCounts[i] = (current + 1) / 2;
			}
		}

		logic.upperHalf();

		//comparing vals
		for(int i = 0; i < slotCount; i++){
			assertEquals(expectedSlotCounts[i], logic.getSlotBeanCount(i));
		}
	}

	/**
	 * Test case for void repeat().
	 * 
	 * <pre>
	 * Preconditions: logic has been initialized with an instance of BeanCounterLogic.
	 *                beans has been initialized with an array of Bean objects.
	 * Execution steps: If beans are created in skill mode (if isLuck is false),
	 *                  Call logic.reset(beans).
	 *                  Call logic.advanceStep() in a loop until it returns false (the machine terminates).
	 *                  Construct an expectedSlotCounts array that stores current bean counts for each slot.
	 *                  Call logic.repeat();
	 *                  Call logic.advanceStep() in a loop until it returns false (the machine terminates).
	 *                  Construct an observedSlotCounts array that stores current bean counts for each slot.
	 * Invariants: expectedSlotCounts matches observedSlotCounts exactly.
	 * </pre>
	 */
	@Test
	public void testRepeat() throws BeanOutOfBoundsException {
		if (verifyNoPropertyViolation() == false) {
			return;
		}
		setUp();

		int[] expectedSlotCounts = new int[slotCount]; 
		logic.reset(beans);
		while(logic.advanceStep()){}

		//pulling counts before repeat and storing in expected
		for(int i = 0; i < slotCount; i++){
			expectedSlotCounts[i] = logic.getSlotBeanCount(i);
		}

		logic.repeat();
		while(logic.advanceStep()){}

		//comparing vals
		for(int i = 0; i < slotCount; i++){
			assertEquals(expectedSlotCounts[i], logic.getSlotBeanCount(i));
		}
	}
}
