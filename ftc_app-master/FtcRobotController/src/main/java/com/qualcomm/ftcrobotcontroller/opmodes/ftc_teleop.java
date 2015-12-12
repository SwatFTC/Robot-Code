/* Copyright (c) 2014 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

/**
 * TeleOp Mode
 * <p>
 * Enables control of the robot via the gamepad
 */
public class ftc_teleop extends OpMode {

	DcMotorController.DeviceMode devModemc1;
	DcMotorController.DeviceMode devModemc2;

	DcMotor liftTurn;
	DcMotor liftRaise;
	DcMotor motorRight;
	DcMotor motorLeft;
	Servo servo1;
	DcMotorController mc1;
	DcMotorController mc2;
	ServoController sc1;
	LegacyModule legacyModule;

	Gamepad dc;
	Gamepad oc;

	float DlsY;
	float DrsX;
	float OlsY;
	float OrsY;
	int extendEnc;
	int leftDriveEnc;
	int rightDriveEnc;
	int numOpLoops = 0;


	/**
	 * Constructor
	 */
	public ftc_teleop() {

	}



	/*
	 * Code to run when the op mode is first enabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
	 */
	@Override
	public void init() {


		mc1 = hardwareMap.dcMotorController.get("mc1");
		mc2 = hardwareMap.dcMotorController.get("mc2");



		/*Define motors*/
		liftRaise = new DcMotor(mc1, 1);
		motorRight = new DcMotor(mc1, 2);
		motorLeft = new DcMotor(mc2, 1);
		liftTurn = new DcMotor(mc2, 2);

		//servo1 = new Servo(sc1, 1);


/* Define the MotorControllers*/


		//controllers

		//motorLeft.setDirection(DcMotor.Direction.REVERSE);
	}

	@Override
	public void init_loop() {

		devModemc1 = DcMotorController.DeviceMode.WRITE_ONLY;
		devModemc2 = DcMotorController.DeviceMode.WRITE_ONLY;


		motorRight.setDirection(DcMotor.Direction.REVERSE);
		//motorLeft.setDirection(DcMotor.Direction.REVERSE);

		// set the mode
		// Nxt devices start up in "write" mode by default, so no need to switch device modes here.
		motorLeft.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
		motorRight.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
		liftTurn.setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
		liftRaise.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS); //Runs with encoders in teleop
	}

	/*
	 * This method will be called repeatedly in a loop
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
	 */
	@Override
	public void loop() {

		DlsY = gamepad1.left_stick_y;
		DrsX = gamepad1.right_stick_x;
		OlsY = gamepad2.left_stick_y;
		OrsY = gamepad2.right_stick_y;

		if(allowedToWrite()) {


			if (Math.abs(DlsY) > .1 || Math.abs(DrsX) > .1) {
				if (Math.abs(DlsY) + Math.abs(DrsX) > 1) {
					motorLeft.setPower(1);
					motorRight.setPower(1);
				} else {
					motorRight.setPower(DlsY + DrsX);
					motorLeft.setPower(DlsY - DrsX);
				}
			} else {
				motorRight.setPower(0);
				motorLeft.setPower(0);
			}

			if (Math.abs(OlsY) > .1) {
				liftRaise.setPower(-OlsY);
			} else {
				liftRaise.setPower(0);
			}

			if (Math.abs(OrsY) > .1) {
				liftTurn.setPower(-OrsY);
			} else if ((Math.abs(OrsY)<= .1)&&(gamepad2.dpad_up)){
				liftTurn.setPower(0.2);
			} else if ((Math.abs(OrsY) <= .1)&&(gamepad2.dpad_down)) {
				liftTurn.setPower(-0.2);
			} else {
				liftTurn.setPower(0);
			}

			/*if (gamepad1.a) { //Turn the servo to the position.
				servo1.setPosition(0.5); //Should be a 180 degree turn from starting position.
			}

			if (gamepad1.a) { //Turn the servo to the reset.
				servo1.setPosition(0.5); //Should be a 180 degree turn to the starting position.
			}*/




		}

		if (numOpLoops % 17 == 0){
			// Note: If you are using the NxtDcMotorController, you need to switch into "read" mode
			// before doing a read, and into "write" mode before doing a write. This is because
			// the NxtDcMotorController is on the I2C interface, and can only do one at a time. If you are
			// using the USBDcMotorController, there is no need to switch, because USB can handle reads
			// and writes without changing modes. The NxtDcMotorControllers start up in "write" mode.
			// This method does nothing on USB devices, but is needed on Nxt devices.
			mc1.setMotorControllerDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
			mc2.setMotorControllerDeviceMode(DcMotorController.DeviceMode.READ_ONLY);

		}

		if(mc1.getMotorControllerDeviceMode() == DcMotorController.DeviceMode.READ_ONLY && (mc2.getMotorControllerDeviceMode() == DcMotorController.DeviceMode.READ_ONLY)){
			rightDriveEnc = motorRight.getCurrentPosition();
			leftDriveEnc = motorLeft.getCurrentPosition();
			extendEnc = liftRaise.getCurrentPosition();
			telemetry.addData("Text", "*** Robot Data***");
			telemetry.addData("ExtendEncoder", extendEnc);
			telemetry.addData("LeftEncPos", leftDriveEnc);
			telemetry.addData("RightEncPos", rightDriveEnc);
			mc1.setMotorControllerDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
			mc2.setMotorControllerDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);

			numOpLoops = 0;
		}

		devModemc1 = mc1.getMotorControllerDeviceMode();
		devModemc2 = mc2.getMotorControllerDeviceMode();
		numOpLoops++;
	}

	/*
	 * Code to run when the op mode is first disabled goes here
	 * 
 	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
	 */
	@Override
	public void stop() {

	}

	private boolean allowedToWrite(){
		return (devModemc1 == DcMotorController.DeviceMode.WRITE_ONLY || devModemc1 == DcMotorController.DeviceMode.SWITCHING_TO_WRITE_MODE) && (devModemc2 == DcMotorController.DeviceMode.WRITE_ONLY || devModemc2 == DcMotorController.DeviceMode.SWITCHING_TO_WRITE_MODE);
	}

    	
	/*
	 * This method scales the joystick input so for low joystick values, the 
	 * scaled value is less than linear.  This is to make it easier to drive
	 * the robot more precisely at slower speeds.
	 */
	double scaleInput(double dVal)  {
		double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
				0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };
		
		// get the corresponding index for the scaleInput array.
		int index = (int) (dVal * 16.0);
		
		// index should be positive.
		if (index < 0) {
			index = -index;
		}

		// index cannot exceed size of array minus 1.
		if (index > 16) {
			index = 16;
		}

		// get value from the array.
		double dScale = 0.0;
		if (dVal < 0) {
			dScale = -scaleArray[index];
		} else {
			dScale = scaleArray[index];
		}

		// return scaled value.
		return dScale;
	}

}
