package org.mort11;

import edu.wpi.first.wpilibj.SpeedController;

public class EClutch implements SpeedController {
	private SpeedController wrappedController;
	private boolean stallFlag = false;
	private TemperatureIntegral tempIntegral = new TemperatureIntegral();

	private double setpoint;

	public EClutch(int PDPChannel, SpeedController wrappedController) throws Exception {
		this.wrappedController = wrappedController;
		ClutchThread.getInstance().registerMotor(this, PDPChannel);
	}

	@Override
	public double get() {
		return wrappedController.get();
	}

	@Override
	public void set(double speed, byte syncGroup) {
		this.setpoint = speed;
		if (isStalled()) {
			wrappedController.set(0);
		} else {
			wrappedController.set(setpoint);
		}
	}

	@Override
	public void set(double speed) {
		set(speed, (byte) 0);
	}

	@Override
	public void disable() {
		wrappedController.disable();
	}

	@Override
	public void pidWrite(double output) {

	}

	public boolean isStalled() {
		return this.stallFlag;
	}

	public TemperatureIntegral getTempIntegral() {
		return this.tempIntegral;
	}
}