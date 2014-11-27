char a[10];
int b = 0;
int c = 0;

void setup()
{
  // start serial port at 9600 bps:
    Serial.begin(9600);
  pinMode(11, OUTPUT);
}

void loop()
{

  while (Serial.available() == 0);  // Wait here until input buffer has a character
  {
    a[c++] = Serial.read();
    a[c] = '\0';
  }
  if(a[0]=='1'){
    digitalWrite(11, HIGH);
    c = 0;
  }
  if(a[0]=='0'){
    digitalWrite(11, LOW);
    c = 0;
  }
  if (strcmp(a, "LED") == 0){
    Serial.print("ARDUINO: ");
    Serial.print(b);
    b++;
    c = 0;
  }
}

