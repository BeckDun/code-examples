
/* Beckett Dunlavy (github.com/BeckDun)
 * CS 241: Lab-06 Cipher
 * this program defines functions used for the struct LCG
 */

#include <stdio.h>
#include "lcg.h"
/*******************************************
 * unsigned long m: m used in lcg in which to
 * find the product of unique primes.
 * 
 * Return: unsigned long: the product of unique primes of m
 * This function takes an unsigned long, m, and finds the unique prime factors.
 * Then it multiplies the unique primes together and returns that number.
 *
 * Check to see if the number is divisible by 2. if m is divisible, divide by 2
 * while the mod 2 == 0. Next check all odd numbers up to the square root of m. * if the new 
 * m is divisible by a prime p multiply the product by p, then divide m by p whille m mod p == 0.
 * return m*product (because m will be prime in the end). 
 ******************************************/
unsigned long primeFactorization(unsigned long m) {
  unsigned long i = 0;
  unsigned long product = 1;
  if(m % 2 == 0) {
    product *= 2;
    while(m %2 ==0) m /= 2;
  }
  
  for(i = 3; i*i < m; i += 2) {
    if ((m%i) == 0) {
      product *= i;
      while ((m%i) == 0) {
	m /= i;
      }
    }
  }

  return m*product;
}

/***************************************************************/
/* Initialize an LCG with modulus m and increment c.           */
/* Calculate multiplier a such that:                           */
/*        a = 1+2p, if 4 is a factor of m, otherwise, a = 1+p. */
/*        p = (product of m's unique prime factors).           */
/*        a < m                                                */
/* Seed value x is same as increment c.                        */
/* If values are invalid for LCG, set all fields to zero.      */
/***************************************************************/
struct LinearCongruentialGenerator makeLCG(unsigned long m, unsigned long c){
  struct LinearCongruentialGenerator lcg = {0,0,0,0};
  struct LinearCongruentialGenerator lcgerror = {0,0,0,0};
  unsigned long p;
  if(c < 0 || m <= 0 || m < c) {
    return lcgerror;
  }
  
  lcg.m = m;
  lcg.c = c;

  p = primeFactorization(m);
  
  if(m % 4 == 0) lcg.a = 1+2*p;
  else {
    lcg.a = 1 + p;
  }

  if(lcg.a > lcg.m) return lcgerror;

  
  lcg.x = c;
  
  return lcg;
}

/* Update lcg and return next value in the sequence. */
unsigned long getNextRandomValue(struct LinearCongruentialGenerator* lcg) {
  unsigned long x = lcg->x; 
  lcg->x = ((lcg->a * lcg->x) + lcg->c) % lcg->m;
  
  return x;
}
