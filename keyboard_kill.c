#include <stdio.h>
#include <stdlib.h>

int checkthem(char *s1, char *s2);
void main()
{
char check[9];
printf ("\n");
printf ("Enter 'enable' to enable keyboard & 'disable' to disable keyboard: ");
scanf ("%[^\n]s", check);
if (checkthem(check, "ENABLE") == 0)
{
printf ("\nEnabling Inbuilt keyboard!");
system ("xinput enable 'AT Translated Set 2 keyboard'");
printf ("\nDone!\n");
}
else if (checkthem(check, "DISABLE") == 0)
{
printf ("\nDisabling Inbuilt keyboard!");
system ("xinput disable 'AT Translated Set 2 keyboard'");
printf ("\nDone!\n");
}
else
{
printf ("\nInvalid Input! Exiting Binary\n");
}
}

// Fuck you linux get proper string.h
int checkthem(char *s1, char *s2)
{
int i;
for (i = 0; s1[i] && s2[i]; ++i)
{
/* If characters are same or inverting the 6th bit makes them same */
if (s1[i] == s2[i] || (s1[i] ^ 32) == s2[i])
continue;
else
break;
}
/* Compare the last (or first mismatching in case of not same) characters */
if (s1[i] == s2[i])
return 0;
// Set the 6th bit in both, then compare
if ((s1[i] | 32) < (s2[i] | 32))
return -1;
return 1;
}
