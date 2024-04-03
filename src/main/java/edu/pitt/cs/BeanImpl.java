package edu.pitt.cs;

import java.util.Random;

public class BeanImpl implements Bean {

  private int xpos;
  private int ypos;
  private int slotCount;
  private boolean isLuck;
  private Random rand;
private int SKILL_LEVEL;

  /**
   * Constructor - creates a bean in either luck mode or skill mode.
   * 
   * @param slotCount the number of slots in the machine
   * @param isLuck    whether the bean is in luck mode
   * @param rand      the random number generator
   */
  BeanImpl(int slotCount, boolean isLuck, Random rand) {
    this.slotCount = slotCount;
    this.isLuck = isLuck;
    this.rand = rand;

    //if (!isLuck) {
      // Skill mode
      double SKILL_AVERAGE = (double) (slotCount-1) * 0.5;
      double SKILL_STDEV = (double) Math.sqrt(slotCount * 0.5 * (1 - 0.5));
      SKILL_LEVEL = (int) Math.round(rand.nextGaussian() * SKILL_STDEV + SKILL_AVERAGE);
      SKILL_LEVEL = SKILL_LEVEL > (slotCount - 1) ? (slotCount - 1) : SKILL_LEVEL;
      SKILL_LEVEL = SKILL_LEVEL < 0 ? 0 : SKILL_LEVEL;
    //}
  }

  /**
   * Returns the current X-coordinate position of the bean in the logical
   * coordinate system.
   * 
   * @return the current X-coordinate of the bean
   */
  public int getXPos() {
    return xpos;
  }

  /**
   * Returns the current Y-coordinate position of the bean in the logical
   * coordinate system.
   * 
   * @return the current Y-coordinate of the bean
   */
  public int getYPos() {
    return ypos;
  }

  /**
   * Resets the bean to its initial state. The X-coordinate should be initialized
   * to 0.
   */
  public void reset() {
    xpos = 0;
    ypos = 0;
  }
  /**
   * Update the X and Y coordinates of the bean when the bean is advanced one step
   * in the machine. The Y coordinate is incremented by 1. The X coordinate gets
   * updated depending on whether the bean chooses to bounce left or right from
   * the current peg. The choice is made randomly if the bean is in luck mode
   * depending on the return value of rand.nextInt(2): if it is 0, the bean goes
   * left, if it is 1, the bean goes right. If the bean is a skilled bean, the
   * choice is made deterministically according to the algorithm on the class
   * description. If the resulting X or Y coordinates are greater than or equal
   * to slotCount throw BeanOutOfBoundsException.
   */
  public void advanceStep() throws BeanOutOfBoundsException {
    ypos++;
    if (isLuck) {
      // Luck mode

      if (rand.nextInt(2) == 1 ) {
        xpos++;
      } else{
      // Go right
      }
    } else {
      //skill mode
      if (xpos < SKILL_LEVEL){
        xpos++;
      }
    }
     
    if (xpos < 0 || xpos >= slotCount || ypos >= slotCount) {
      throw new BeanOutOfBoundsException();
    }
  }
}
