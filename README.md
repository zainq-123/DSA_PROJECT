Blockchain Transaction Simulator
A Java Swing-based educational tool that demonstrates core blockchain concepts using fundamental data structures (ArrayList, HashMap, Queue).
Project Overview
This project simulates how blockchain technology works by allowing users to:
Create digital transactions between wallet addresses
Store pending transactions in a mempool (queue)
Mine transactions into blocks with hash generation
View the complete blockchain with linked hashes
Observe real-time balance updates
Built with: Java 21 + Swing GUI
IDE: IntelliJ IDEA
Course: Data Structures & Algorithms
Data Structures Used
Table
Structure	Purpose	Location in Code	Time Complexity
ArrayList	Stores blockchain blocks in sequence	ArrayList<Block> blockchain	O(1) add, O(1) get
HashMap	Stores wallet addresses and balances	HashMap<String, Double> balances	O(1) get, O(1) put
Queue (LinkedList)	Stores pending transactions (mempool)	Queue<Transaction> mempool	O(1) add, O(1) poll
Where Each Structure is Used
ArrayList - Blockchain Storage
createGenesisBlock() - blockchain.add(genesis)
mineBlock() - blockchain.add(newBlock)
showBlockchain() - iterates through blockchain to display
handleBlockClick() - blockchain.get(index) for block details
HashMap - Balance Management
setupGUI() - balances.put("Alice", 1000.0) initializes wallets
sendTransaction() - balances.containsKey(sender) validates wallet
sendTransaction() - balances.get(sender) checks sufficient balance
mineBlock() - balances.get(tx.sender) and balances.put() updates
updateBalanceTable() - iterates balances.keySet() to display
Queue (LinkedList) - Mempool (Pending Transactions)
sendTransaction() - mempool.add(newTransaction) adds to queue
mineBlock() - mempool.poll() removes from front (FIFO)
mineBlock() - mempool.isEmpty() checks before mining
updateMempoolList() - iterates mempool to refresh GUI
Algorithms Implemented
1. Simple Hash Generation
Generates random 8-character hash for each block using character set and Random class.
java
String simpleHash() {
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    StringBuilder hash = new StringBuilder();
    Random r = new Random();
    for (int i = 0; i < 8; i++) {
        hash.append(chars.charAt(r.nextInt(chars.length())));
    }
    return hash.toString();
}
2. Transaction Validation (6 Checks)
All fields filled
Sender != Receiver
Valid positive number
Sender wallet exists (HashMap.containsKey)
Receiver wallet exists (HashMap.containsKey)
Sufficient balance (HashMap.get)
3. Block Mining Algorithm
Collect up to 3 transactions from Queue (FIFO)
Create Block with previous hash linkage
Generate new hash
Add to ArrayList (blockchain)
Update balances via HashMap
4. Balance Update Algorithm
Iterate mined transactions
Deduct from sender: balances.put(sender, balances.get(sender) - amount)
Add to receiver: balances.put(receiver, balances.get(receiver) + amount)
How to Run in IntelliJ IDEA
Prerequisites
IntelliJ IDEA (Community or Ultimate Edition)
Java JDK 21 installed
JDK configured in IntelliJ (File > Project Structure > SDKs)
Steps
Open IntelliJ IDEA
Open Project
File > Open
Select your project folder containing BlockchainTransactionSimulator.java
Click OK
Verify JDK Configuration
File > Project Structure > Project
Ensure Project SDK is set to Java 21
If not set, click New > JDK and select your Java 21 installation path
Open Main File
In Project Explorer (left panel), navigate to src/
Open BlockchainTransactionSimulator.java
Run the Project
Right-click anywhere in the code editor
Select Run 'BlockchainTransactionSimulator.main()'
Or click the green play button next to the main method
Alternative: Using Run Configuration
Run > Edit Configurations
Click + > Application
Name: BlockchainSimulator
Main class: BlockchainTransactionSimulator
Click OK, then Run
Java Version
plain
Java JDK 21
Project Structure (IntelliJ)
plain
BlockchainTransactionSimulator/
├── .idea/
│   ├── misc.xml
│   ├── modules.xml
│   └── workspace.xml
├── src/
│   └── BlockchainTransactionSimulator.java    # Main file (3 classes inside)
├── out/
│   └── production/
│       └── BlockchainTransactionSimulator/
│           └── BlockchainTransactionSimulator.class
├── screenshots/
│   ├── main_interface.png
│   ├── transaction_creation.png
│   ├── mining_process.png
│   └── blockchain_display.png
└── README.md
Screenshots
Main Interface

Shows blockchain display, transaction panel, mempool, and balance table
Creating Transaction

User enters sender, receiver, and amount
Mining Block

Pending transactions processed into new block
Blockchain Display

Multiple blocks with hashes and linked chain
Workflow: Sender to Receiver
plain
User Input (Sender, Receiver, Amount)
    |
    v
Validation (6 checks using HashMap)
    |
    v
Mempool (Queue - FIFO waiting area)
    |
    v
Mine Block (collect 3 txs from Queue)
    |
    v
New Block Created (hash + prev hash link)
    |
    v
Add to Blockchain (ArrayList)
    |
    v
Update Balances (HashMap - O(1))
    |
    v
Transaction Complete!
What is Mempool?
Mempool = Memory Pool = Waiting Area for Transactions
When you send money, it doesn't go immediately. It goes to a waiting line first. Transactions wait in FIFO order (First In, First Out). The first transaction that arrives gets mined first. This ensures fair processing.
Key Features
Create transactions between 5 pre-loaded wallets (Alice, Bob, Charlie, David, Eve)
Visual mempool showing pending transactions in Queue order
Mine up to 3 transactions per block
View complete blockchain with block numbers, hashes, timestamps
Real-time wallet balance updates via HashMap
Input validation with error messages
Simple random hash generation for educational purposes
Future Improvements
[ ] Add digital signatures for transaction security
[ ] Implement proof-of-work mining difficulty
[ ] Add peer-to-peer network simulation
[ ] Export blockchain data to file
[ ] Add transaction history per wallet
[ ] Implement smart contract basics
[ ] Add SHA-256 instead of simple hash
Author
Your Name: Muhammad Zain
4th Semester - Software enginerring
Data Structures & Algorithms Project
Java Swing GUI Application
