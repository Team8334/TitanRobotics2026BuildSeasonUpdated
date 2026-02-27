package frc.robot.Auto.Actions;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.Subsystems.Shooter;
<<<<<<< Updated upstream
=======
import frc.robot.Subsystems.SwerveBase;
import frc.robot.Subsystems.Shooter.ShootingSolution;
>>>>>>> Stashed changes
import frc.robot.Interfaces.*;
import edu.wpi.first.math.geometry.Pose2d;

/*
 * Class: ShootAction
 * Description: Uses the state to force the shooter to shoot.
 * Notes: not completed
 * Author: Rhea Sneller
 */

<<<<<<< Updated upstream
public class ShootAction implements Actions{
    private double seconds;
    Timer timer;
    private Shooter shooter = null;

    public ShootAction(double seconds) {
        this.seconds = seconds;
        shooter = Shooter.getInstance();
=======
public class ShootAction implements Actions {
    private double seconds;
    Timer timer;
    private Shooter shooter = null;
    private SwerveBase swerveBase;
    private Pose2d robotPose;
    private ShootingSolution shootingSolution;

    public ShootAction(double seconds, Pose2d robotPose) {
        this.seconds = seconds;
        shooter = Shooter.getInstance();
        swerveBase = SwerveBase.getInstance();
>>>>>>> Stashed changes
    }

    @Override
    public void start() {
        timer = new Timer();
        timer.start();
    }

    @Override
    public void update() {
        shooter.shoot();
<<<<<<< Updated upstream
=======
        shooter.calculateShootingSolution(robotPose);
        shootingSolution = shooter.calculateShootingSolution(swerveBase.getPose());
        shooter.setTargetRPM(shootingSolution.flywheelRPM());
        if (shootingSolution.shotPossibilty()) {
            if (Math.abs(shootingSolution.shootingAngle().minus(swerveBase.getHeading()).getDegrees()) < 3) {

                shooter.shoot();

            } else {
                shooter.prepareToShoot();

            }
        }

>>>>>>> Stashed changes
    }

    @Override
    public boolean isFinished() {
        return timer.get() >= seconds;
    }

    @Override
    public void done() {
        timer.stop();
<<<<<<< Updated upstream
    }
=======
        shooter.stop();
    }

>>>>>>> Stashed changes
}
