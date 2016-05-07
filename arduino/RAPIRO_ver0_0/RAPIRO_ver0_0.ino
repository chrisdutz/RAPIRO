// by ShotaIshiwatari is licensed under the Creative Commons - Public Domain Dedication license.

#include <Servo.h>

#define SHIFT 7
#define R 0          // Red LED
#define G 1          // Green LED
#define B 2          // Blue LED
#define TIME 15      // Column of Time
#define MAXSN 12     // Max Number of Servos
#define MAXMN 5      // Max Number of Motions
#define MAXFN 6      // Max Number of Frames
#define POWER 17     // Servo power supply control pin
#define ERR -1       // Error
#define TIMEUNIT 100 // Number of milliseconds one time unit is long

int i = 0;
int t = 1;
Servo servo[MAXSN];
uint8_t eyes[3] = { 0, 0, 0};

// Fine angle adjustments (degrees)
int trim[MAXSN] = { 0,  // Head yaw
                    1,  // Waist yaw
                    4,  // R Sholder roll
                    0,  // R Sholder pitch
                    0,  // R Hand grip
                    0,  // L Sholder roll
                    -6,  // L Sholder pitch
                    0,  // L Hand grip
                    4,  // R Foot yaw
                    -2,  // R Foot pitch
                    -4,  // L Foot yaw
                    4}; // L Foot pitch

int nowAngle[MAXSN] =        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};  // Initialize array to 0
int targetAngle[MAXSN] =     { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};  // Initialize array to 0
int deltaAngle[MAXSN] =      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};  // Initialize array to 0
uint8_t bufferAngle[MAXSN] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};  // Initialize array to 0
uint8_t tempAngle[MAXSN] =   { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};  // Initialize array to 0

int nowBright[3] =        { 0, 0, 0};  // Initialize array to 0
int targetBright[3] =     { 0, 0, 0};  // Initialize array to 0
int deltaBright[3] =      { 0, 0, 0};  // Initialize array to 0
uint8_t bufferBright[3] = { 0, 0, 0};  // Initialize array to 0
uint8_t tempBright[3] =   { 0, 0, 0};  // Initialize array to 0

double startTime =   0;                // Motion start time(msec)
double endTime =     0;                // Motion end time(msec)
int remainingTime =  0;                // Motion remaining time(msec)
uint8_t bufferTime = 0;                // Motion buffer time (0.1sec)

uint8_t motionNumber = 0;
uint8_t frameNumber = 0;
uint8_t irPortNumber = 6;
double aval;
char mode = 'M';

