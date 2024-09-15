/* Header guard prevents errors if header is included twice */
/* Provided by Joseph Haugh (UNM) CS 241 instructor  */
#ifndef LCG_H
#define LCG_H

struct LinearCongruentialGenerator
{
  unsigned long m; /* modulus */
  unsigned long c; /* increment */
  unsigned long a; /* multiplier */
  unsigned long x; /* value in sequence */
};

/***************************************************************/
/* Initialize an LCG with modulus m and increment c.           */
/* Calculate multiplier a such that:                           */
/*        a = 1+2p, if 4 is a factor of m, otherwise, a = 1+p. */
/*        p = (product of m's unique prime factors).           */
/*        a < m                                                */
/* Seed value x is same as increment c.                        */
/* If values are invalid for LCG, set all fields to zero.      */
/***************************************************************/
struct LinearCongruentialGenerator makeLCG(unsigned long m, unsigned long c);

/* Update lcg and return next value in the sequence. */
unsigned long getNextRandomValue(struct LinearCongruentialGenerator* lcg);

#endif
