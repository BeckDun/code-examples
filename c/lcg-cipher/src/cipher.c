/* Beckett Dunlavy (github.com/BeckDun)
 * CS 241: Lab-06 Cipher
 * This program takes input from a file that has the correct input for 
 * encrypting or decrypting used and LCG.
 * This cipher uses an XOR encryption and decryption method.
 */
#include <stdio.h>
#include "lcg.h"


int mIn = 1;
int cIn = 0;
int encrypt = 0;
int decrypt = 0;
int first = 1;
int error = 0;

/*************************************
* Parameters:
*   - m (input):
*       Type: unsigned long
*       Meaning: Modulus used in encryption
*   - c (input):
*       Type: unsigned long: Increment used in encryption
*   - ch (input):
*       Type: int: Character to encrypt
*   - x (input):
*       Type: unsigned long: "random" number
* Description:
*   Encrypts the character 'ch' using XOR operation with a value derived from 'x'.
*   Prints the encrypted character or an error message if 'm' or 'c' is zero.
* Function’s algorithm:
*   1. Calculate the output value 'out' by performing XOR operation on 'ch' and 'x%128'.
*   2. If 'm' or 'c' is zero, print "Error" and set 'error' flag to 1.
*   3. If 'out' is less than 32, print '*'+(out+63).
*   4. If 'out' is 127, print "*$".
*   5. If 'out' is '*', print "**".
*   6. Otherwise, print the character corresponding to 'out'.
*************************************/
void encryptData(unsigned long m, unsigned long c, int ch, unsigned long x) {
  int out;
  out = ch ^ (x%128);

  if(m==0 || c==0) {
    printf("Error");
    error = 1;
  }
  else {
    if(out < 32) printf("*%c", out + '?');
    else if (out == 127) printf("*$");
    else if (out == '*') printf("**");
    else printf("%c", out);
  }
  
}


/*************************************
* Parameters:
*   - m (input):
*       Type: unsigned long: used in lcg
*   - c (input):
*       Type: unsigned long: c used on lcg
*   - ch (input):
*       Type: int: character to decrypt
*       Range of Values: ASCII values (0-127)
*   - x (input):
*       Type: unsigned long: random number
* Return Value:
*   Type: int: used for error handling and breaking
* Description:
*   Decrypts the character 'ch' using XOR operation with a value derived from 'x'.
*   Prints the decrypted character or an error message if 'm' or 'c' is zero.
*   Handles special characters '*' and '$' in a specific manner.
* Function’s algorithm:
*   1. Calculate the output value 'out' by performing XOR operation on 'ch' and 'x%128'.
*   2. If 'm' or 'c' is zero, print "Error", set 'error' flag to 1, and return 0.
*   3. If 'ch' is '*', get the next character from input and handle '*' or '$' accordingly.
*   4. If 'ch' is a printable character, decrypt it using XOR operation and print the result.
*   5. Return 0.
*************************************/
int decryptData(unsigned long m, unsigned long c, int ch, unsigned long x) {
  int out;
  out = ch ^(x%128);

  if(m==0 || c==0) {
    printf("Error");
    error = 1;
    return 0;
  }

  if(ch == '*') {
    
    ch = getchar();

    if(ch == '*') {
      out = ch^(x%128);
      printf("%c", out);
    }
    else if (ch == '$') {
      out = 127^(x%128);
      printf("%c", out);
    }
    else if (ch > 32) {
      out = (ch-'?')^(x%128);
      printf("%c", out);
    }
    else if(ch == 32) {
      printf("Error");
      error = 1;
      return 0;
    }
  } else {
    printf("%c", out);
  }
  return 0;
}


