package frc.robot.Auto.Actions;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.Subsystems.Shooter;
import frc.robot.Subsystems.SwerveBase;
import frc.robot.Subsystems.intake.*;
import frc.robot.Subsystems.Shooter.ShootingSolution;
import frc.robot.Interfaces.*;
import edu.wpi.first.math.geometry.Pose2d;

/*
 * Class: ShootAction
 * Description: Uses the state to force the shooter to shoot.
 * Author: Rhea, Sarah 
 */

public class ShootAction implements Actions {
    private double seconds;
    private double targetRPM;
    private double speed = 0.5;
    Timer timer;
    private Shooter shooter = null;
    private SwerveBase swerveBase;
    private Pose2d robotPose;
    private ShootingSolution shootingSolution;
    private Hopper hopper;

    public ShootAction(double seconds, double targetRPM) {
        this.seconds = seconds;
        this.targetRPM = targetRPM;
        shooter = Shooter.getInstance();
        swerveBase = SwerveBase.getInstance();
        hopper = Hopper.getInstance();
    }

    @Override
    public void start() {
        timer = new Timer();
        timer.start();
    }

    @Override
    public void update() {
        //hopper.setSpeed(-speed);
        shooter.manualSpeedAuto(targetRPM);
    }

    @Override
    public boolean isFinished() {
        return timer.get() >= seconds;
    }

    @Override
    public void done() {
        timer.stop();
        shooter.stop();
        //hopper.setSpeed(0);
    }

}