uint8_t motion[MAXMN][MAXFN][16]={
{  // 0 Stop
  { 90, 90,  0,130, 90,180, 50, 90, 90, 90, 90, 90,  0,  0,255, 10},
  { 90, 90,  0,130, 90,180, 50, 90, 90, 90, 90, 90,  0,  0,  0,  0},
  { 90, 90,  0,130, 90,180, 50, 90, 90, 90, 90, 90,  0,  0,  0,  0},
  { 90, 90,  0,130, 90,180, 50, 90, 90, 90, 90, 90,  0,  0,  0,  0},
  { 90, 90,  0,130, 90,180, 50, 90, 90, 90, 90, 90,  0,  0,  0,  0},
  { 90, 90,  0,130, 90,180, 50, 90, 90, 90, 90, 90,  0,  0,  0,  0}/*,
  { 90, 90,  0,130, 90,180, 50, 90, 90, 90, 90, 90,  0,  0,  0,  0},
  {  90,    90,              0,             130,          90,            180,              50,          90,         90,           90,         90,           90,   0,     0,    0,  0}*/
},
{  // 1 Forward
// Head, Wiast, R Sholder roll, R Sholder pitch, R Hand grip, L Sholder roll, L Sholder pitch, L Hand grip, R Foot yaw, R Foot pitch, L Foot yaw, L Foot pitch, Red, Green, Blue,
//                                            *                                             *                                      *                         *
  {  90,    90,              0,              90,          90,            180,              90,          90,         80,          110,         80,          120,   0,     0,    0,  5},
  {  90,    90,              0,              90,          90,            180,              90,          90,         70,           90,         70,           90,   0,     0,  255,  5},
  {  90,    90,              0,              90,          90,            180,              90,          90,         70,           70,         70,           80,   0,     0,  255,  5},
  {  90,    90,              0,              90,          90,            180,              90,          90,        100,           60,        100,           70,   0,     0,    0,  5},
  {  90,    90,              0,              90,          90,            180,              90,          90,        110,           90,        110,           90,   0,     0,  255,  5},
  {  90,    90,              0,              90,          90,            180,              90,          90,        110,          100,        110,          110,   0,     0,  255,  5}/*,
  {  90,    90,              0,              90,          90,            180,              90,          90,         90,           90,         90,           90,   0,     0,    0,  0},
  {  90,    90,              0,              90,          90,            180,              90,          90,         90,           90,         90,           90,   0,     0,    0,  0}*/
},
{  // 2 Back
  { 90, 90,  0, 90, 90,180, 90, 90,100,110,100,120,  0,  0,  0,  5},
  { 90, 90,  0, 90, 90,180, 90, 90,110, 90,110, 90,  0,  0,255,  5},
  { 90, 90,  0, 90, 90,180, 90, 90,110, 70,110, 80,  0,  0,255,  5},
  { 90, 90,  0, 90, 90,180, 90, 90, 80, 30, 80, 70,  0,  0,  0,  5},
  { 90, 90,  0, 90, 90,180, 90, 90, 70, 90, 70, 90,  0,  0,255,  5},
  { 90, 90,  0, 90, 90,180, 90, 90, 70,100, 70,110,  0,  0,255,  5}/*,
  { 90, 90,  0, 90, 90,180, 90, 90, 90, 90, 90, 90,  0,  0,  0,  0},
  { 90, 90,  0, 90, 90,180, 90, 90, 90, 90, 90, 90,  0,  0,  0,  0}*/
},
{  // 3 Right
  { 90, 90,  0, 90, 90,180, 90, 90, 95,110, 85,120,  0,  0,  0,  5},
  { 90, 90,  0, 90, 90,180, 90, 90,100, 90, 80, 90,  0,  0,255,  5},
  { 90, 90,  0, 90, 90,180, 90, 90,100, 70, 80, 80,  0,  0,  0,  5},
  { 90, 90,  0, 90, 90,180, 90, 90, 85, 60, 95, 70,  0,  0,255,  5},
  { 90, 90,  0, 90, 90,180, 90, 90, 80, 90,100, 90,  0,  0,  0,  5},
  { 90, 90,  0, 90, 90,180, 90, 90, 80,100,100,110,  0,  0,255,  5}/*,
  { 90, 90,  0, 90, 90,180, 90, 90, 90, 90, 90, 90,  0,  0,  0,  0},
  { 90, 90,  0, 90, 90,180, 90, 90, 90, 90, 90, 90,  0,  0,  0,  0}*/
},
{  // 4 Left
  { 90, 90,  0, 90, 90,180, 90, 90, 95, 60, 85, 70,  0,  0,  0,  5},
  { 90, 90,  0, 90, 90,180, 90, 90,100, 90, 80, 90,  0,  0,255,  5},
  { 90, 90,  0, 90, 90,180, 90, 90,100,100, 80,110,  0,  0,  0,  5},
  { 90, 90,  0, 90, 90,180, 90, 90, 85,110, 95,120,  0,  0,255,  5},
  { 90, 90,  0, 90, 90,180, 90, 90, 80, 90,100, 90,  0,  0,  0,  5},
  { 90, 90,  0, 90, 90,180, 90, 90, 80, 70,100, 80,  0,  0,255,  5}/*,
  { 90, 90,  0, 90, 90,180, 90, 90, 90, 90, 90, 90,  0,  0,  0,  0},
  { 90, 90,  0, 90, 90,180, 90, 90, 90, 90, 90, 90,  0,  0,  0,  0}*/
}/*,
{  // 5 Green
  { 90, 90,120, 90, 90, 60, 90, 90, 90, 90, 90, 90,  0,  0,  0, 10},
  {100, 90,120,130,110, 60, 50, 70, 90, 90, 90, 90,  0,255,  0,  5},
  { 90, 90,120, 90, 90, 60, 90, 90, 90, 90, 90, 90,  0,255,  0,  5},
  { 80, 90,120,130,110, 60, 50, 70, 90, 90, 90, 90,  0,  0,  0,  5},
  { 90, 90,120, 90, 90, 60, 90, 90, 90, 90, 90, 90,  0,255,  0, 10},
  { 90, 90,120,130,110, 60, 50, 70, 90, 90, 90, 90,  0,255,  0,  5},
  { 90, 90,  0, 90, 90,180, 90, 90, 90, 90, 90, 90,  0,  0,  0,  0},
  { 90, 90,  0, 90, 90,180, 90, 90, 90, 90, 90, 90,  0,  0,  0,  0}
},
{  // 6 Yellow
  { 90,120,120,130, 90,180, 90, 90, 90, 90, 90, 90,255,255,  0,  7},
  { 90,120,120, 90, 90,180, 90, 90, 90, 90, 90, 90,255,255,  0,  7},
  { 90, 90,  0, 90, 90,180, 90, 90, 90, 90, 90, 90,  0,  0,  0,  0},
  { 90, 90,  0, 90, 90,180, 90, 90, 90, 90, 90, 90,  0,  0,  0,  0},
  { 90, 90,  0, 90, 90,180, 90, 90, 90, 90, 90, 90,  0,  0,  0,  0},
  { 90, 90,  0, 90, 90,180, 90, 90, 90, 90, 90, 90,  0,  0,  0,  0},
  { 90, 90,  0, 90, 90,180, 90, 90, 90, 90, 90, 90,  0,  0,  0,  0},
  { 90, 90,  0, 90, 90,180, 90, 90, 90, 90, 90, 90,  0,  0,  0,  0}
},
{  // 7 Blue
  { 90, 90,120,130, 70, 60, 50,110, 90, 90, 90, 90,  0,  0,255, 10},
  { 90, 90,120,130,110, 60, 50, 70, 90, 90, 90, 90,  0,  0,255,  5},
  { 90, 90,120,130, 70, 60, 50,110, 90, 90, 90, 90,  0,  0,255,  5},
  { 90, 90,120,130,110, 60, 50, 70, 90, 90, 90, 90,  0,  0,255,  5},
  { 90, 90,120,130,110, 60, 50, 70, 90, 90, 90, 90,  0,  0,255, 15},
  { 90, 90, 90,130,110, 90, 50, 70, 90, 90, 90, 90,  0,  0,255,  3},
  { 90, 90,120,130,110, 60, 50, 70, 90, 90, 90, 90,  0,  0,255,  3},
  { 90, 90, 90,130,110, 90, 50, 70, 90, 90, 90, 90,  0,  0,255,  3}
},
{  // 8 Red
  { 90, 60,  0, 90, 90, 60, 50, 90, 90, 90, 90, 90,255,  0,  0,  7},
  { 90, 60,  0, 90, 90, 60, 90, 90, 90, 90, 90, 90,255,  0,  0,  7},
  { 90, 90,  0, 90, 90,180, 90, 90, 90, 90, 90, 90,  0,  0,  0,  0},
  { 90, 90,  0, 90, 90,180, 90, 90, 90, 90, 90, 90,  0,  0,  0,  0},
  { 90, 90,  0, 90, 90,180, 90, 90, 90, 90, 90, 90,  0,  0,  0,  0},
  { 90, 90,  0, 90, 90,180, 90, 90, 90, 90, 90, 90,  0,  0,  0,  0},
  { 90, 90,  0, 90, 90,180, 90, 90, 90, 90, 90, 90,  0,  0,  0,  0},
  { 90, 90,  0, 90, 90,180, 90, 90, 90, 90, 90, 90,  0,  0,  0,  0}
},
{  // 9 Push
  { 90, 90, 90,130,110,180, 50, 90, 90, 90, 90, 90,  0,  0,  0, 10},
  { 90, 90, 90,130,110,180, 50, 90, 90, 90, 90, 90,  0,  0,255,  5},
  { 90, 90, 90,130,110,180, 50, 90, 90, 90, 90, 90,  0,  0,255, 25},
  { 90, 90, 90,130, 90,180, 50, 90, 90, 90, 90, 90,  0,  0,  0,  5},
  { 40,140, 90, 70, 90,180, 90, 90, 90, 90, 90, 90,  0,  0,255, 10},
  { 40,140, 90, 70, 90,180, 90, 90, 90, 90, 90, 90,  0,  0,255, 25},
  { 90, 90,  0, 90, 90,180, 90, 90, 90, 90, 90, 90,  0,  0,  0,  0},
  { 90, 90,  0, 90, 90,180, 90, 90, 90, 90, 90, 90,  0,  0,  0,  0}
}*/
};

