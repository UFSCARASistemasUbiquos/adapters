int valor = 100;
int a;
void setup()
{
  // start serial port at 9600 bps:
  Serial.begin(9600);
  
}

void loop()
{
  while (Serial.available() == 0);  // Wait here until input buffer has a character
  {
    a = Serial.parseInt();
    valor += a;
    Serial.print("VALOR: ");
    Serial.println(valor);
  }
}
