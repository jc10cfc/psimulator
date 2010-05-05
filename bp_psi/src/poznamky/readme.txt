Spuštění serveru
Celý server se nastartuje příkazem:
./start_server <config> <port>

kde config je XML soubor s nastavením sítě (počítače, rozhraní, ..)
Parametr port říká, na jakém portu se začnou vytvářet jednotlivé počítače z XML souboru.
Volitelný parametr -n umožní načtení pouze kostry sítě a počítačů s rozhraními.

Po spuštění serveru se vypíše seznam počítačů a k nim přiřazených portů.
Dále se bude na standartní výstup vypisou různé servisní informace.



Připojení klientem
Pro připojení na cisco počítač:
./cisco.sh <port>

Pro připojení na cisco počítač:
./linux.sh <port>
