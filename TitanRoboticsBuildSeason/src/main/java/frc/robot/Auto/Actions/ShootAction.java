package frc.robot.Auto.Actions;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.Subsystems.Shooter;
import frc.robot.Subsystems.SwerveBase;
import frc.robot.Subsystems.intake.*;
import frc.robot.Subsystems.Shooter.ShootingSolution;
import frc.robot.Interfaces.*;

/*
 * Class: ShootAction
 * Description: Uses the state to force the shooter to shoot.
 * Author: Rhea, Sarah 
 */

public class ShootAction implements Actions {
    private double seconds;
    
    // Best practice: instantiate the timer once
    private final Timer timer = new Timer(); 
    
    private Shooter shooter;
    private SwerveBase swerveBase;

    public ShootAction(double seconds) {
        this.seconds = seconds;
        shooter = Shooter.getInstance();
        swerveBase = SwerveBase.getInstance();
    }

    @Override
    public void start() {
        // restart() clears any previous time and starts it fresh
        timer.restart(); 
    }

    @Override
    public void update() {
        // Correctly get the pose straight from swerve every loop
        ShootingSolution shootingSolution = shooter.calculateShootingSolution(swerveBase.getPose());
        shooter.setTargetRPM(shootingSolution.flywheelRpmLeft(), shootingSolution.flywheelRpmRight());

        // First check: Is the shot mathematically possible from here?
        if (shootingSolution.shotPossibility()) {
            
            // Second check: Are we pointed at the target within 3 degrees?
            if (Math.abs(shootingSolution.shootingAngle().minus(swerveBase.getHeading()).getDegrees()) < 3) {
                shooter.shoot();
            } 
            else {
                // If we aren't aligned, spool up but don't feed the note yet
                shooter.prepareToShoot();
            }
        } else {
            // If the shot is impossible from this location, do nothing
            shooter.stop();
        }
    }

    @Override
    public boolean isFinished() {
        return timer.get() >= seconds;
    }

    @Override
    public void done() {
        timer.stop();
        shooter.stop();
    }
}