/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.security.acl.Group;
import java.util.ResourceBundle.Control;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  boolean stage1;
  String colorString;

  AHRS ahrs = new AHRS(Port.kMXP);

  Joystick j = new Joystick(0);

  I2C.Port rioI2C = I2C.Port.kOnboard;
  ColorSensorV3 colorSensor = new ColorSensorV3(rioI2C);
  ColorMatch colorMatcher = new ColorMatch();
  private final Color kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);
  private final Color kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);
  private final Color kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);
  private final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);
  Color detectedColor;

  WPI_VictorSPX frontLeft = new WPI_VictorSPX(2);
  WPI_VictorSPX backLeft = new WPI_VictorSPX(4);
  SpeedControllerGroup left = new SpeedControllerGroup(frontLeft, backLeft);

  WPI_VictorSPX frontRight = new WPI_VictorSPX(3);
  WPI_VictorSPX backRight = new WPI_VictorSPX(5);
  SpeedControllerGroup right = new SpeedControllerGroup(frontRight, backRight);

  WPI_VictorSPX shooter = new WPI_VictorSPX(6);
  WPI_VictorSPX setter = new WPI_VictorSPX(7);
  WPI_VictorSPX intake = new WPI_VictorSPX(8);
  WPI_VictorSPX revolver = new WPI_VictorSPX(9);

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    revolver.setNeutralMode(NeutralMode.Brake);
    
    colorMatcher.addColorMatch(kBlueTarget);
    colorMatcher.addColorMatch(kGreenTarget);
    colorMatcher.addColorMatch(kRedTarget);
    colorMatcher.addColorMatch(kYellowTarget);   
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable chooser
   * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
   * remove all of the chooser code and uncomment the getString line to get the
   * auto name from the text box below the Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure below with additional strings. If using the SendableChooser
   * make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
    case kCustomAuto:
      // Put custom auto code here
      break;
    case kDefaultAuto:
    default:
      // Put default auto code here
      break;
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    detectedColor = colorSensor.getColor();
    ColorMatchResult match = colorMatcher.matchClosestColor(detectedColor);

    if(j.getRawButton(2)) {
      revolver.set(ControlMode.PercentOutput, -0.2);
    } else if (match.color == kRedTarget) {
      revolver.set(ControlMode.PercentOutput, 0);
    }

    /**if (stage1) {
      if (match.color == red) {
        revolver.set(ControlMode.PercentOutput, 0);
        stage1 = false;
      }
      else {
        revolver.set(ControlMode.PercentOutput, -0.15);
      }
    } else if (j.getRawButton(2)) {
      revolver.set(ControlMode.PercentOutput, -0.15);
      stage1 = true;
    } else {
      revolver.set(ControlMode.PercentOutput, 0);
    }**/

    if(j.getRawButton(1)) {
      setter.set(ControlMode.PercentOutput, 1);
      shooter.set(ControlMode.PercentOutput, 1);
    } else{
      setter.set(ControlMode.PercentOutput, 0);
      shooter.set(ControlMode.PercentOutput, 0);
    }

    if(j.getRawButton(3)) {
      intake.set(ControlMode.PercentOutput, -0.75);
    } else{
      intake.set(ControlMode.PercentOutput, 0);
    }

    left.set(-j.getY() + j.getZ()*0.25);
    right.set(j.getY() + j.getZ()*0.25);

    if (match.color == kBlueTarget) {
      colorString = "Blue";
    } else if (match.color == kRedTarget) {
      colorString = "Red";
    } else if (match.color == kGreenTarget) {
      colorString = "Green";
    } else if (match.color == kYellowTarget) {
      colorString = "Yellow";
    } else {
      colorString = "Unknown";
    }

    SmartDashboard.putNumber("Red", detectedColor.red);
    SmartDashboard.putNumber("Green", detectedColor.green);
    SmartDashboard.putNumber("Blue", detectedColor.blue);
    SmartDashboard.putNumber("Confidence", match.confidence);
    SmartDashboard.putString("Detected Color", colorString);
}

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
