package edu.pitt.cs;

import java.util.Random;

/**
 * Code by @author Wonsun Ahn
 * 
 * <p>Bean: Each bean is assigned a skill level from 0-9 on creation according to a
 * normal distribution with average SKILL_AVERAGE and standard deviation
 * SKILL_STDEV. The formula to calculate the skill level is:
 * 
 * <p>SKILL_AVERAGE = (double) (SLOT_COUNT-1) * 0.5
 * SKILL_STDEV = (double) Math.sqrt(SLOT_COUNT * 0.5 * (1 - 0.5))
 * SKILL_LEVEL = (int) Math.round(rand.nextGaussian() * SKILL_STDEV + SKILL_AVERAGE)
 * 
 * <p>A skill level of 9 means it always makes the "right" choices (pun intended)
 * when the machine is operating in skill mode ("skill" passed on command line).
 * That means the bean will always go right when a peg is encountered, resulting
 * it falling into slot 9. A skill evel of 0 means that the bean will always go
 * left, resulting it falling into slot 0. For the in-between skill levels, the
 * bean will first go right then left. For example, for a skill level of 7, the
 * bean will go right 7 times then go left twice.
 * 
 * <p>Skill levels are irrelevant when the machine operates in luck mode. In that
 * case, the bean will have a 50/50 chance of going right or left, regardless of
 * skill level. The formula to calculate the direction is: rand.nextInt(2). If
 * the return value is 0, the bean goes left. If the return value is 1, the bean
 * goes right.
 */

public class BeanImpl implements Bean {
	// TODO: Add member methods and variables as needed

	private int slotCount;
	private Random rand;
	private boolean isLuck;
	private int xpos;
	private int ypos;
	private double skillAverage; // MainPanel.SLOT_COUNT * 0.5;
	private double skillStdDev; // Math.sqrt(SLOT_COUNT * 0.5 * (1 - 0.5));
	private int rightChoices;
	private int remainingRightChoices;

	/**
	 * Constructor - creates a bean in either luck mode or skill mode.
	 * 
	 * @param slotCount the number of slots in the machine
	 * @param isLuck whether the bean is in luck mode
	 * @param rand   the random number generator
	 */
	BeanImpl(int slotCount, boolean isLuck, Random rand) {
		// TODO: Implement
		this.slotCount = slotCount;
		this.isLuck = isLuck;
		this.rand = rand;
		this.xpos = 0;
		this.ypos = 0;
		this.skillAverage = (slotCount - 1) * 0.5;
		this.skillStdDev = Math.sqrt(slotCount * 0.5 * (1 - 0.5));
		this.rightChoices = (int) Math.round(rand.nextGaussian() * skillStdDev + skillAverage);
		this.rightChoices = rightChoices > (slotCount - 1) ? (slotCount - 1) : rightChoices;
		this.rightChoices = rightChoices < 0 ? 0 : rightChoices;
		this.remainingRightChoices = rightChoices;
	}

	public int getXPos() {
		return xpos;
	}

	public int getYPos() {
		return ypos;
	}

	/**
	 * Resets the bean.
	 */
	public void reset() {
		remainingRightChoices = rightChoices;
		xpos = 0;
		ypos = 0;
	}

	/**
	 * Chooses left or right randomly (if luck) or according to skill. If the return
	 * value of rand.nextInt(2) is 0, the bean goes left. Otherwise, the bean goes
	 * right.
	 */
	public void advanceStep() throws BeanOutOfBoundsException {
    ypos++;
		if (isLuck) {
			if (rand.nextInt(2) == 0) {

			}else{
        xpos++;
      }
		} else {
			if (remainingRightChoices > 0) {
				xpos++;
				remainingRightChoices--;
			}
		}

		if (xpos >= slotCount || ypos >= slotCount) {
			throw new BeanOutOfBoundsException();
		}
	}
}