void setup()  {
  // Setup which pins each servo is connected to.
  servo[0].attach(10);   // Head yaw
  servo[1].attach(11);   // Waist yaw
  servo[2].attach(9);    // R Sholder roll
  servo[3].attach(8);    // R Sholder pitch
  servo[4].attach(7);    // R Hand grip
  servo[5].attach(12);   // L Sholder roll
  servo[6].attach(13);   // L Sholder pitch
  servo[7].attach(14);   // L Hand grip
  servo[8].attach(4);    // R Foot yaw
  servo[9].attach(2);    // R Foot pitch
  servo[10].attach(15);  // L Foot yaw
  servo[11].attach(16);  // L Foot pitch

  // Initialize the Servos.
  for( i = 0; i < MAXSN; i++) {
    targetAngle[i] = motion[0][0][i] << SHIFT;
    nowAngle[i] = targetAngle[i];
    servo[i].write((nowAngle[i] >> SHIFT) + trim[i]);
  }

  // Setup which pins the R,G and B LEDs are connected to.
  eyes[R] = 6;           // Red LED of eyes
  eyes[G] = 5;           // Green LED of eyes
  eyes[B] = 3;           // Blue LED of eyes

  // Initialize the color of the LEDs.
  for(i = 0; i < 3; i++) {
    targetBright[i] = 0 << SHIFT;
    nowBright[i] = targetBright[i];
    analogWrite(eyes[i], nowBright[i] >> SHIFT);
  }

  // Set the boud rate.
  // 8 data bits, one stop bit, no parity bit.
  //Serial.begin(115200);
  Serial.begin(57600);

  // Give everything some time to setup.
  delay(500);

  Serial.print("Hello Chris");

  // Turn on the servos and the LEDs.
  pinMode(POWER, OUTPUT);
  digitalWrite(POWER, HIGH);
}

