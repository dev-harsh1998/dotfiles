#include <stdio.h>
#include <stdlib.h>

void main()
{
printf ("\nDeleting APT's lock content please input your sudo password if asked\n");
system ("sudo rm -rf /var/lib/apt/lists/lock");
system ("sudo rm -rf /var/cache/apt/archives/lock");
printf ("Progress 69%\n");
system ("sudo rm -rf /var/lib/dpkg/lock");
printf ("Done!\n");
}

