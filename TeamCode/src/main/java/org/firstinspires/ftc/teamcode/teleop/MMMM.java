package org.firstinspires.ftc.teamcode.teleop;

import static org.firstinspires.ftc.teamcode.pedroPathing.tuning.FollowerConstants.leftFrontMotorName;
import static org.firstinspires.ftc.teamcode.pedroPathing.tuning.FollowerConstants.leftRearMotorName;
import static org.firstinspires.ftc.teamcode.pedroPathing.tuning.FollowerConstants.rightFrontMotorName;
import static org.firstinspires.ftc.teamcode.pedroPathing.tuning.FollowerConstants.rightRearMotorName;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.config.BetterBoolGamepad;
import org.firstinspires.ftc.teamcode.config.Intake;
import org.firstinspires.ftc.teamcode.config.Slide;
import org.firstinspires.ftc.teamcode.pedroPathing.follower.Follower;

@Config
@TeleOp(name = "MMMM", group = "Drive")
public class MMMM extends OpMode {

    Follower follower;
    Slide slide;
    Intake intake;
    private DcMotorEx leftFront;
    private DcMotorEx leftRear;
    private DcMotorEx rightFront;
    private DcMotorEx rightRear;

    BetterBoolGamepad bGamepad1, bGamepad2;
    public double speedMod = 0.5;
    public double speedModTwo = 0.25;
    public double speedModTurn = 0.5;

    public int intakeCycleDirection = 1;
    public static int flipDrive = -1;

    @Override
    public void init() {

        follower = new Follower(hardwareMap);

        leftFront = hardwareMap.get(DcMotorEx.class, leftFrontMotorName);
        leftRear = hardwareMap.get(DcMotorEx.class, leftRearMotorName);
        rightRear = hardwareMap.get(DcMotorEx.class, rightRearMotorName);
        rightFront = hardwareMap.get(DcMotorEx.class, rightFrontMotorName);

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        Slide slide = new Slide(hardwareMap);
        Intake intake = new Intake(hardwareMap);

        follower.startTeleopDrive();

        bGamepad2 = new BetterBoolGamepad(gamepad2);
        bGamepad1 = new BetterBoolGamepad(gamepad1);

    }

    @Override
    public void loop() {
        //Follower drive = new Follower(hardwareMap);
        Slide slide = new Slide(hardwareMap);
        Intake intake = new Intake(hardwareMap);

        if (gamepad1.right_bumper) {
            speedMod = 0.25;
            speedModTurn = 0.25;
        }
        else {
            if (gamepad1.right_trigger > 0.5) speedMod = gamepad1.right_trigger;
            else if (speedMod < 0.5) {
                speedMod = 0.5;
            }
            if (gamepad1.left_trigger > 0.5) speedModTurn = gamepad1.left_trigger;
            else if (speedModTurn < 0.5) {
                speedModTurn = 0.5;
            }
        }
        if (bGamepad1.a()) flipDrive = -1;
        if (bGamepad1.b()) flipDrive = 1;
        if (gamepad2.dpad_left) {intake.claw.setPosition(intake.clawPosition);}

        speedMod = gamepad1.right_bumper ? 0.25 : (gamepad1.right_trigger>0.5 ? gamepad1.right_trigger : 0.5);

        follower.setTeleOpMovementVectors(
                        gamepad1.right_stick_y * speedMod * flipDrive,
                        gamepad1.right_stick_x * speedMod * flipDrive,
                        gamepad1.left_stick_x * speedModTurn * flipDrive
        );
        follower.update();

        if (gamepad2.right_bumper && !gamepad2.left_bumper) {intakeCycleDirection = 1;}
        if (!gamepad2.right_bumper && gamepad2.left_bumper) {intakeCycleDirection = -1;}
        if (!gamepad2.right_bumper && !gamepad2.left_bumper) {intakeCycleDirection = 0;}

        if (gamepad2.dpad_left && !gamepad2.dpad_right) {intake.setClaw(Intake.clawClosed);}
        if (!gamepad2.dpad_left && gamepad2.dpad_right) {intake.setClaw(Intake.clawOpen);}
        if (!gamepad2.dpad_left && !gamepad2.dpad_right) {intake.setClaw(intake.claw.getPosition());}

        //Davids Control
        //if (gamepad2.right_trigger >= 0.25) {intakeCycleDirection = 1;}
        //if (gamepad2.left_trigger >= 0.25) {intakeCycleDirection = -1;}
        //if (gamepad2.right_trigger <= 0.25 && gamepad2.left_trigger <= 0.25) {intakeCycleDirection = 0;}

        intake.cycleIntake(intakeCycleDirection);

        //slide.setCentralLift(-gamepad2.left_stick_y);
        intake.cycleGearRack(-gamepad2.right_stick_y);

        if(gamepad2.y) {intake.setWrist(intake.wristDrop);}
        if (gamepad2.a) {intake.setWrist(intake.wristFlat);}
        if (gamepad2.b) {intake.setWrist(intake.wristBack);}


        if (Math.abs(gamepad2.left_stick_y) < 0) {
            if (gamepad2.dpad_right) {
                slide.HighBasket();
            }
            if (gamepad2.dpad_left) {
                slide.Base();
            }
        }
        else {
            slide.setCentralLift(-gamepad2.left_stick_y);
            slide.setTargetPosition(slide.slide.getCurrentPosition());
        }

        //if (gamepad2.dpad_up && !gamepad2.dpad_down){slide.sethangLift(1);}
        //if (!gamepad2.dpad_up && gamepad2.dpad_down) {slide.sethangLift(-1);}
        //if (!gamepad2.dpad_up && !gamepad2.dpad_down) {slide.sethangLift(0);}

        slide.slideToPosition();

        telemetry.addData("Speedmod:", speedMod);
        telemetry.addData("SpeedmodTwo:", speedModTwo);
        telemetry.addData("Flip Drive:", flipDrive);
        telemetry.addData("intakeCycleDirection:", intakeCycleDirection);
        telemetry.addData("slide Position:", slide.slide.getCurrentPosition());
        telemetry.update();
    }


}

