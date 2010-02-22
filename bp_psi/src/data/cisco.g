grammar cisco;
options {k=1;}

// asi smazat

start
:   show
|   enable;

show
:   'show' zbytekShow;

zbytekShow
:   'interfaces'
|   ip;

ip
:   'interface' ipZbytek
|   'route';

ipZbytek
:   'FastEthernet' iface;

iface
:   '0/0'
|   '0/1'
|   '0/2'
|   '0/1';

enable
:   'dodelat';


// reload           Halt and perform a cold restart
// ?                napoveda
// disable
// 