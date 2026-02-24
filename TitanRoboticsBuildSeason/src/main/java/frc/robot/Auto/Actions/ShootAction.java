package frc.robot.Auto.Actions;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.Subsystems.Shooter;
import frc.robot.Interfaces.*;
import edu.wpi.first.math.geometry.Pose2d;

public class ShootAction implements Actions{
    private double seconds;
    private double speed;
    Pose2d robotPose;
    Timer timer;
    private Shooter shooter = null;

    public ShootAction(double seconds, Pose2d robotPose) {
        this.seconds = seconds;
        this.robotPose = robotPose;
        shooter = Shooter.getInstance();
    }

    @Override
    public void start() {
        timer = new Timer();
        timer.start();
    }

    @Override
    public void update() {
        shooter.shoot();
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
