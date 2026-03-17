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
    private double speed = 0.5;
    
    // Best practice: instantiate the timer once
    private final Timer timer = new Timer(); 
    
    private Shooter shooter;
    private SwerveBase swerveBase;
    private Hopper hopper;

    public ShootAction(double seconds) {
        this.seconds = seconds;
        shooter = Shooter.getInstance();
        swerveBase = SwerveBase.getInstance();
        hopper = Hopper.getInstance();
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
        shooter.setTargetRPM(shootingSolution.flywheelRPM());

        // First check: Is the shot mathematically possible from here?
        if (shootingSolution.shotPossibility()) {
            
            // Second check: Are we pointed at the target within 3 degrees?
            if (Math.abs(shootingSolution.shootingAngle().minus(swerveBase.getHeading()).getDegrees()) < 3) {
                shooter.shoot();
                
                // Third check: Are the flywheels at the target RPM? 
                // If yes, finally run the hopper to feed the game piece!
                if (shooter.isAtCorrectSpeed()) {
                    hopper.setSpeed(-speed);
                } else {
                    hopper.setSpeed(0); 
                }

            } else {
                // If we aren't aligned, spool up but don't feed the note yet
                shooter.prepareToShoot();
                hopper.setSpeed(0);
            }
        } else {
            // If the shot is impossible from this location, do nothing
            shooter.stop();
            hopper.setSpeed(0);
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
        hopper.setSpeed(0);
    }
}