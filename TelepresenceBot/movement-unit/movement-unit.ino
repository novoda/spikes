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
  setMotorsRightDirection(FORWARD);
  setMotorsLeftDirection(FORWARD);
  for (i=0; i<255; i++) {
    setMotorsRightSpeed(i);  
    setMotorsLeftSpeed(i);  
    delay(3);
 }

    setMotorsRightSpeed(255);  
    setMotorsLeftSpeed(255);   
    delay(5000);
 
 
  for (i=255; i!=0; i--) {
    setMotorsRightSpeed(i);  
    setMotorsLeftSpeed(i);  
    delay(3);
 }
 
  setMotorsRightDirection(BACKWARD);
  setMotorsLeftDirection(BACKWARD);
  for (i=0; i<255; i++) {
    setMotorsRightSpeed(i);  
    setMotorsLeftSpeed(i);   
    delay(3);
 }
 
  for (i=255; i!=0; i--) {
    setMotorsRightSpeed(i);  
    setMotorsLeftSpeed(i);  
    delay(3);
 }
}

void setMotorsRightDirection(int direction) {
  motorRight1.run(direction);
  motorRight2.run(direction);
}
void setMotorsLeftDirection(int direction) {
  motorLeft1.run(direction);
  motorLeft2.run(direction);
}

void setMotorsRightSpeed(int speed) {
  motorRight1.setSpeed(speed);
  motorRight2.setSpeed(speed);
}
void setMotorsLeftSpeed(int speed) {
  motorLeft1.setSpeed(speed);
  motorLeft2.setSpeed(speed);
}

