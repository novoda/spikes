#include <AFMotor.h> // from https://github.com/adafruit/Adafruit-Motor-Shield-library
#include <Servo.h> 

AF_DCMotor motorRight1(1);
AF_DCMotor motorRight2(2);
AF_DCMotor motorLeft1(4);
AF_DCMotor motorLeft2(3);

void setup() {
  Serial.begin(9600);           
  Serial.println("Movement unit starting");
   
  // turn on motors
  // TODO: check if these are actually needed
//  motorRight1.setSpeed(200);
//  motorRight1.run(RELEASE);
//  motorRight2.setSpeed(200);
//  motorRight2.run(RELEASE);
//  motorLeft1.setSpeed(200);
//  motorLeft1.run(RELEASE);
//  motorLeft2.setSpeed(200);
//  motorLeft2.run(RELEASE);
}

int i;

void loop() {
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

