package org.mort11;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SpeedController;

public class ClutchThread implements Runnable {

	public static final double POLL_HZ = 16;

	private final SpeedController[] clutchedControllers = new SpeedController[16];
	private PowerDistributionPanel pdp;

	public ClutchThread() {
		pdp = new PowerDistributionPanel();
		new Thread(this).start();
	}

	@Override
	public void run() {
		while (true) {
			double motorVoltage = pdp.getVoltage();
			for (int i = 0; i < clutchedControllers.length; i++) {
				if (clutchedControllers[i] != null) {
					double motorCurrent = pdp.getCurrent(i);
					// Update Temperature integral
					// Check for new Stall
					boolean stalled = false;
					boolean wasStalled = false;

					if (stalled && !wasStalled) {
						clutchedControllers[i].set(0);
					} else if (!stalled && wasStalled) {
						double desiredSpeed = clutchedControllers[i].get();
						clutchedControllers[i].set(desiredSpeed);
					}
				}
				// Set clutchedController stall flag
			}
			try {
				Thread.sleep((long) (1000 * 1 / POLL_HZ));
			} catch (InterruptedException e) {
				System.err.println(this + "Sleep Interrupted");
			}
		}
	}

	public synchronized void registerMotor(SpeedController motor, int PDPChannel) throws Exception {
		// Check for motor registered
		for (int i = 0; i < clutchedControllers.length; i++) {
			if (clutchedControllers[i] == motor) {
				throw new Exception();
			}
		}
		// Check for channel registered
		if (clutchedControllers[PDPChannel] != null) {
			throw new Exception();
		}
		clutchedControllers[PDPChannel] = motor;
	}

}