int main(void) {
  unsigned long m, c, random;
  int lineNumber;
  char ch = getchar();
  m = c = 0;
  lineNumber = 1;
  
   while(ch != EOF) {
     
     if(first) {
       if(ch == 'e') encrypt = 1;
       else if(ch == 'd') decrypt = 1;
       else {
	 while (ch!= '\n') {
	   ch = getchar();
	 }
	 mIn = 1;
	 cIn = 0;
	 first = 1;
	 ch = getchar();
	 printf("%5d: Error\n", lineNumber);
	 lineNumber++;
	 continue;
       }
       first = 0;
       ch = getchar();
       continue;
     }
     
     
     if(mIn) {
      
       if(ch >= '0' && ch <= '9') {
	 if(m == 0 && ch == '0') {
	   m = 0;
	 } else {
	   m = m*10 + (ch - '0');
	 }
       } else if(ch == ',') {
	 mIn = 0;
	 cIn = 1;
	 ch = getchar();
	 continue;
       } else {
	 while (ch!= '\n') {
	   ch = getchar();
	 }
	 m = c = 0;
	 mIn = 1;
	 cIn = 0;
	 first = 1;
	 ch = getchar();
	 printf("%5d: Error\n", lineNumber++);
	 continue;
       }
     }

     if(cIn){
       
       if(ch >= '0' && ch <= '9') {
	 if(c == 0 && ch == '0') {
	   c = 0;
	 } else {
	   c = c*10 + (ch - '0');
	 }
       } else if(ch == ',') {
	 cIn = 0;
	 ch = getchar();
	 continue;
       } else {

	 while (ch!= '\n') {
	   ch = getchar();
	 }
	 m = c = 0;
	 mIn = 1;
	 cIn = 0;
	 first = 1;
	 ch = getchar();
	 printf("%5d: Error\n", lineNumber++);
	 continue;
       }
     }
     
     if(!mIn && !cIn) {
       if(m < 0 || c < 1) {
	 printf("%5d: Error\n",lineNumber++);
	 while(ch != '\n') ch = getchar();
	 
	   ch = getchar();
           error = 0;
           m = c = random = 0;
           mIn = 1;
           cIn = 0;
           first = 1;
           encrypt = decrypt =  0;
	   continue;
       }
       if(encrypt) {
	 struct LinearCongruentialGenerator lcg;
	 lcg = makeLCG(m,c);
	 if(lcg.m == 0) {
           printf("%5d: Error\n", lineNumber);
	   while(ch != '\n') ch = getchar();
	   
           ch =	getchar();
           error = 0;
           m = c = random = 0;
           mIn = 1;
           cIn = 0;
           first = 1;
           encrypt =  0;
	   lineNumber++;
	   continue;
         } else {
	   random = getNextRandomValue(&lcg);
	   
	   printf("%5d: ",lineNumber);
	   
	   while(ch!='\n' && error == 0) {
	     encryptData(m,c,ch,random);
	     ch = getchar();
	     random = getNextRandomValue(&lcg);
	   }

	   if(error == 1) {
             while((ch= getchar()) != '\n');
           }
	   
	   printf("\n");
	   lineNumber++;
	   m = c  = 0;
	   mIn = 1;
	   cIn = 0;
	   first = 1;
	   ch = getchar();
	   encrypt = 0;
	   error = 0;
	   continue;
	 }
       }

       if (decrypt) {
	 struct LinearCongruentialGenerator lcg;
         lcg = makeLCG(m,c);
         random = getNextRandomValue(&lcg);
         /* check for error */
	 if(lcg.m == 0) {
	   printf("%5d: Error\n", lineNumber);
	   while((ch= getchar()) != '\n');
	   ch = getchar();
	   error = 0;
	   m = c = random = 0;
	   mIn = 1;
	   cIn = 0;
	   first = 1;
	   decrypt = 0;
	   lineNumber++;
	   continue;
	 } else {
	   printf("%5d: ",lineNumber);
	   while(ch !='\n' && error == 0) {
	     decryptData(m,c,ch,random);
	     ch = getchar();
	     random = getNextRandomValue(&lcg);
	   }
	   if(error == 1) {
	     while((ch= getchar()) != '\n');
	   }
	   
	   printf("\n");
	   lineNumber++;
	   m = c = 0;
	   mIn = 1;
	   cIn = 0;
	   first = 1;
	   ch = getchar();
	   decrypt = 0;
	   error = 0;
	   continue;
	 }
       }
       
     }
     
     
     ch = getchar();
   }
   
   return 0;
} 