void loop()  {
  int buf = ERR;
  // Handle the input of commands.
  if(Serial.available()) {
    if(Serial.read() == '#') {
      while(!Serial.available()){}

      switch(Serial.read()) {
        // Motions are timed sequences of poses.
        case 'M':
          buf = readOneDigit();
          if(buf != ERR){
            motionNumber = buf;
            mode = 'M';

            // If the servos were turned off, turn them on now.
            digitalWrite(POWER, HIGH);

            // Echo the command.
            Serial.write("#M");
            Serial.write(motionNumber);
          } else {
            Serial.write("#EM");
          }
          Serial.write("\n");
          break;

        // Set all servos to a given position and LEDs to a given color.
        case 'P':
          buf = getPose();
          if(buf != ERR) {
            mode = 'P';
            digitalWrite(POWER, HIGH);
            Serial.write("#PT");
            printThreeDigit(buf);
          } else {
            Serial.write("#EP");
          }
          Serial.write("\n");
          break;

        // Tells what Rapiro is currently doing (Motion and rest-time, Pose and rest-time)
        case 'Q':
          Serial.write("#Q");
          if(mode == 'M') {
            Serial.write("M");
            Serial.write(motionNumber);
            Serial.write("T");
            buf = (endTime-millis()) /100;
            if(buf < 0) { buf = 0;}
            printThreeDigit(buf);
          }
          if(mode == 'P') {
            Serial.write("PT");
            buf = (endTime-millis()) /100;
            if(buf < 0) { buf = 0;}
            printThreeDigit(buf);
          }
          Serial.write("\n");
          break;

        // Reports if Rapiro is finished moving ( TODO: I hope)
        case 'C':
          Serial.write("#C");
          if(bufferTime > 0) {
            Serial.write("F");
          } else {
            Serial.write("0");
          }
          Serial.write("\n");
          break;

        // Read the analog value of the IR sensor.
        case 'A':
          aval = analogRead(irPortNumber);
          Serial.write("#A");
          Serial.write(irPortNumber);
          Serial.write("V");
          Serial.write(int(aval));
          Serial.write("\n");
          break;

        // Halt (Turns the power off all servos and LEDs).
        case 'H':
          Serial.write("#H\n");
          digitalWrite(POWER, LOW);
          break;

        // Output the current positions of each servo as well as the analog ir value.
        case 'S':
          Serial.write("#S");
          for( i = 0; i < MAXSN; i++) {
            Serial.write((nowAngle[i] >> SHIFT) + trim[i]);
            Serial.write(":");
          }
          for(i = 0; i < 3; i++) {
            Serial.write(nowBright[i]);
            Serial.write(":");
          }
          aval = analogRead(irPortNumber);
          Serial.write(int(aval));
          Serial.write("\n");
          break;

        default:
          Serial.write("#E\n");
          break;
      }
    }
  }

  // If the movement isn't finished yet, update the new servo angles and LED brightnesses.
  if(endTime > millis()) {
    // As we are processing deltaAngle in degrees per 10 milliseconds, we have to divide the remaining time by 10.
    remainingTime = (endTime - millis()) / 10;
    for( i = 0; i < MAXSN; i++) {
      nowAngle[i] = targetAngle[i] - (deltaAngle[i] * remainingTime);
      // As the angle was shifted left by 7 bits, we have to shift it back.
      servo[i].write((nowAngle[i] >> SHIFT) + trim[i]);
    }
    for( i = 0; i < 3; i++) {
      nowBright[i] = targetBright[i] - (deltaBright[i] * remainingTime);
      // As the brightness was shifted left by 7 bits, we have to shift it back.
      analogWrite(eyes[i], nowBright[i] >> SHIFT);
    }
  }

  // If we are finished moving and we are in "Motion" mode, go to the next pose.
  else if(mode == 'M') {
    nextFrame();
  }

  // If we are finished moving and we are in "Pose" mode, go to the next pose.
  else if(mode == 'P') {
    // If we have just entered the pose and the input hasn't been processed yet, make the pose active.
    if(bufferTime > 0){
      nextPose();
    }

    // Turn off the servos half a second after the movement has finished.
    else if(endTime + 500 < millis()){
      //digitalWrite(POWER, LOW);
    }
  }
}

