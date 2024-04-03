package edu.pitt.cs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;

/**
 * Code by @author Wonsun Ahn. Copyright Spring 2024.
 * 
 * <p>
 * Does integration testing on BeanCounterLogic. Makes tests deterministic by
 * seeding the random number generator for lucky beans and mocking it for
 * skilled beans.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BeanCounterLogicTest {

	private ByteArrayOutputStream out = new ByteArrayOutputStream();

	private final int slotCount = 5;
	private final int beanCount = 3;

	private BeanCounterLogic logic;
	private Bean[] luckyBeans;
	private Bean[] skilledBeans;
	Random rand1; //skill level 1
  	Random rand2; //skill level 4

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
	 * Sets up the JUnit test fixture.
	 */
	@Before
	public void setUp() {
		logic = BeanCounterLogic.createInstance(InstanceType.IMPL, slotCount);
		luckyBeans = new Bean[beanCount];
		skilledBeans = new Bean[beanCount];

		//populate luckyBeans
		int seed = 42;
		for (int i = 0; i < beanCount; i++) {
			luckyBeans[i] = Bean.createInstance(InstanceType.IMPL, slotCount, true, new Random(seed + i));
		}

		//populate skilledBeans[0]
		rand1 = mock(Random.class);
    	Mockito.when(rand1.nextGaussian()).thenReturn(-1.0);
		skilledBeans[0] = Bean.createInstance(InstanceType.IMPL, slotCount, false, rand1);

		//populate rest of skilledBeans
		rand2 = mock(Random.class);
    	Mockito.when(rand2.nextGaussian()).thenReturn(2.0);
		for(int i = 1; i < beanCount; i++) {
			skilledBeans[i] = Bean.createInstance(InstanceType.IMPL, slotCount, false, rand2);
		}
		System.setOut(new PrintStream(out));
	}
	/**
	 * Test reset(Bean[]).
	 * 
	 * <pre>
	 * Preconditions: logic, luckyBeans, skilledBeans initialized in setUp() as described.
	 * Execution steps: Pour lucky beans into machine by calling logic.reset(luckyBeans).
	 * Postconditions: logic.getRemainingBeanCount() returns 2.
	 *                 getInSlotsBeanCount() returnd 0.
	 *                 logic.getInFlightBeanXPos(0) returns 0.
	 *                 For all other i, logic.getInFlightBeanXPos(i) returns BeanCounterLogic.NO_BEAN_IN_YPOS.
	 * </pre>
	 */
	@Test
	public void testReset() {
		logic.reset(luckyBeans);
		assertEquals(2, logic.getRemainingBeanCount());
		assertEquals(0, getInSlotsBeanCount());
		assertEquals(0, logic.getInFlightBeanXPos(0));
		for(int i = 1; i < slotCount; i++){
			assertEquals(BeanCounterLogic.NO_BEAN_IN_YPOS, logic.getInFlightBeanXPos(i));
		}
	}
	/**
	 * Test calling advanceStep() in luck mode once.
	 * 
	 * <pre>
	 * Preconditions: logic, luckyBeans, skilledBeans initialized in setUp() as described.
	 * Execution steps: Pour lucky beans into machine by calling logic.reset(luckyBeans).
	 *                  Call logic.advanceStep() once.
	 * Postconditions: logic.getRemainingBeanCount() returns 1.
	 *                 getInSlotsBeanCount() returnd 0.
	 *                 logic.getInFlightBeanXPos(0) returns 0.
	 *                 logic.getInFlightBeanXPos(1) returns 0.
	 *                 For all other i, logic.getInFlightBeanXPos(i) returns BeanCounterLogic.NO_BEAN_IN_YPOS.
	 * </pre>
	 */
	@Test
	public void testAdvanceStepOnceLuckMode() throws BeanOutOfBoundsException {
		logic.reset(luckyBeans);
		logic.advanceStep();
		assertEquals(1, logic.getRemainingBeanCount());
		assertEquals(0, getInSlotsBeanCount());
		assertEquals(0, logic.getInFlightBeanXPos(0));
		assertEquals(0, logic.getInFlightBeanXPos(1)); 
		assertEquals(BeanCounterLogic.NO_BEAN_IN_YPOS, logic.getInFlightBeanXPos(2));
		assertEquals(BeanCounterLogic.NO_BEAN_IN_YPOS, logic.getInFlightBeanXPos(3));
		assertEquals(BeanCounterLogic.NO_BEAN_IN_YPOS, logic.getInFlightBeanXPos(4));
	}
	/**
	 * Test calling advanceStep() in luck mode twice.
	 * 
	 * <pre>
	 * Preconditions: logic, luckyBeans, skilledBeans initialized in setUp() as described.
	 * Execution steps: Pour lucky beans into machine by calling logic.reset(luckyBeans).
	 *                  Call logic.advanceStep() twice.
	 * Postconditions: logic.getRemainingBeanCount() returns 0.
	 *                 getInSlotsBeanCount() returnd 0.
	 *                 logic.getInFlightBeanXPos(0) returns 0.
	 *                 logic.getInFlightBeanXPos(1) returns 1.
	 *                 logic.getInFlightBeanXPos(2) returns 1.
	 *                 For all other i, logic.getInFlightBeanXPos(i) returns BeanCounterLogic.NO_BEAN_IN_YPOS.
	 * </pre>
	 */
	@Test
	public void testAdvanceStepTwiceLuckMode() throws BeanOutOfBoundsException {
		logic.reset(luckyBeans);
		logic.advanceStep();
		logic.advanceStep();
		assertEquals(0, logic.getRemainingBeanCount());
		assertEquals(0, getInSlotsBeanCount());
		assertEquals(0, logic.getInFlightBeanXPos(0));
		assertEquals(1, logic.getInFlightBeanXPos(1));
		assertEquals(1, logic.getInFlightBeanXPos(2));
		assertEquals(BeanCounterLogic.NO_BEAN_IN_YPOS, logic.getInFlightBeanXPos(3));
		assertEquals(BeanCounterLogic.NO_BEAN_IN_YPOS, logic.getInFlightBeanXPos(4));
	}
	/**
	 * Test calling advanceStep() in luck mode thrice.
	 * 
	 * <pre>
	 * Preconditions: logic, luckyBeans, skilledBeans initialized in setUp() as described.
	 * Execution steps: Pour lucky beans into machine by calling logic.reset(luckyBeans).
	 *                  Call logic.advanceStep() thrice.
	 * Postconditions: logic.getRemainingBeanCount() returns 0.
	 *                 getInSlotsBeanCount() returnd 0.
	 *                 logic.getInFlightBeanXPos(1) returns 1.
	 *                 logic.getInFlightBeanXPos(2) returns 2.
	 *                 logic.getInFlightBeanXPos(3) returns 1.
	 *                 For all other i, logic.getInFlightBeanXPos(i) returns BeanCounterLogic.NO_BEAN_IN_YPOS.
	 * </pre>
	 */
	@Test
	public void testAdvanceStepThriceLuckMode() throws BeanOutOfBoundsException {
		logic.reset(luckyBeans);
		for(int i = 0; i < 3; i++){
			logic.advanceStep();
		}
		assertEquals(0, logic.getRemainingBeanCount());
		assertEquals(0, getInSlotsBeanCount());
		assertEquals(BeanCounterLogic.NO_BEAN_IN_YPOS, logic.getInFlightBeanXPos(0));
		assertEquals(1, logic.getInFlightBeanXPos(1));
		assertEquals(2, logic.getInFlightBeanXPos(2));
		assertEquals(1, logic.getInFlightBeanXPos(3));
		assertEquals(BeanCounterLogic.NO_BEAN_IN_YPOS, logic.getInFlightBeanXPos(4));
	}
	/**
	 * Test calling advanceStep() in luck mode 4 times.
	 * 
	 * <pre>
	 * Preconditions: logic, luckyBeans, skilledBeans initialized in setUp() as described.
	 * Execution steps: Pour lucky beans into machine by calling logic.reset(luckyBeans).
	 *                  Call logic.advanceStep() 4 times.
	 * Postconditions: logic.getRemainingBeanCount() returns 0.
	 *                 getInSlotsBeanCount() returnd 0.
	 *                 logic.getInFlightBeanXPos(2) returns 1.
	 *                 logic.getInFlightBeanXPos(3) returns 2.
	 *                 logic.getInFlightBeanXPos(4) returns 2.
	 *                 For all other i, logic.getInFlightBeanXPos(i) returns BeanCounterLogic.NO_BEAN_IN_YPOS.
	 * </pre>
	 */
	@Test
	public void testAdvanceStep4TimesLuckMode() throws BeanOutOfBoundsException {
		logic.reset(luckyBeans);
		for(int i = 0; i < 4; i++){
			logic.advanceStep();
		}
		assertEquals(0, logic.getRemainingBeanCount());
		assertEquals(0, getInSlotsBeanCount());
		assertEquals(BeanCounterLogic.NO_BEAN_IN_YPOS, logic.getInFlightBeanXPos(0));
		assertEquals(BeanCounterLogic.NO_BEAN_IN_YPOS, logic.getInFlightBeanXPos(1));
		assertEquals(2, logic.getInFlightBeanXPos(3));
		assertEquals(2, logic.getInFlightBeanXPos(4));
	}
	/**
	 * Test calling advanceStep() in luck mode 5 times.
	 * 
	 * <pre>
	 * Preconditions: logic, luckyBeans, skilledBeans initialized in setUp() as described.
	 * Execution steps: Pour lucky beans into machine by calling logic.reset(luckyBeans).
	 *                  Call logic.advanceStep() 5 times.
	 * Postconditions: logic.getRemainingBeanCount() returns 0.
	 *                 logic.getInFlightBeanXPos(3) returns 2.
	 *                 logic.getInFlightBeanXPos(4) returns 2.
	 *                 For all other i, logic.getInFlightBeanXPos(i) returns BeanCounterLogic.NO_BEAN_IN_YPOS.
	 *                 logic.getSlotBeanCount(2) returns 1.
	 *                 For all other i, logic.getSlotBeanCount(i) returns 0.
	 * </pre>
	 */
	@Test
	public void testAdvanceStep5TimesLuckMode() throws BeanOutOfBoundsException {
		//------------Execution------------
		logic.reset(luckyBeans);
		for(int i = 0; i < 5; i++){
			logic.advanceStep();
		}
		//---------------------------------

		assertEquals(0, logic.getRemainingBeanCount());
		for(int i = 0; i < slotCount; i++){
			if(i == 3 || i ==4){
				assertEquals(2, logic.getInFlightBeanXPos(i));
			}
			else{
				assertEquals(BeanCounterLogic.NO_BEAN_IN_YPOS, logic.getInFlightBeanXPos(i));
			}
		}
		for(int j = 0; j < slotCount; j++){
			if(j == 2){
				assertEquals(1, logic.getSlotBeanCount(j));
			}else{
				assertEquals(0, logic.getSlotBeanCount(j));
			}
		}
	}
	/**
	 * Test calling advanceStep() in luck mode 6 times.
	 * 
	 * <pre>
	 * Preconditions: logic, luckyBeans, skilledBeans initialized in setUp() as described.
	 * Execution steps: Pour lucky beans into machine by calling logic.reset(luckyBeans).
	 *                  Call logic.advanceStep() 6 times.
	 * Postconditions: logic.getRemainingBeanCount() returns 0.
	 *                 logic.getInFlightBeanXPos(4) returns 3.
	 *                 For all other i, logic.getInFlightBeanXPos(i) returns BeanCounterLogic.NO_BEAN_IN_YPOS.
	 *                 logic.getSlotBeanCount(2) returns 2.
	 *                 For all other i, logic.getSlotBeanCount(i) returns 0.
	 * </pre>
	 */
	@Test
	public void testAdvanceStep6TimesLuckMode() throws BeanOutOfBoundsException {
		//------------Execution------------
		logic.reset(luckyBeans);
		for(int i = 0; i < 6; i++){
			logic.advanceStep();
		}
		//---------------------------------

		assertEquals(0, logic.getRemainingBeanCount());
		for(int i = 0; i < slotCount; i++){
			if(i == 4){
				assertEquals(3, logic.getInFlightBeanXPos(4));
			}else{
				assertEquals(BeanCounterLogic.NO_BEAN_IN_YPOS, logic.getInFlightBeanXPos(i));
			}
		}
		for(int j = 0; j < slotCount; j++){
			if(j == 2){
				assertEquals(2, logic.getSlotBeanCount(j));
			}else{
				assertEquals(0, logic.getSlotBeanCount(j));
			}
		}

	}
	/**
	 * Test calling advanceStep() in luck mode 7 times.
	 * 
	 * <pre>
	 * Preconditions: logic, luckyBeans, skilledBeans initialized in setUp() as described.
	 * Execution steps: Pour lucky beans into machine by calling logic.reset(luckyBeans).
	 *                  Call logic.advanceStep() 7 times.
	 * Postconditions: logic.getRemainingBeanCount() returns 0.
	 *                 getInFlightBeanCount() returns 0.
	 *                 logic.getSlotBeanCount(2) returns 2.
	 *                 logic.getSlotBeanCount(3) returns 1.
	 *                 For all other i, logic.getSlotBeanCount(i) returns 0.
	 * </pre>
	 */
	@Test
	public void testAdvanceStep7TimesLuckMode() throws BeanOutOfBoundsException {
		//------------Execution------------
		logic.reset(luckyBeans);
		for(int i = 0; i < 7; i++){
			logic.advanceStep();
		}
		//---------------------------------

		assertEquals(0, logic.getRemainingBeanCount());
		assertEquals(0, getInFlightBeanCount());
		for(int j = 0; j < slotCount; j++){
			if(j == 2){
				assertEquals(2, logic.getSlotBeanCount(j));
			}else if(j == 3){
				assertEquals(1, logic.getSlotBeanCount(j));
			}else{
				assertEquals(0, logic.getSlotBeanCount(j));
			}
		}
	}
	/**
	 * Test calling advanceStep() in skill mode.
	 * 
	 * <pre>
	 * Preconditions: logic, luckyBeans, skilledBeans initialized in setUp() as described.
	 * Execution steps: Pour skilled beans into machine by calling logic.reset(skilledBeans).
	 *                  Call logic.advanceStep() 7 times.
	 * Postconditions: logic.getRemainingBeanCount() returns 0.
	 *                 getInFlightBeanCount() returns 0.
	 *                 logic.getSlotBeanCount(1) returns 1.
	 *                 logic.getSlotBeanCount(4) returns 2.
	 *                 For all other i, logic.getSlotBeanCount(i) returns 0.
	 * </pre>
	 */
	@Test
	public void testAdvanceStepSkillMode() throws BeanOutOfBoundsException {
		//------------Execution------------
		logic.reset(skilledBeans);
		for(int i = 0; i < 7; i++){
			logic.advanceStep();
		}
		//---------------------------------

		assertEquals(0, logic.getRemainingBeanCount());
		assertEquals(0, getInFlightBeanCount());
		for(int j = 0; j < slotCount; j++){
			if(j == 1){
				assertEquals(1, logic.getSlotBeanCount(j));
			}else if(j == 4){
				assertEquals(2, logic.getSlotBeanCount(j));
			}else{
				assertEquals(0, logic.getSlotBeanCount(j));
			}
		}
	}
	/**
	 * Test calling lowerHalf() in skill mode.
	 * 
	 * <pre>
	 * Preconditions: logic, luckyBeans, skilledBeans initialized in setUp() as described.
	 * Execution steps: Pour skilled beans into machine by calling logic.reset(skilledBeans).
	 *                  Call logic.advanceStep() until it returns false.
	 *                  Call logic.lowerHalf().
	 * Postconditions: logic.getSlotBeanCount(1) returns 1.
	 *                 logic.getSlotBeanCount(4) returns 1.
	 *                 For all other i, logic.getSlotBeanCount(i) returns 0.
	 * </pre>
	 */
	@Test
	public void testLowerHalf() throws BeanOutOfBoundsException {
		//------------Execution------------
		logic.reset(skilledBeans);
		while(logic.advanceStep()){}
		logic.lowerHalf();
		//---------------------------------

		for(int j = 0; j < slotCount; j++){
			if(j == 1){
				assertEquals(1, logic.getSlotBeanCount(j));
			}else if(j == 4){
				assertEquals(1, logic.getSlotBeanCount(j));
			}else{
				assertEquals(0, logic.getSlotBeanCount(j));
			}
		}
	}
	/**
	 * Test calling upperHalf() in skill mode.
	 * 
	 * <pre>
	 * Preconditions: logic, luckyBeans, skilledBeans initialized in setUp() as described.
	 * Execution steps: Pour skilled beans into machine by calling logic.reset(skilledBeans).
	 *                  Call logic.advanceStep() until it returns false.
	 *                  Call logic.upperHalf().
	 * Postconditions: logic.getSlotBeanCount(4) returns 2.
	 *                 For all other i, logic.getSlotBeanCount(i) returns 0.
	 * </pre>
	 */
	@Test
	public void testUpperHalf() throws BeanOutOfBoundsException {
		//------------Execution------------
		logic.reset(skilledBeans);
		while(logic.advanceStep()){}
		logic.upperHalf();
		//---------------------------------

		for(int j = 0; j < slotCount; j++){
			if(j == 4){
				assertEquals(2, logic.getSlotBeanCount(j));
			}else{
				assertEquals(0, logic.getSlotBeanCount(j));
			}
		}
	}

	/**
	 * Test calling repeat() in skill mode.
	 * 
	 * <pre>
	 * Preconditions: logic, luckyBeans, skilledBeans initialized in setUp() as described.
	 * Execution steps: Pour skilled beans into machine by calling logic.reset(skilledBeans).
	 *                  Call logic.advanceStep() until it returns false.
	 *                  Call logic.repeat().
	 *                  Call logic.advanceStep() until it returns false.
	 * Postconditions: logic.getRemainingBeanCount() returns 0.
	 *                 getInFlightBeanCount() returns 0.
	 *                 logic.getSlotBeanCount(1) returns 1.
	 *                 logic.getSlotBeanCount(4) returns 2.
	 *                 For all other i, logic.getSlotBeanCount(i) returns 0.
	 * </pre>
	 */
	@Test
	public void testRepeat() throws BeanOutOfBoundsException {
		//------------Execution------------
		logic.reset(skilledBeans);
		while(logic.advanceStep()){}
		logic.repeat();
		while(logic.advanceStep()){}
		//---------------------------------

		assertEquals(0, logic.getRemainingBeanCount());
		assertEquals(0, getInFlightBeanCount());
		for(int j = 0; j < slotCount; j++){
			if(j == 1){
				assertEquals(1, logic.getSlotBeanCount(j));
			}else if(j == 4){
				assertEquals(2, logic.getSlotBeanCount(j));
			}else{
				assertEquals(0, logic.getSlotBeanCount(j));
			}
		}
	}

	/**
	 * Test calling getAverageSlotBeanCount() in luck mode.
	 * 
	 * <pre>
	 * Preconditions: logic, luckyBeans, skilledBeans initialized in setUp() as described.
	 * Execution steps: Pour lucky beans into machine by calling logic.reset(luckyBeans).
	 *                  Call logic.advanceStep() until it returns false.
	 *                  Call logic.getAverageSlotBeanCount().
	 * Postconditions: return value is 2.3333333333333333 within a difference delta of 0.001
	 * </pre>
	 */
	@Test
	public void testGetAverageSlotBeanCount() throws BeanOutOfBoundsException {
		logic.reset(luckyBeans);
		while(logic.advanceStep()){}
		double expectedAverage = 2.3333333333333333;
		assertEquals(expectedAverage, logic.getAverageSlotBeanCount(), 0.001);
	}

	/**
	 * Test main(String[] args).
	 * 
	 * <pre>
	 * Preconditions: None.
	 * Execution steps: Call BeanCounterLogicImpl.main("10", "500", "luck").
	 * Postconditions: There are two lines of output.
	 *             There are 10 slot counts on the second line of output.
	 *             The sum of the 10 slot counts is equal to 500.
	 * </pre>
	 */
	@Test
	public void testMain() {
		String[] stringArray = {"10", "500", "luck"};
		BeanCounterLogicImpl.main(stringArray);

		String output = out.toString().trim();

        String[] lines = output.split("\\r?\\n");

        assertEquals(2, lines.length);
        assertEquals("Slot bean counts:", lines[0].trim());

        String[] counts = lines[1].trim().split("\\s+");
        assertEquals(10, counts.length);

        // Ensure the sum of the slot counts is equal to 500
        int sum = 0;
        for (String count : counts) {
            sum += Integer.parseInt(count);
        }
        assertEquals(500, sum);

	}

}
