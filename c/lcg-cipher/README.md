# [LCG Cipher](https://en.wikipedia.org/wiki/Linear_congruential_generator) (2024)

C program that uses a linear congruential generator to encrypt or decrypt given text.

## Description

Full implementation of a C command-line program that encrypts or decrypts given text leveraging the following:

- Files based specification of lines to encrypt or decrypt
- Linear congruential generator using a seed and a modulus to generate pseudo random numbers
- Error handling for incorrect input formats

Correct input format: 
- __e__ or __d__ for encrypt or decrypt
- Modulus number
- Seed number
- text to encrypt or decrypt
  
Example:
```bash
e126,25,<input-text>
d126,25,<input-text> 
```
## Dependencies

- <stdio.h>
 
## Build

```bash
$ cd src
$ make
gcc -Wall -ansi -pedantic -c cipher.c -o cipher.o
gcc -Wall -ansi -pedantic -c lcg.c -o lcg.o
gcc cipher.o lcg.o -o cipher
$ cd ..
```

## Run

```bash
$ ./src/cipher < <input-file> > <output-file>
```

## Demo

Command-line: 
```bash
$ ./src/cipher < ../tests/input-files/cipherdata.in > ../tests/output-files/cipherdata.out
```
Files: 
```bash
$ head ./tests/input-files/cipherdata.in 
e126,25,Byte
e126,25,ByteByte
e126,25,ByteByteB
e126,25,gggggggggggggggggggggggggggggggg
e8,5,gggggggggggggggggggggggggggggggg
e256,123,It is not the strongest of the species that survive, nor the most intelligent, but the one most responsive to change. --Charles Darwin
d126,25,[%?*@kaE*P
d126,25,~;,*BN*$V*R*C*Z&W.i*[q>/*EArI*U*F*]YJ!l*^t1
e38875,1234,Leonhard Euler (1707 - 1783)
d38875,1234,*]**w*\_K:3*MVR*I*S*ZOWn<*S|\u*Dn5lE*U
```

```bash
$ head ./tests/output-files/cipherdata.out 
    1: [%?*@
    2: [%?*@kaE*P
    3: [%?*@kaE*P!
    4: ~;,*BN*$V*R*C*Z&W.i*[q>/*EArI*U*F*]YJ!l*^t1
    5: badcfe`gbadcfe`gbadcfe`gbadcfe`g
    6: 2*UE*\L*UgG7j*Xtb>b$y]*Z#**umX<|]*W*$*J*@Skgf}:%*H<*J*Jy*[TKC*EB$pHOHVjSNui*Vc.3O*ALZ*J*R%*CjmH23rqr>*E*Tpg#yW}?*\A8]*T@)'6$px*YaUOm*G*G^*QX*I|2A*@FTup*Q<>*Z+r ?*B*V*BVX
    7: ByteByte
    8: gggggggggggggggggggggggggggggggg
    9: *]**w*\_K:3*MVR*I*S*ZOWn<*S|\u*Dn5lE*U
   10: Leonhard Euler (1707 - 1783)
```
## Author

[Beckett Dunlavy](https://github.com/BeckDun)

