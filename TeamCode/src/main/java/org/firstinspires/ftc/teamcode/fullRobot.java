package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@TeleOp(name = "fullrobot")
public class fullRobot extends LinearOpMode {
    Limelight3A limelight;

    private DcMotor leftFront;
    private DcMotor leftBack;
    private DcMotor rightFront;
    private DcMotor rightBack;
    private DcMotor intake;
    private DcMotor feeder;
    private DcMotorEx LeftShooter;
         private DcMotorEx RightShooter;
    public double highVelocity = 1265;

    public double lowVelocity = 900;
    public double superlowVelocity = 800;

    double curTargetVelocity = highVelocity;

    double F = 0;

    double P = 0;

    double[] stepSizes = {10.0, 1.0, 0.1, 0.001, 0.0001};

    int stepIndex=1;

    @Override
    public void runOpMode() {
        // Initialize hardware components
  leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        intake = hardwareMap.get(DcMotor.class, "intake");
        feeder = hardwareMap.get(DcMotor.class, "feeder");
        LeftShooter = hardwareMap.get(DcMotorEx.class, "LeftShooter");
        RightShooter = hardwareMap.get(DcMotorEx.class, "RightShooter");

        limelight.setPollRateHz(100); // This sets how often we ask Limelight for data (100 times per second)
        limelight.start();
        limelight.pipelineSwitch(0);

        boolean autoTurn;

        leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftFront.setPower(0.0);
        leftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setPower(0.0);
        rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setPower(0.0);     // The right-side motors are mounted facing the opposite direction, so we reverse
        rightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);   // them so that positive power always means "drive forward" on both sides.
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setPower(0.0);
        rightBack.setDirection(DcMotor.Direction.REVERSE);
        LeftShooter.setDirection(DcMotorSimple.Direction.REVERSE);
        RightShooter.setDirection(DcMotorSimple.Direction.REVERSE);
        LeftShooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RightShooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P,0,0,F);
        LeftShooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
RightShooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        double startTime = 0;
        autoTurn = false;

        waitForStart();
        while (opModeIsActive()) {

            double forwardSpeed = -gamepad1.left_stick_y;


            double turnSpeed = gamepad1.right_stick_x;

            double leftPower = forwardSpeed + turnSpeed;

            double rightPower = forwardSpeed - turnSpeed;
           leftFront.setPower(leftPower);
            leftBack.setPower(leftPower);
            //   Set the power of BOTH right motors to rightPower
            rightFront.setPower(rightPower);
            rightBack.setPower(rightPower);
            RightShooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,pidfCoefficients);
            LeftShooter.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,pidfCoefficients);

            //set velocity
           RightShooter.setVelocity(curTargetVelocity);
            LeftShooter.setVelocity(curTargetVelocity);

            double curvelocityRight = RightShooter.getVelocity();
            double curvelocityLeft = LeftShooter.getVelocity();

            double righterror = curTargetVelocity - curvelocityRight;
            double lefterror = curTargetVelocity - curvelocityLeft;
            if (gamepad1.dpadLeftWasPressed()){
                F -= stepSizes[stepIndex];
            }

            if (gamepad1.dpadRightWasPressed()){
                F += stepSizes[stepIndex];
            }
            if (gamepad1.dpadUpWasPressed()){
                P += stepSizes[stepIndex];
            }
            if (gamepad1.dpadDownWasPressed()){
                P-= stepSizes[stepIndex];
            }
           if (gamepad1.right_trigger>.1) {
               intake.setPower(1);
               feeder.setPower(1);

           }
           if (gamepad1.left_trigger>.1) {
               intake.setPower(1);
               feeder.setPower(-1);
           }
           if (gamepad1.right_bumper) {
               intake.setPower(-1);
               feeder.setPower(-1);
           }



            telemetry.addData("F", F);
            telemetry.addData("P", P);
            telemetry.addData("curvelocityRight", curvelocityRight);
            telemetry.addData("curvelocityLeft", curvelocityLeft);
telemetry.addData("righterror", righterror);
telemetry.addData("lefterror", lefterror);
telemetry.addData("curTargetVelocity", curTargetVelocity);

            //   LLResult result = limelight.getLatestResult();
            //if (result != null && result.isValid()) {
              //  double tx = result.getTx(); // How far left or right the target is (degrees)
               // double ta = result.getTa(); // How big the target looks (0%-100% of the image)

              //  telemetry.addData("tx: ", tx);
               // telemetry.addData("ta: ", ta);

                //double kP = 0.02;
               // double kF = 0.05;
             //   double kFThreshold = 0.5;


              //  if (gamepad1.right_bumper) {
                   // turnSpeed = (tx - 0) * kP;
                  //  if ((tx - 0) > kFThreshold) {
                 //       turnSpeed += kF;
                 //   }
                   // if ((tx - 0) < -kFThreshold) {
                  //      turnSpeed -= kF;
                    //}
                }






        }
    }
    //   Set the power of BOTH left motors to leftPower







