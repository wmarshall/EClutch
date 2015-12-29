package org.mort11;

import edu.wpi.first.wpilibj.SpeedController;

public class EClutch implements SpeedController {
    private int PDPChannel;
    private SpeedController wrappedController;
    private boolean stallFlag = false;
    private TemperatureIntegral tempIntegral;

    private ClutchThread thread = new ClutchThread();

    private double setpoint;

    public EClutch(int PDPChannel, SpeedController wrappedController) {
        this.PDPChannel = PDPChannel;
        this.wrappedController = wrappedController;
    }

    @Override
    public double get() {
        return wrappedController.get();
    }

    @Override
    public void set(double speed, byte syncGroup) {
        this.setpoint = speed;

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
}