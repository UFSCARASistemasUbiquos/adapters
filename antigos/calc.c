#include <stdlib.h>
#include <stdio.h>
#include <string.h>


float Calcula(char *s){
	int c = 0, entrada = 0, op = 0, g;
	int numContas[10];
	float pilhaVal[10];
	char pilhaOp[10];
	int flag = 0, flag2 = 0, flagNumNegativo = 0;
	entrada = 0;
	for(c=0;c<strlen(s);c++) {
		if(s[c] == '-' || s[c] == '+' || s[c] == '*' || s[c] == '/'){
			if(c!=0){
				flag = flag2 = 0;
				pilhaOp[op] = s[c];
				op++;
			}else{
				flagNumNegativo = 1;
			}
		}
		if((s[c] >= '0' && s[c] <= '9') || s[c] == '.'){
			/* VERIFICA SE EH CONTINUACAO DE UM NUMERO (DEZENA, CENTENA, UNIDADE, etc) */
			if(flag==0){
				pilhaVal[entrada] =  s[c] - '0';
				if(flagNumNegativo){
					pilhaVal[entrada] = pilhaVal[entrada] * -1.0;
					flagNumNegativo = 0;
				}
				flag = 1;
				entrada++;
			}else{
				if(s[c] == '.'){
					flag2 = 1;
				}else{
					pilhaVal[entrada-1] = pilhaVal[entrada-1] * 10;
					pilhaVal[entrada-1] = pilhaVal[entrada-1] + s[c] - '0';
				}
				if(flag2 == 1 && s[c] != '.'){
					pilhaVal[entrada-1] = pilhaVal[entrada-1] / 10;
				}
			}
		}
	}
	for(g = 0; g < op; g++) {
		if(pilhaOp[op-1]=='+'){
			pilhaVal[entrada-2] = pilhaVal[entrada-2] + pilhaVal[entrada-1];
		}else if(pilhaOp[op-1]=='-') {
			pilhaVal[entrada-2] = pilhaVal[entrada-2] - pilhaVal[entrada-1];
		}else if(pilhaOp[op-1]=='*') {
			pilhaVal[entrada-2] = pilhaVal[entrada-2] * pilhaVal[entrada-1];
		}else if(pilhaOp[op-1]=='/') {
			pilhaVal[entrada-2] = pilhaVal[entrada-2] / pilhaVal[entrada-1];
		}
		op--;
		entrada--;
	}
	return pilhaVal[0];
}

int main(){
	
	char entrada[] = "15-6.1";
	float resultado;
	resultado = Calcula(entrada);
	printf("\n\nRESULTADO: %f\n", resultado);
	
	return 0;
}