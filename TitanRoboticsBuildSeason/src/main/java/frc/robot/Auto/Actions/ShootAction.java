package frc.robot.Auto.Actions;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.Interfaces.*;

public class ShootAction extends Actions{
    private double seconds;
    private double speed;
    Timer timer;

    public ShootAction(double seconds, double speed) {
        this.seconds = seconds;
        this.speed = speed;
    }

    @Override
    public void start() {
        timer = new Timer();
        timer.start();
    }

    @Override
    public void update() {
        
    }

    @Override
    public boolean isFinished() {
        return timer.get() >= seconds;
    }

    @Override
    public void done() {
        timer.stop();
    }
}
