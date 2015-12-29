package org.mort11;

public class TemperatureIntegral {
    // TODO: 12/29/15 Still get these constants
    private final double HEATING_CONSTANT = 0;
    private final double RPM_CONSTANT = 140;
    private final double ACTIVE_COOLING_CONSTANT = 0;
    private final double PASSIVE_COOLING_CONSTANT = 0;
    private double tempApproximation;
    private double lastTime;

    public void update(double voltage, double current, final double time) {
        double dt = time - lastTime;

        double speed = current * RPM_CONSTANT;

        double passiveCooling = PASSIVE_COOLING_CONSTANT * dt;
        double tempIncrease = current * HEATING_CONSTANT * dt;
        double activeCooling = ACTIVE_COOLING_CONSTANT * dt * speed;

        this.tempApproximation += passiveCooling + tempIncrease + activeCooling;
        this.lastTime = time;
    }

    public double getTempApproximation() {
        return tempApproximation;
    }
}
