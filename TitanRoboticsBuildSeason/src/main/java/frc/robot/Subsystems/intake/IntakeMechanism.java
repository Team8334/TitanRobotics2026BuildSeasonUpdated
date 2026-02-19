package frc.robot.Subsystems.intake;

import frc.robot.Devices.NeoSparkMaxMotor;
import frc.robot.Data.Constants;
import frc.robot.Interfaces.Subsystem;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class IntakeMechanism implements Subsystem {

    private XboxController xboxController;
    private ArmMotor armMotor;
    private WheelsMotor wheelsMotor;
    private String state;
    private double speed;

    private ModifiedMotors pivotMotor;
    private ModifiedEncoders pivotEncoder;
    private ProfiledPIDController pivotProfiledPIDController;
    private double kP = 0.1;
    private double kI = 0.0;
    private double kD = 0.0;
    private double kSVolts = 0.0;
    private double kGVolts = 0.0;
    private double kVVolts = 0.0;
    private double kAVolts = 0.0;
    private double currentPosition;

    public IntakeMechanism() {
        this(Constants.INTAKE_ARM_MOTOR_ID, Constants.INTAKE_WHEELS_MOTOR_ID);
    }
    public IntakeMechanism(int armCANID, int wheelsCANID) {
        this.xboxController = new XboxController(0);
        this.armMotor = new ArmMotor(armCANID);
        this.wheelsMotor = new WheelsMotor(wheelsCANID);
    }

    public void GetwheelsMotorSpeed() {

        if (this.xboxController.getAButtonPressed() == true) {
            // button pressed
            this.wheelsMotor.setSpeed(54354);
        } else {
            // button not pressed
            this.wheelsMotor.setSpeed(0);
        }

    }

    private final ArmFeedforward feedforward = new ArmFeedforward(kSVolts, kGVolts, kVVolts, kAVolts);

    private void armControlFunction() {
        pivotProfiledPIDController.setGoal(goal + startingOffset);

        pivotMotor.setVoltage((pivotProfiledPIDController.calculate(currentPosition) + feedforward.calculate(pivotProfiledPIDController.getSetpoint().position, pivotProfiledPIDController.getSetpoint().velocity)));

    }
    public void update() {
        switch (state) {
            case "Standby":
            this.wheelsMotor.setSpeed(speed = 0);

            break;
            case "Intaking":
            this.wheelsMotor.setSpeed(speed);

            break;
            case "Reverse":
            this.wheelsMotor.setSpeed(speed = speed * -1);
            
            break;
            case "Stop":
            this.wheelsMotor.setSpeed(speed = 0);
        }
    }

    public void initialize() {

    }

    public void log() {

    }

    public boolean isEnabled() {
        return true;
    }

    public String getName() {
        return "IntakeMechanism";
    }

}
