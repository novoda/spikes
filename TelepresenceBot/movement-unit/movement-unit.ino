#include <AFMotor.h> // from https://github.com/adafruit/Adafruit-Motor-Shield-library
#include <Servo.h> 

AF_DCMotor motorRight1(3);
AF_DCMotor motorRight2(2);
AF_DCMotor motorLeft1(4);
AF_DCMotor motorLeft2(1);

const char COMMAND_FORWARD = 'w';
const char COMMAND_BACKWARD = 's';
const char COMMAND_TEST = 't';
const int MAX_SPEED = 255;
const int DELTA_SPEED = 50;

int currentDirection = RELEASE;
int currentSpeed = 0;

void setup() {
  Serial.begin(9600);           
  Serial.println("Movement unit starting");
}

void loop() {
  if (Serial.available() == 0) {
    return;
  }
  
  char inChar = Serial.read();

  switch(inChar) {
    case (COMMAND_FORWARD):
      increaseForward();
      break;
    case (COMMAND_BACKWARD):
      increaseBackward();
      break;
    case (COMMAND_TEST):
      testMotors();
      break;
    default:
       Serial.println("Unknown command " + inChar);
  }
}

void testMotors() {
  int i;
  
  setRightDirection(FORWARD);
  setLeftDirection(FORWARD);
  for (i=0; i<MAX_SPEED; i++) {
    setRightSpeed(i);  
    setLeftSpeed(i);  
    delay(3);
  }

  setRightSpeed(MAX_SPEED);  
  setLeftSpeed(MAX_SPEED);   
  delay(5000);
 
 
  for (i=MAX_SPEED; i!=0; i--) {
    setRightSpeed(i);  
    setLeftSpeed(i);  
    delay(3);
  }
 
  setRightDirection(BACKWARD);
  setLeftDirection(BACKWARD);
  for (i=0; i<MAX_SPEED; i++) {
    setRightSpeed(i);  
    setLeftSpeed(i);   
    delay(3);
  }
 
  for (i=MAX_SPEED; i!=0; i--) {
    setRightSpeed(i);  
    setLeftSpeed(i);  
    delay(3);
  }
}

void increaseForward() {
  if (currentSpeed == 0) {
    currentDirection = FORWARD;
    setRightDirection(FORWARD);
    setLeftDirection(FORWARD);
  }
  if (currentDirection == FORWARD) {
    currentSpeed = currentSpeed + DELTA_SPEED;
  } else {
    currentSpeed = currentSpeed - DELTA_SPEED;
  }
  enforceSpeedWithinLimits();
  setRightSpeed(currentSpeed);  
  setLeftSpeed(currentSpeed);
}

void increaseBackward() {
  if (currentSpeed == 0) {
    currentDirection = BACKWARD;
    setRightDirection(BACKWARD);
    setLeftDirection(BACKWARD);
  }
  if (currentDirection == BACKWARD) {
    currentSpeed = currentSpeed + DELTA_SPEED;
  } else {
    currentSpeed = currentSpeed - DELTA_SPEED;
  }
  enforceSpeedWithinLimits();
  setRightSpeed(currentSpeed);  
  setLeftSpeed(currentSpeed);
}

void enforceSpeedWithinLimits() {
  if (currentSpeed > MAX_SPEED) {
    currentSpeed = MAX_SPEED;
  }
  if (currentSpeed < 0) {
    currentSpeed = 0;
  }
}

void setRightDirection(int direction) {
  motorRight1.run(direction);
  motorRight2.run(direction);
}

void setLeftDirection(int direction) {
  motorLeft1.run(direction);
  motorLeft2.run(direction);
}

void setRightSpeed(int speed) {
  motorRight1.setSpeed(speed);
  motorRight2.setSpeed(speed);
}

void setLeftSpeed(int speed) {
  motorLeft1.setSpeed(speed);
  motorLeft2.setSpeed(speed);
}