// Motion Play
void nextFrame() {
  frameNumber++;

  // As soon as the last frame is reached, start from the beginning.
  if(frameNumber >= MAXFN) {
    frameNumber = 0;
  }

  // Update the next values for each servo angle.
  for(i = 0; i < MAXSN; i++) {
    bufferAngle[i] = motion[motionNumber][frameNumber][i];
  }

  // Update the next values for the LEDs colors.
  for( i = 0; i < 3; i++) {
    bufferBright[i] = motion[motionNumber][frameNumber][MAXSN+i];
  }

  // Update how long reaching the next position should take.
  bufferTime = motion[motionNumber][frameNumber][TIME];

  // Bring all servos to the new positions and the LEDs to the next color.
  nextPose();
}

// Make a pose
void nextPose() {
  // If a bufferTime is provided, calculate how much the servos need to move.
  if(bufferTime > 0) {
    for(i = 0; i < MAXSN; i++) {
      // Shift the value for the angle left 7 bits to increase the precision.
      targetAngle[i] = bufferAngle[i] << SHIFT;
      // Calculate the degrees delta per 10 milliseconds (No idea why we are doing that)
      deltaAngle[i] = (targetAngle[i] - nowAngle[i]) / (bufferTime * TIMEUNIT / 10);
    }
    for( i = 0; i < 3; i++) {
      // Shift the value for the brightness left 7 bits to increase the precision.
      targetBright[i] = bufferBright[i] << SHIFT;
      // Calculate the brightness delta per 10 milliseconds (No idea why we are doing that)
      deltaBright[i] = (targetBright[i] - nowBright[i]) / (bufferTime * TIMEUNIT / 10);
    }
  }

  // If the bufferTime is 0, then this pose should simply be skipped.
  else {
    for(i = 0; i < MAXSN; i++) {
      deltaAngle[i] = 0;
    }
    for(i = 0; i < 3; i++) {
      deltaBright[i] = 0;
    }
  }

  // Calculate the start time and the estimated finish time.
  startTime = millis();
  endTime = startTime + (bufferTime * TIMEUNIT);

  bufferTime = 0;
}

