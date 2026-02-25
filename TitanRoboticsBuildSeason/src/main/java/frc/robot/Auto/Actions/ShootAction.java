package frc.robot.Auto.Actions;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.Subsystems.Shooter;
import frc.robot.Interfaces.*;
import edu.wpi.first.math.geometry.Pose2d;

/*
 * Class: ShootAction
 * Description: Uses the state to force the shooter to shoot.
 * Notes: not completed
 * Author: Rhea Sneller
 */

public class ShootAction implements Actions{
    private double seconds;
    Timer timer;
    private Shooter shooter = null;

    public ShootAction(double seconds) {
        this.seconds = seconds;
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
