Spuštění serveru
Celý server se nastartuje příkazem:
./start_server <config> <port>

kde <config> je XML soubor s nastavením sítě (počítače, rozhraní, ..)
Parametr <port> říká, na jakém portu se začnou vytvářet jednotlivé počítače z XML souboru.
Parametr <port> je volitelny, defaultne je nastaven 4000.
Volitelný parametr -n umožní načtení pouze kostry sítě a počítačů s rozhraními.

Po spuštění serveru se vypíše seznam počítačů a k nim přiřazených portů.
Dále se bude na standartní výstup vypisou různé servisní informace.

Připojení klientem
Pro připojení na cisco počítač:
./cisco.sh <port>

Na ciscu je implementován příkaz help (help_en), který vypisuje seznam podporovaných příkazů.

Pro připojení na cisco počítač:
./linux.sh <port>
