package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;


@TeleOp(name = "drive (Blocks to Java)")
public class drive extends LinearOpMode {
Limelight3A limelight;

  private DcMotor rightFront;
  private DcMotor rightBack;
  private DcMotor LeftShooter;
  private DcMotor RightShooter;
  private Servo Prism;
  private DcMotor leftBack;
  private DcMotor leftFront;
  private DcMotor feeder;
  private DcMotor intake;
  // create motor variiables
  private int shooterVelo = 1075;
  double[] stepSizes = {10.0, 1.0, 0.1, 0.001, 0.0001};
  int stepIndex = 10;
  //create normal shooter speed

  private boolean shooterSwitch = false;

  /**
   * This sample contains the bare minimum Blocks for any regular OpMode. The 3 blue
   * Comment Blocks show where to place Initialization code (runs once, after touching the
   * DS INIT button, and before touching the DS Start arrow), Run code (runs once, after
   * touching Start), and Loop code (runs repeatedly while the OpMode is active, namely not
   * Stopped).
   */
  @Override
  public void runOpMode() {
    float rx;
    float turn;
    float foward;
    //create drive variables
    limelight = hardwareMap.get(Limelight3A.class, "limelight");
    limelight.setPollRateHz(100); // This sets how often we ask Limelight for data (100 times per second)
    limelight.start();

    rightFront = hardwareMap.get(DcMotor.class, "rightFront");
    rightBack = hardwareMap.get(DcMotor.class, "rightBack");
    LeftShooter = hardwareMap.get(DcMotor.class, "LeftShooter");
    RightShooter = hardwareMap.get(DcMotor.class, "RightShooter");
    Prism = hardwareMap.get(Servo.class, "Prism");
    leftBack = hardwareMap.get(DcMotor.class, "leftBack");
    leftFront = hardwareMap.get(DcMotor.class, "leftFront");
    feeder = hardwareMap.get(DcMotor.class, "feeder");
    intake = hardwareMap.get(DcMotor.class, "intake");
    //get mortor names from robotz
    intake.setDirection(DcMotor.Direction.REVERSE);
    feeder.setDirection(DcMotor.Direction.REVERSE);
    rightFront.setDirection(DcMotor.Direction.REVERSE);
    rightBack.setDirection(DcMotor.Direction.REVERSE);
    LeftShooter.setDirection(DcMotor.Direction.REVERSE);
    LeftShooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    RightShooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    ((DcMotorEx) LeftShooter).setVelocityPIDFCoefficients(150, 0, 0, 20);
    ((DcMotorEx) RightShooter).setVelocityPIDFCoefficients(150, 0, 0, 20);
    //intalize shooter with pidf
    telemetry.setMsTransmissionInterval(500);
    // Init
    waitForStart();
    if (opModeIsActive()) {
      while (opModeIsActive()) {
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
          double tx = result.getTx(); // How far left or right the target is (degrees)
          double ty = result.getTy(); // How far up or down the target is (degrees)
          double ta = result.getTa(); // How big the target looks (0%-100% of the image)

          telemetry.addData("Target X", tx);
          telemetry.addData("Target Y", ty);
          telemetry.addData("Target Area", ta);
        } else {
          telemetry.addData("Limelight", "No Targets");
        }
        // Drive
        rx = gamepad1.left_stick_x;
        turn = gamepad1.right_stick_x;
        foward = gamepad1.left_stick_y;
        Prism.setPosition(0.223);
        leftBack.setPower(foward - (turn - rx));
        leftFront.setPower(foward - (turn + rx));
        rightBack.setPower(foward + (turn - rx));
        rightFront.setPower(foward + turn + rx);
// set motor power to variable from gamepad
        // Shooter
        if (gamepad1.rightBumperWasPressed()) {
            shooterSwitch = !shooterSwitch;
        }
        if (shooterSwitch){
          ((DcMotorEx) LeftShooter).setVelocity(shooterVelo);
          ((DcMotorEx) RightShooter).setVelocity(shooterVelo);
        } else {
          ((DcMotorEx) LeftShooter).setVelocity(0);
          ((DcMotorEx) RightShooter).setVelocity(0);
        }
        // Feeder
        if (gamepad1.left_bumper) {//turns on intake to intake
        feeder.setPower(.6);
        intake.setPower(-.6);

        } else {
          intake.setPower(-gamepad1.left_trigger*.7);
   feeder.setPower(0);
        }
        if (gamepad1.dpadUpWasPressed()){
          shooterVelo += 10;
       } else if (gamepad1.dpadDownWasPressed()){
          shooterVelo -= 10;
      }

        //manuel offset for shooter speed
        // Telemetry
        telemetry.addData("Left Shooter",((DcMotorEx) LeftShooter).getVelocity());
        telemetry.addData("Right Shooter",((DcMotorEx) RightShooter).getVelocity());
        telemetry.addData("Target Velocity", shooterVelo);
        telemetry.addData("Is shooter enabled?", shooterSwitch);
        telemetry.update();
      }
    }
  }
}
