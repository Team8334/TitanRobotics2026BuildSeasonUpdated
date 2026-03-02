package frc.robot.Data;

import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.util.Units;
import swervelib.math.Matter;

public class Constants {
    public static final String MODE = "DEV";   // valid values: DEV or LIVE

    public static final int INTAKE_ARM_MOTOR_ID = 12;
    public static final int INTAKE_WHEELS_MOTOR_ID = 2;

    public static final double ROBOT_MASS = (148 - 20.3) * 0.453592; // 32lbs * kg per pound
    public static final Matter CHASSIS = new Matter(new Translation3d(0, 0, Units.inchesToMeters(8)), ROBOT_MASS);
    public static final double LOOP_TIME = 0.13; // s, 20ms + 110ms sprk max velocity lag
    public static final double MAX_SPEED = Units.feetToMeters(15);
    public static final double MAX_ROTATION_SPEED = 10;


    public static final Translation3d RED_HUB_LOCATION = new Translation3d(11.938, 4.035, 1.829);
    public static final Translation3d BLUE_HUB_LOCATION = new Translation3d(4.597, 4.035, 1.829);

    public static final double SHOOTER_OFFSET = 0.0;
    public static final double GRAVITY = 9.8;
    public static final double FIRING_ANGLE = Units.degreesToRadians(75); //75 is a random number, please change when we know our firing angle. delete this comment after.
    public static final double HEIGHT_DIFFERENCE = RED_HUB_LOCATION.getZ()-0.53; //in meters. meters > imperial system
    public static final double FLYWHEEL_CIRCUMFENCE = 0.1016*Math.PI; //0.1 is random. in meters. 
    public static final double kFLYWHEELs = 0.0;
    public static final double kFLYWHEELv = 0.40;
    public static final double kFLYWHEELa = 0.0;
    public static final double kFLYWHEELp = 0.01;
    public static final double KICKERMOTOR = 0.5; 
    public static final double MINIMUMMOTORSPEEDTOSHOOT = 0.5;

    public static final double VELOCITY_TRANSFER_PARANOIA = 0.5;

    // Maximum speed of the robot in meters per second, used to limit acceleration.

    // public static final class AutonConstants
    // {
    //
    // public static final PIDConstants TRANSLATION_PID = new PIDConstants(0.7, 0,
    // 0);
    // public static final PIDConstants ANGLE_PID = new PIDConstants(0.4, 0, 0.01);
    // }

    public static final class DrivebaseConstants {

        // Hold time on motor brakes when disabled
        public static final double WHEEL_LOCK_TIME = 10; // seconds
    }

    public static class OperatorConstants {

        // Joystick Deadband
        public static final double DEADBAND = 0.1;
        public static final double LEFT_Y_DEADBAND = 0.1;
        public static final double RIGHT_X_DEADBAND = 0.1;
        public static final double TURN_CONSTANT = 6;
    }
}