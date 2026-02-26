package frc.robot.Subsystems.intake;

import frc.robot.Interfaces.Subsystem;
import frc.robot.Subsystems.SubsystemManager;
import frc.robot.Devices.NeoSparkMaxMotor;
import frc.robot.Data.Constants;
import frc.robot.Devices.ModifiedEncoder;

import static edu.wpi.first.units.Units.Volt;

import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class IntakeMechanism implements Subsystem {

    private WheelsMotor wheelsMotor;
    private ArmMotor armMotor;
    private String state;
    private double power = 123;

    private ModifiedEncoder pivotEncoder;
    private ProfiledPIDController pivotProfiledPIDController;
    private double kP = 1.0;
    private double kI = 0.0;
    private double kD = 0.25;
    private double kSVolts = 12.19;
    private double kGVolts = 0.48;
    private double kVVolts = 0.0;
    private double kAVolts = 0.1;
    private double currentPosition;
    private double encoderDistancePerRotation = 360;
    private double velocity;
    private double acceleration;

    //the position for the arm motor in angles I need to get
    private double upPosition = 90.0;
    private double downPosition = 0.0;
    private double goal;
    private double startingOffset = 0.0;

    private static IntakeMechanism instance = null;

    public static IntakeMechanism getInstance() {
        if (instance == null) {
            instance = new IntakeMechanism();
        }
        return instance;
    }

    

    private final ArmFeedforward feedforward = new ArmFeedforward(kSVolts, kGVolts, kVVolts, kAVolts);

    private void armControlFunction() {
        pivotProfiledPIDController.setGoal(goal + startingOffset);
        currentPosition = pivotEncoder.getAbsolutePosition();
        armMotor.setVoltage((pivotProfiledPIDController.calculate(currentPosition) + feedforward.calculate(pivotProfiledPIDController.getSetpoint().position, pivotProfiledPIDController.getSetpoint().velocity)));

    }

    public IntakeMechanism() {
        
        SubsystemManager.registerSubsystem(instance);
        armMotor = new ArmMotor(Constants.INTAKE_ARM_MOTOR_ID);
        wheelsMotor = new WheelsMotor(Constants.INTAKE_WHEELS_MOTOR_ID);
        pivotEncoder = new ModifiedEncoder(3);
        pivotProfiledPIDController = new ProfiledPIDController(kP, kI, kD, new TrapezoidProfile.Constraints(velocity, acceleration));
        pivotEncoder.setDistancePerPulse(encoderDistancePerRotation);
    }

    public void setState(String state) {

        this.state = state;
    }


    public void update() {
        switch (state) {
            case "Standby":

            goal = upPosition;
            armControlFunction();
            this.wheelsMotor.set(0);

            break;

            case "Intaking":

            goal = downPosition;
            armControlFunction();
            //set the speed of the wheel motor
            this.wheelsMotor.set(power);

            break;

            case "Reverse":
            
            //The wheels will be reversed in case a fuel is stuck
            goal = downPosition;
            armControlFunction();
            this.wheelsMotor.set(-power);
            
            break;
            
            case "Disabled":

            this.wheelsMotor.set(0);
            this.armMotor.setVoltage(0.0);

            break;
        }
    }

    public void initialize() {

    }

    public void log() {
        SmartDashboard.putNumber("IntakeMechanism/pivotAbsoluteEncoder", currentPosition);
        SmartDashboard.putNumber("goal", goal);
        SmartDashboard.putNumber("voltage",armMotor.getAppliedOutput());
    }

    public boolean isEnabled() {
        return true;
    }

    public String getName() {
        return "IntakeMechanism";
    }

}
