package frc.robot.Subsystems.intake;

import frc.robot.Interfaces.Subsystem;
import frc.robot.Subsystems.SubsystemManager;
import frc.robot.Devices.NeoSparkMaxMotor;
import frc.robot.Data.Constants;
import frc.robot.Data.PortMap;
import frc.robot.Devices.ModifiedEncoder;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class IntakeMechanism implements Subsystem {

    private WheelsMotor wheelsMotor;
    private ArmMotor armMotor;
    private String state = "Disabled";
    private double power = -0.4; 
    private double armVoltage = 0.0;

    private ModifiedEncoder pivotEncoder;
    private ProfiledPIDController pivotProfiledPIDController;
    private double kP = Constants.INTAKE_ARM_KP;
    private double kI = Constants.INTAKE_ARM_KI;
    private double kD = Constants.INTAKE_ARM_KD;
    private double kSVolts = Constants.INTAKE_ARM_KS;
    private double kGVolts = Constants.INTAKE_ARM_KG;
    private double kVVolts = Constants.INTAKE_ARM_KV;
    private double kAVolts = Constants.INTAKE_ARM_KA;
    
    private double currentPosition = 128;
    private double encoderDistancePerRotation = 360; 
    private double unmodifiedAbsolutePosition = 0;

    private double upPosition = Constants.INTAKE_UP_POSITION;
    private double downPosition = Constants.INTAKE_DOWN_POSITION;
    private double goal = Constants.INTAKE_UP_POSITION;
    private double manualPosition;
    
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

        double pidOutput = pivotProfiledPIDController.calculate(currentPosition);
        
        // Feedforward needs the TARGET velocity from the profile, not the actual motor speed
        double targetVelocityRadians = Math.toRadians(pivotProfiledPIDController.getSetpoint().velocity);
        
        // Note: ArmFeedforward assumes 0 radians is exactly horizontal to the floor. 
        // If your 0 position is NOT horizontal, you will need to apply an offset here!
        double ffOutput = feedforward.calculate(
            Math.toRadians(currentPosition - Constants.INTAKE_DOWN_POSITION), 
            targetVelocityRadians
        );

        armVoltage = -(pidOutput + ffOutput);
        armMotor.setVoltage(armVoltage);
    }

    public IntakeMechanism() {
        SubsystemManager.registerSubsystem(this);
        armMotor = new ArmMotor(PortMap.INTAKE_ARM_MOTOR_ID);
        armMotor.setInverted(Constants.INTAKE_ARM_INVERTED);
        armMotor.setBrakeMode(false);
        
        wheelsMotor = new WheelsMotor(PortMap.INTAKE_WHEELS_MOTOR_ID);
        wheelsMotor.setInverted(Constants.INTAKE_WHEELS_INVERTED);
        
        pivotEncoder = new ModifiedEncoder(0);
        pivotProfiledPIDController = new ProfiledPIDController(kP, kI, kD, new TrapezoidProfile.Constraints(Constants.MAX_ARM_VELOCITY, Constants.MAX_ARM_ACCELERATION));
        
        // Crucial: Tell the PID controller that 0 and 360 are the same place!
        pivotProfiledPIDController.enableContinuousInput(0, 360);
        
        pivotEncoder.setDistancePerPulse(encoderDistancePerRotation);
    } 

    public void setState(String state) {
        if (this.state.equals("Disabled") && !state.equals("Disabled")) {
            // Must reset to the modified currentPosition to prevent violent snaps
            pivotProfiledPIDController.reset(currentPosition);
        }
        this.state = state;
    }

    public void manualIntakeControl(double manualInput){ 
        state = "Manual";

        // Correctly maps a [-1, 1] joystick input to the [DOWN, UP] range
        double normalizedInput = (manualInput + 1.0) / 2.0; // scales to [0, 1]
        double modifiedManualPosition = Constants.INTAKE_DOWN_POSITION + (normalizedInput * (Constants.INTAKE_UP_POSITION - Constants.INTAKE_DOWN_POSITION));

        // MathUtil.clamp prevents it from exceeding your upper and lower limits
        this.manualPosition = MathUtil.clamp(modifiedManualPosition, Math.min(Constants.INTAKE_DOWN_POSITION, Constants.INTAKE_UP_POSITION), Math.max(Constants.INTAKE_DOWN_POSITION, Constants.INTAKE_UP_POSITION));
    }

    public void update() {
        unmodifiedAbsolutePosition = pivotEncoder.getAbsolutePosition();
        
        // Apply offset and ensure the result stays cleanly mapped between 0 and 360
        currentPosition = MathUtil.inputModulus(unmodifiedAbsolutePosition - Constants.INTAKE_POSITION_OFFSET, 0, 360);

        switch (state) {
            case "Standby":
                goal = upPosition;
                armControlFunction();
                this.wheelsMotor.set(0);
                break;

            case "StandbyIntaking":
                goal = upPosition;
                armControlFunction();
                this.wheelsMotor.set(power);
                break;

            case "StandbyReversed":
                goal = upPosition;
                armControlFunction();
                this.wheelsMotor.set(-power);
                break;

            case "Intaking":
                goal = downPosition;
                armControlFunction();
                this.wheelsMotor.set(power);
                break;

            case "Down":
                goal = downPosition;
                armControlFunction();
                this.wheelsMotor.set(0);
                break;

            case "Reversed":
                goal = downPosition;
                armControlFunction();
                this.wheelsMotor.set(-power);
                break;

            case "Manual":
                goal = manualPosition;
                armControlFunction();
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