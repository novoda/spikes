#include <AFMotor.h> // from https://github.com/adafruit/Adafruit-Motor-Shield-library
#include <Servo.h> 

AF_DCMotor motorRight1(1);
AF_DCMotor motorRight2(2);
AF_DCMotor motorLeft1(4);
AF_DCMotor motorLeft2(3);

const char COMMAND_FORWARD = 'w';
const char COMMAND_BACKWARD = 's';
const char COMMAND_TEST = 't';

void setup() {
  Serial.begin(9600);           
  Serial.println("Movement unit starting");
}

void loop() {
  if (Serial.available() == 0) {
    return;
  }
  
  char inChar = Serial.read();
  Serial.println("received " + inChar);

  switch(inChar) {
    case (COMMAND_FORWARD):
      // TODO: increase speed
      break;
    case (COMMAND_BACKWARD):
      // TODO: decrease speed
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
  for (i=0; i<255; i++) {
    setRightSpeed(i);  
    setLeftSpeed(i);  
    delay(3);
  }

    setRightSpeed(255);  
    setLeftSpeed(255);   
    delay(5000);
 
 
  for (i=255; i!=0; i--) {
    setRightSpeed(i);  
    setLeftSpeed(i);  
    delay(3);
  }
 
  setRightDirection(BACKWARD);
  setLeftDirection(BACKWARD);
  for (i=0; i<255; i++) {
    setRightSpeed(i);  
    setLeftSpeed(i);   
    delay(3);
  }
 
  for (i=255; i!=0; i--) {
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

void setRightSpeed(int speed) {
  motorRight1.setSpeed(speed);
  motorRight2.setSpeed(speed);
}
void setLeftSpeed(int speed) {
  motorLeft1.setSpeed(speed);
  motorLeft2.setSpeed(speed);
}

