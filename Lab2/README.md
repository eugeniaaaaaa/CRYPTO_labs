# [Lab2](https://docs.google.com/document/d/121efoh98-uQQdgpz1fc_Zu27SiNLzib1o2Ah_sr5f1Y/edit)
Some lines of text are encoded using the same key, so parts with same lengths
can be used do break the cipher in the same way as Vigenere in lab 1.
The program will try all the possible combinations in parallel, but most probable ones
are processed first, so program can be stopped after getting some acceptable results.
Here's the output after several seconds of execution:
```
ROYADEAACFCBEFCFDDDDDEECCFADEABRYDBAAEFCCAADEACDEDEAFFEFE
ROYBCFACFABDFDEDCCDCCFACBDBFCCCDBCC
ROYBCFACADEBCDECDBEDFEDECBBEDFEADBDBBCRYADEEDAFACEEFDFDDCDFECFDEBEFFBFFAEDFDESYCDFEABDBAADFACDCBDEBDFDCBD
JOEBBFFCCDEADCDFDCEBDFCAABADCCAA
JOEDCEFCFDEEDACDAFAEABCACFBADEDAAEABKEACABBFEACAADDEFDADFDCEAABFEDCCAFB
```

Therefore, we can assume that names of people are "Roy" and "Joe"