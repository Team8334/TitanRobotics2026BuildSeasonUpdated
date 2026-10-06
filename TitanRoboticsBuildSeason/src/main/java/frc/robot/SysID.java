package frc.robot;

import static edu.wpi.first.units.Units.Volts;
import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.RotationsPerSecond;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.Subsystems.Shooter;

public class SysID {
    private final SysIdRoutine m_routine;
    private final Shooter m_shooter;
    private final Subsystem m_dummySubsystem = new Subsystem() {};

    public SysID(Shooter shooter) {
        this.m_shooter = shooter;

        m_routine = new SysIdRoutine(
            new SysIdRoutine.Config(),
            new SysIdRoutine.Mechanism(
                // Voltage consumer
                (volts) -> {
                    m_shooter.shooterMotorLeft.setVoltage(volts.in(Volts));
                    m_shooter.shooterMotorRight.setVoltage(volts.in(Volts));
                },

                // Logging consumer
                (log) -> {
                    log.motor("flywheel-left")
                        .voltage(
                            Volts.of(
                                m_shooter.shooterMotorLeft.getBusVoltage()
                                * m_shooter.shooterMotorLeft.getAppliedOutput()
                            )
                        )
                        .angularVelocity(
                            RotationsPerSecond.of(
                                m_shooter.shooterMotorLeft.getSpeed() / 60.0
                            )
                        )
                        .angularPosition(
                            Rotations.of(m_shooter.shooterMotorLeft.getPosition())
                        );

                    log.motor("flywheel-right")
                        .voltage(
                            Volts.of(
                                m_shooter.shooterMotorRight.getBusVoltage()
                                * m_shooter.shooterMotorRight.getAppliedOutput()
                            )
                        )
                        .angularVelocity(
                            RotationsPerSecond.of(
                                m_shooter.shooterMotorRight.getSpeed() / 60.0
                            )
                        );
                },
                m_dummySubsystem
            )
        );
    }

    public void runTest(XboxController controller) {
        if (controller.getAButton()) {
            m_routine.quasistatic(Direction.kForward).schedule();
        } else if (controller.getBButton()) {
            m_routine.quasistatic(Direction.kReverse).schedule();
        } else if (controller.getYButton()) {
            m_routine.dynamic(Direction.kForward).schedule();
        } else if (controller.getXButton()) {
            m_routine.dynamic(Direction.kReverse).schedule();
        }
    }
}