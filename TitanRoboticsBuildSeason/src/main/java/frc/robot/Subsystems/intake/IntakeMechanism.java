package frc.robot.Subsystems.intake;

import frc.robot.Interfaces.Subsystem;
import frc.robot.Subsystems.SubsystemManager;
import frc.robot.Devices.NeoSparkMaxMotor;
import frc.robot.Data.Constants;
import frc.robot.Data.PortMap;
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
    private String state = "Disabled";
    private double power = 0.0; //make this not 0 after testing
    private double armVoltage = 0.0;

    private ModifiedEncoder pivotEncoder;
    private ProfiledPIDController pivotProfiledPIDController;
    private double kP = 0.1;
    private double kI = 0.0;
    private double kD = 0.0;
    private double kSVolts = 0; //was previously 12.19, changed for debugging
    private double kGVolts = 0;//0.44;
    private double kVVolts = 0.0;
    private double kAVolts = 0.0;
    private double currentPosition = 128;
    private double encoderDistancePerRotation = 360; 

    //the position for the arm motor in angles I need to get
    private double upPosition = Constants.INTAKE_UP_POSITION;
    private double downPosition = Constants.INTAKE_DOWN_POSITION;
    private double goal = 128;
    
    private static IntakeMechanism instance = null;

    public static IntakeMechanism getInstance() {
        if (instance == null) {
            instance = new IntakeMechanism();
        }
        return instance;
    }

    private final ArmFeedforward feedforward = new ArmFeedforward(kSVolts, kGVolts, kVVolts, kAVolts);

    private void armControlFunction() {
        pivotProfiledPIDController.setGoal(goal);

        //un-comment this for armMotor
        armVoltage = -(pivotProfiledPIDController.calculate(currentPosition));// + feedforward.calculate(pivotProfiledPIDController.getSetpoint().position, pivotProfiledPIDController.getSetpoint().velocity));
        armMotor.setVoltage(armVoltage);
    }

    public IntakeMechanism() {
        
        SubsystemManager.registerSubsystem(this);
        armMotor = new ArmMotor(PortMap.INTAKE_ARM_MOTOR_ID);
        armMotor.setInverted(Constants.INTAKE_ARM_INVERTED);
        
        //un-comment wheels motor so that intake works
        wheelsMotor = new WheelsMotor(PortMap.INTAKE_WHEELS_MOTOR_ID);
        wheelsMotor.setInverted(Constants.INTAKE_WHEELS_INVERTED);
        
        pivotEncoder = new ModifiedEncoder(2);
        pivotProfiledPIDController = new ProfiledPIDController(kP, kI, kD, new TrapezoidProfile.Constraints(Constants.MAX_ARM_VELOCITY, Constants.MAX_ARM_ACCELERATION));
        pivotEncoder.setDistancePerPulse(encoderDistancePerRotation);
    }

    public void setState(String state) {
        if (this.state.equals("Disabled") && !state.equals("Disabled")) {
            // Reset the profiled PID controller's internal setpoint to the current physical position
            // This prevents the arm from violently snapping to its 0 initialization state on first deploy
            pivotProfiledPIDController.reset(pivotEncoder.getAbsolutePosition());
        }
        this.state = state;
    }


    public void update() {

        currentPosition = pivotEncoder.getAbsolutePosition();

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

            case "Reversed":
            
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
        SmartDashboard.putNumber("Intake/Pivot Position", currentPosition);
        SmartDashboard.putNumber("Intake/Pivot Goal", goal);
        SmartDashboard.putNumber("Intake/Arm Applied Output", armMotor.getAppliedOutput());
        SmartDashboard.putNumber("Intake/Arm Speed", armMotor.getSpeed());
        SmartDashboard.putNumber("Intake/Wheels Applied Output", wheelsMotor.getAppliedOutput());
        SmartDashboard.putString("Intake/State", state);
        SmartDashboard.putNumber("Intake/Setpoint Position", pivotProfiledPIDController.getSetpoint().position);
        SmartDashboard.putNumber("Intake/Setpoint Velocity", pivotProfiledPIDController.getSetpoint().velocity);
        SmartDashboard.putNumber("Intake/Arm Voltage", armVoltage);
    }

    public boolean isEnabled() {
        return true;
    }

    public String getName() {
        return "IntakeMechanism";
    }

}
