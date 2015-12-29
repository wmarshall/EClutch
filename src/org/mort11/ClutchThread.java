package org.mort11;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.util.AllocationException;

public class ClutchThread implements Runnable {

	public static final double POLL_HZ = 16;

	private static ClutchThread singleton = null;

	private final EClutch[] clutchedControllers = new EClutch[16];
	private final Timer loopTimer;
	private PowerDistributionPanel pdp;

	private ClutchThread() {
		pdp = new PowerDistributionPanel();
		loopTimer = new Timer();
		new Thread(this).start();
	}

	public static ClutchThread getInstance() {
		if (singleton != null) {
			singleton = new ClutchThread();
		}
		return singleton;
	}

	@Override
	public void run() {
		loopTimer.start();
		while (true) {
			double motorVoltage = pdp.getVoltage();
			for (int i = 0; i < clutchedControllers.length; i++) {
				if (clutchedControllers[i] != null) {
					double motorCurrent = pdp.getCurrent(i);
					// Update Temperature integral
					TemperatureIntegral temp = clutchedControllers[i].getTempIntegral();
					temp.update(motorVoltage, motorCurrent, loopTimer.get());
					// Check for new Stall
					boolean stalled = temp.getTempApproximation() > clutchedControllers[i].getStallTemperature();
					boolean wasStalled = clutchedControllers[i].isStalled();

					if (stalled && !wasStalled) {
						clutchedControllers[i].set(0);
					} else if (!stalled && wasStalled) {
						double desiredSpeed = clutchedControllers[i].get();
						clutchedControllers[i].set(desiredSpeed);
					}
					clutchedControllers[i].setStalled(stalled);
				}
			}
			try {
				Thread.sleep((long) (1000 * 1 / POLL_HZ));
			} catch (InterruptedException e) {
				System.err.println(this + "Sleep Interrupted");
			}
		}
	}

	public synchronized void registerMotor(EClutch motor, int PDPChannel) throws AllocationException {
		// Check for motor registered
		for (int i = 0; i < clutchedControllers.length; i++) {
			if (clutchedControllers[i] == motor) {
				throw new AllocationException("Motor already allocated!");
			}
		}
		// Check for channel registered
		if (clutchedControllers[PDPChannel] != null) {
			throw new AllocationException("Channel already allocated");
		}
		clutchedControllers[PDPChannel] = motor;
	}

}