// Parse the parameters for the next pose.
int getPose() {
  int buf = 0;
  int value = 0;
  int maximum = 255;
  boolean readPose = true;
  if(bufferTime == 0) {
    //Initialize array to target angle
    for(i = 0; i < MAXSN; i++) {
      tempAngle[i] = bufferAngle[i];
    }
    for( i = 0; i < 3; i++) {
      tempBright[i] = bufferBright[i];
    }
  } else {
    buf = ERR;
    readPose = false;
  }

  //Read data
  while(readPose) {
    while(!Serial.available()) {}
    switch(Serial.read()) {
      case 'S':
        buf = readOneDigit();
        if(buf != ERR) {
          value = buf *10;
          buf = readOneDigit();
          if(buf != ERR) {
            value += buf;
            if(0 <= value && value < MAXSN) {
              while(!Serial.available()) {}
              if(Serial.read() == 'A') {
                maximum = 180;
                buf = readThreeDigit(maximum);
                if(buf != ERR) {
                  tempAngle[value] = buf;
                } else {
                  readPose = false;
                }
              } else {
                buf = ERR;
                readPose = false;
              }
            } else {
              buf = ERR;
              readPose = false;
            }
          }
        }
        break;

      case 'R':
        maximum = 255;
        buf = readThreeDigit(maximum);
        if(buf != ERR) {
          tempBright[R] = buf;
        } else {
          readPose = false;
        }
        break;

      case 'G':
        maximum = 255;
        buf = readThreeDigit(maximum);
        if(buf != ERR) {
          tempBright[G] = buf;
        } else {
          readPose = false;
        }
        break;

      case 'B':
        maximum = 255;
        buf = readThreeDigit(maximum);
        if(buf != ERR) {
          tempBright[B] = buf;
        } else {
          readPose = false;
        }
        break;

      case 'T':
        maximum = 255;
        buf = readThreeDigit(maximum);
        if(buf > 0) {
          bufferTime = buf;
          for(i = 0; i < MAXSN; i++){
            bufferAngle[i] = tempAngle[i];
          }
          for( i = 0; i < 3; i++) {
            bufferBright[i] = tempBright[i];
          }
        }
        readPose = false;
        break;

      default:
        buf = ERR;
        readPose = false;
        break;
    }
  }
  return buf;
}

void printThreeDigit(int buf) {
  Serial.write(buf);
}

int digit;
//Read ASCII Three-digit
int readThreeDigit(int maximum) {
  int buf;
  buf = readOneDigit();
  if(buf != ERR) {
    digit = buf * 100;
    buf = readOneDigit();
    if(buf != ERR) {
      digit += buf * 10;
      buf = readOneDigit();
      if(buf != ERR) {
        digit += buf;
        if(digit <= maximum) {
          buf = digit;
        } else {
          buf = ERR;
        }
      }
    }
  }
  return buf;
}

//Read ASCII One-digit
int readOneDigit() {
  int buf;
  while(!Serial.available()) {}
  buf = Serial.read() - 48;
  if(buf < 0 || 9 < buf){
    buf = ERR;
  }
  return buf;
}
