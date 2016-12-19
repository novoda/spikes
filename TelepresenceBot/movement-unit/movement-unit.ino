#include <AFMotor.h> // from https://github.com/adafruit/Adafruit-Motor-Shield-library
#include <Servo.h> 

AF_DCMotor motorRight1(3);
AF_DCMotor motorRight2(2);
AF_DCMotor motorLeft1(4);
AF_DCMotor motorLeft2(1);

const char COMMAND_FORWARD = 'w';
const char COMMAND_BACKWARD = 's';
const char COMMAND_LEFT = 'a';
const char COMMAND_RIGHT = 'd';
const char COMMAND_TEST = 't';
const unsigned long COMMAND_TIMEOUT = 100;
const int MAX_SPEED = 255;
const int DELTA_SPEED = 50;

int currentDirection = RELEASE;
int currentSpeed = 0;
unsigned long lastCommand;

void setup() {
  Serial.begin(9600);           
  Serial.println("Movement unit starting");
}

void loop() {
  if (Serial.available() == 0) {
    if (millis() - lastCommand > COMMAND_TIMEOUT) {
      stopMotors();
    }
    return;
  }

  lastCommand = millis();
  char inChar = Serial.read();

  switch(inChar) {
    case (COMMAND_FORWARD):
      setRightDirection(FORWARD);
      setLeftDirection(FORWARD);
      setAllMotorsSpeed(MAX_SPEED);
      break;
    case (COMMAND_BACKWARD):
      setRightDirection(BACKWARD);
      setLeftDirection(BACKWARD);
      setAllMotorsSpeed(MAX_SPEED);
      break;   
   case (COMMAND_LEFT):
      setRightDirection(FORWARD);
      setLeftDirection(BACKWARD);
      setAllMotorsSpeed(MAX_SPEED);
      break;
    case (COMMAND_RIGHT):
      setRightDirection(BACKWARD);
      setLeftDirection(FORWARD);
      setAllMotorsSpeed(MAX_SPEED);
      break;
    case (COMMAND_TEST):
      testMotors();
      break;
    default:
       Serial.println("Unknown command " + inChar);
  }
}

void stopMotors() {
  setAllMotorsSpeed(0);
  currentDirection = RELEASE;
  setRightDirection(RELEASE);
  setLeftDirection(RELEASE);
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

void setRightDirection(int direction) {
  motorRight1.run(direction);
  motorRight2.run(direction);
}

void setLeftDirection(int direction) {
  motorLeft1.run(direction);
  motorLeft2.run(direction);
}

void setAllMotorsSpeed(int speed) {
  setRightSpeed(speed);  
  setLeftSpeed(speed);
}

void setRightSpeed(int speed) {
  motorRight1.setSpeed(speed);
  motorRight2.setSpeed(speed);
}

void setLeftSpeed(int speed) {
  motorLeft1.setSpeed(speed);
  motorLeft2.setSpeed(speed);
}

