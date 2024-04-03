package edu.pitt.cs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.Random;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BeanTest {

	final int beanCount = 4;
	final int slotCount = 10;

	Random rand;
  Random rand1;
  Random rand2;
  Random rand3;
	Bean[] beans;

	/**
	 * Set up the JUnit test fixture.
	 */
	@Before
	public void setUp() {
		beans = new Bean[beanCount];

		//Create a mock Random object and assign to rand
		rand = mock(Random.class);
    Mockito.when(rand.nextInt(2)).thenReturn(0);

    rand1 = mock(Random.class);
    Mockito.when(rand1.nextGaussian()).thenReturn(-3.0);

    rand2 = mock(Random.class);
    Mockito.when(rand2.nextGaussian()).thenReturn(-1.5);

    rand3 = mock(Random.class);
    Mockito.when(rand3.nextGaussian()).thenReturn(3.0);
		
		// Call Bean.createInstance to create a bean in luck mode for slotCount and assign to beans[0].
		// To make the bean always go left, pass in a rand which always returns 0 on rand.nextInt(2).
    Bean bean0 = Bean.createInstance(InstanceType.IMPL, slotCount, true, rand);
		beans[0] = bean0;
		
		// Call Bean.createInstance to create a bean in skilled mode for slotCount and assign to beans[1].
		// To fix bean skill level to 0, pass in a rand which always returns -3.0 on rand.nextGaussian().
		Bean bean1 = Bean.createInstance(InstanceType.IMPL, slotCount, false, rand1);
		beans[1] = bean1;

		// Call Bean.createInstance to create a bean in skilled mode for slotCount and assign to beans[2].
		// To fix bean skill level to 2, pass in a rand which always returns -1.5 on rand.nextGaussian().
		Bean bean2 = Bean.createInstance(InstanceType.IMPL, slotCount, false, rand2);
		beans[2] = bean2;

		// Call Bean.createInstance to create a bean in skilled mode for slotCount and assign to beans[3].
		// To fix bean skill level to 9, pass in a rand which always returns 3.0 on rand.nextGaussian().
		Bean bean3 = Bean.createInstance(InstanceType.IMPL, slotCount, false, rand3);
		beans[3] = bean3;
	}

	/**
	 * Test BeanImpl(int slotCount, boolean isLuck, Random rand).
	 * 
	 * <pre>
	 * Preconditions: beans array populated with beans as described in setUp().
	 * Execution steps: None. 
	 * Postconditions: For each bean in beans, getXPos() == 0 and getYPos() == 0.
	 * </pre>
	 */
	@Test
	public void testConstructor() {
		for (int i = 0; i < beans.length; i++) {
		assertEquals(0, beans[i].getXPos());
		assertEquals(0, beans[i].getYPos());
		}
	}
	/**
	 * Test reset().
	 * 
	 * <pre>
	 * Preconditions: beans array populated with beans as described in setUp().
	 * Execution steps: For each bean in beans, call advanceStep().
	 *                  For each bean in beans, call reset().
	 * Postconditions: For each bean in beans, getXPos() == 0 and getYPos() == 0.
	 * </pre>
	 */
	@Test
	public void testReset() throws BeanOutOfBoundsException {
		for (int i = 0; i < beans.length; i++) {
			beans[i].advanceStep();
			beans[i].reset();
		  	assertEquals(0, beans[i].getXPos());
		  	assertEquals(0, beans[i].getYPos());
		}
	}

	/**
	 * Test advanceStep().
	 * 
	 * <pre>
	 * Preconditions: beans array populated with beans as described in setUp().
	 *                beans[0] always goes left by having rand.nextInt(2) return 0.
	 * Execution steps: For beans[0], call advanceStep().
	 * Postconditions: For beans[0], getXPos() == 0 and getYPos() == 1.
	 * </pre>
	 */
	@Test
	public void testAdvanceStepLuckyBeanOnceLeft() throws BeanOutOfBoundsException {
    	beans[0].advanceStep();
    	assertEquals(0, beans[0].getXPos());
    	assertEquals(1, beans[0].getYPos());
	}

	/**
	 * Test advanceStep().
	 * 
	 * <pre>
	 * Preconditions: beans array populated with beans as described in setUp().
	 *                beans[0] always goes left by having rand.nextInt(2) return 0.
	 * Execution steps: For beans[0], call advanceStep() twice.
	 * Postconditions: For beans[0], getXPos() == 0 and getYPos() == 2.
	 * </pre>
	 */
	@Test
	public void testAdvanceStepLuckyBeanTwiceLeft() throws BeanOutOfBoundsException {
		Mockito.when(rand.nextInt(2)).thenReturn(0);
    	beans[0].advanceStep();
    	beans[0].advanceStep();
    	assertEquals(0, beans[0].getXPos());
    	assertEquals(2, beans[0].getYPos());
	}

	/**
	 * Test advanceStep().
	 * 
	 * <pre>
	 * Preconditions: beans array populated with beans as described in setUp().
	 *                beans[0] always goes right by having rand.nextInt(2) return 1.
	 * Execution steps: For beans[0], call advanceStep().
	 * Postconditions: For beans[0], getXPos() == 1 and getYPos() == 1.
	 * </pre>
	 */
	@Test
	public void testAdvanceStepLuckyBeanOnceRight() throws BeanOutOfBoundsException {
		Mockito.when(rand.nextInt(2)).thenReturn(1);
    	beans[0].advanceStep();
    	assertEquals(1, beans[0].getXPos());
    	assertEquals(1, beans[0].getYPos());
	}

	/**
	 * Test advanceStep().
	 * 
	 * <pre>
	 * Preconditions: beans array populated with beans as described in setUp().
	 *                beans[0] always goes right by having rand.nextInt(2) return 1.
	 * Execution steps: For beans[0], call advanceStep() twice.
	 * Postconditions: For beans[0], getXPos() == 2 and getYPos() == 2.
	 * </pre>
	 */
	@Test
	public void testAdvanceStepLuckyBeanTwiceRight() throws BeanOutOfBoundsException {
		Mockito.when(rand.nextInt(2)).thenReturn(1);
    	beans[0].advanceStep();
    	beans[0].advanceStep();
    	assertEquals(2, beans[0].getXPos());
    	assertEquals(2, beans[0].getYPos());
	}

	/**
	 * Test advanceStep().
	 * 
	 * <pre>
	 * Preconditions: beans array populated with beans as described in setUp().
	 *                beans[0] always goes left by having rand.nextInt(2) return 0.
	 * Execution steps: For beans[0], call advanceStep() 10 times.
	 * Postconditions: BeanOutOfBoundsException is thrown at 10th call to advanceStep().
	 * </pre>
	 */
	@Test
    public void testAdvanceStepLuckyBean10TimesLeft() throws BeanOutOfBoundsException {
    	Mockito.when(rand.nextInt(2)).thenReturn(0);
      	// Call advanceStep() 9 times
      	for (int i = 0; i < 9; i++) {
        	beans[0].advanceStep();
      	}
      	// On the 10th call, assert that a BeanOutOfBoundsException is thrown
      	assertThrows(BeanOutOfBoundsException.class, () -> beans[0].advanceStep());
    }

	/**
	 * Test advanceStep().
	 * 
	 * <pre>
	 * Preconditions: beans array populated with beans as described in setUp().
	 * Execution steps: For beans[1], call advanceStep() 9 times.
	 * Postconditions: For beans[1], getXPos() == 0 and getYPos() == 9.
	 * </pre>
	 */
	@Test
	public void testAdvanceStepSkill0Bean9Times() throws BeanOutOfBoundsException {
      	Mockito.when(rand.nextGaussian()).thenReturn(-3.0);
		for (int i = 0; i < 9; i++) {
        	beans[1].advanceStep();
    	}
	  	assertEquals(0, beans[1].getXPos() );
    	assertEquals(9, beans[1].getYPos());
	}

	
	/**
	 * Test advanceStep().
	 * 
	 * <pre>
	 * Preconditions: beans array populated with beans as described in setUp().
	 * Execution steps: For beans[2], call advanceStep() 9 times.
	 * Postconditions: For beans[2], getXPos() == 2 and getYPos() == 9.
	 * </pre>
	 */
	@Test
	public void testAdvanceStepSkill2Bean9Times() throws BeanOutOfBoundsException {
      	Mockito.when(rand.nextGaussian()).thenReturn(3.0);
		for (int i = 0; i < 9; i++) {
     		beans[2].advanceStep();
    	}
	  	assertEquals(2, beans[2].getXPos());
    	assertEquals(9, beans[2].getYPos());
	}

	/**
	 * Test advanceStep().
	 * 
	 * <pre>
	 * Preconditions: beans array populated with beans as described in setUp().
	 * Execution steps: For beans[3], call advanceStep() 9 times.
	 * Postconditions: For beans[3], getXPos() == 9 and getYPos() == 9.
	 * </pre>
	 */
	@Test
	public void testAdvanceStepSkill9Bean9Times() throws BeanOutOfBoundsException {
      	Mockito.when(rand.nextGaussian()).thenReturn(3.0);
		for (int i = 0; i < 9; i++) {
       		beans[3].advanceStep();
    	}
    	assertEquals(9, beans[3].getXPos());
    	assertEquals(9, beans[3].getYPos());
	}

	/**
	 * Test advanceStep().
	 * 
	 * <pre>
	 * Preconditions: beans array populated with beans as described in setUp().
	 * Execution steps: For beans[2], call advanceStep() once.
	 * Postconditions: For beans[2], getXPos() == 1 and getYPos() == 1.
	 * </pre>
	 */
	@Test
	public void testAdvanceStepSkill2BeanOnce() throws BeanOutOfBoundsException {
		beans[2].advanceStep();
	  	assertEquals(1, beans[2].getXPos());
    	assertEquals(1, beans[2].getYPos());
	}

	/**
	 * Test advanceStep().
	 * 
	 * <pre>
	 * Preconditions: beans array populated with beans as described in setUp().
	 * Execution steps: For beans[2], call advanceStep() twice.
	 * Postconditions: For beans[2], getXPos() == 2 and getYPos() == 2.
	 * </pre>
	 */
	@Test
	public void testAdvanceStepSkill2BeanTwice() throws BeanOutOfBoundsException {
		beans[2].advanceStep();
    	beans[2].advanceStep();
    	assertEquals(2, beans[2].getXPos());
    	assertEquals(2, beans[2].getYPos());
	}

	/**
	 * Test advanceStep().
	 * 
	 * <pre>
	 * Preconditions: beans array populated with beans as described in setUp().
	 * Execution steps: For beans[2], call advanceStep() thrice.
	 * Postconditions: For beans[2], getXPos() == 2 and getYPos() == 3.
	 * </pre>
	 */
	@Test
	public void testAdvanceStepSkill2BeanThrice() throws BeanOutOfBoundsException {
      	Mockito.when(rand.nextGaussian()).thenReturn(-1.5);
        beans[2].advanceStep();
        beans[2].advanceStep();
        beans[2].advanceStep();
        assertEquals(2, beans[2].getXPos());
        assertEquals(3, beans[2].getYPos());
	}
}
