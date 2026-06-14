import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class Transaction {
    String sender;
    String receiver;
    double amount;
    String timestamp;
    String txId;

    public Transaction(String sender, String receiver, double amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.timestamp = new Date().toString();
        this.txId = generateTxId();
    }

    String generateTxId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder id = new StringBuilder("TX-");
        Random r = new Random();
        for (int i = 0; i < 6; i++) {
            id.append(chars.charAt(r.nextInt(chars.length())));
        }
        return id.toString();
    }

    public String toString() {
        return sender + " sent " + amount + " to " + receiver;
    }

    public String getDetails() {
        return "Transaction ID: " + txId +
                "\nSender: " + sender +
                "\nReceiver: " + receiver +
                "\nAmount: " + amount +
                "\nTime: " + timestamp;
    }
}

class Block {
    int blockNumber;
    String previousHash;
    String currentHash;
    ArrayList<Transaction> transactions;
    String timestamp;

    public Block(int blockNumber, String previousHash, ArrayList<Transaction> transactions) {
        this.blockNumber = blockNumber;
        this.previousHash = previousHash;
        this.transactions = transactions;
        this.timestamp = new Date().toString();
        this.currentHash = simpleHash();
    }

    String simpleHash() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder hash = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < 8; i++) {
            hash.append(chars.charAt(r.nextInt(chars.length())));
        }
        return hash.toString();
    }
}

public class BlockchainTransactionSimulator extends JFrame {

    ArrayList<Block> blockchain;
    HashMap<String, Double> balances;
    Queue<Transaction> mempool;
    ArrayList<Transaction> allTransactions; // Store all transactions for retrieval

    JTextField txtSender, txtReceiver, txtAmount;
    JButton btnSend, btnMine, btnRetrieve;
    DefaultListModel<String> listModel;
    JList<String> listMempool;
    JTextArea txtBlockchain;
    JTable tableBalances;
    DefaultTableModel tableModel;
    JLabel lblStatus;
    JTextArea txtTransactionHistory;

    // Constructor - initializes data structures but NOT pre-defined wallets
    public BlockchainTransactionSimulator() {
        blockchain = new ArrayList<Block>();
        balances = new HashMap<String, Double>();
        mempool = new LinkedList<Transaction>();
        allTransactions = new ArrayList<Transaction>();

        createGenesisBlock();
        setupGUI();
        updateBalanceTable();
        showBlockchain();
    }

    // Method to add wallets dynamically (called from main)
    public void addWallet(String name, double initialBalance) {
        balances.put(name, initialBalance);
        updateBalanceTable();
    }

    void createGenesisBlock() {
        ArrayList<Transaction> genesisTransactions = new ArrayList<Transaction>();
        genesisTransactions.add(new Transaction("System", "Genesis", 0));
        Block genesis = new Block(0, "0", genesisTransactions);
        blockchain.add(genesis);
    }

    void setupGUI() {
        setTitle("Blockchain Transaction Simulator");
        setSize(1400, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelTop = new JPanel();
        panelTop.setBackground(new Color(50, 50, 80));
        JLabel lblTitle = new JLabel("BLOCKCHAIN TRANSACTION SIMULATOR");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        panelTop.add(lblTitle);
        add(panelTop, BorderLayout.NORTH);

        // Center panel with tabs for Blockchain and Transaction History
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Blockchain View
        JPanel panelBlockchain = new JPanel(new BorderLayout());
        panelBlockchain.setBorder(BorderFactory.createTitledBorder("Blockchain"));
        txtBlockchain = new JTextArea();
        txtBlockchain.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtBlockchain.setEditable(false);
        txtBlockchain.setBackground(new Color(240, 240, 240));
        JScrollPane scrollBlockchain = new JScrollPane(txtBlockchain);
        panelBlockchain.add(scrollBlockchain, BorderLayout.CENTER);
        tabbedPane.addTab("Blockchain", panelBlockchain);

        // Tab 2: Transaction History View
        JPanel panelHistory = new JPanel(new BorderLayout());
        panelHistory.setBorder(BorderFactory.createTitledBorder("Transaction History & Retrieval"));
        txtTransactionHistory = new JTextArea();
        txtTransactionHistory.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtTransactionHistory.setEditable(false);
        txtTransactionHistory.setBackground(new Color(245, 245, 255));
        JScrollPane scrollHistory = new JScrollPane(txtTransactionHistory);
        panelHistory.add(scrollHistory, BorderLayout.CENTER);
        tabbedPane.addTab("Transaction History", panelHistory);

        add(tabbedPane, BorderLayout.CENTER);

        JPanel panelRight = new JPanel();
        panelRight.setLayout(new BoxLayout(panelRight, BoxLayout.Y_AXIS));
        panelRight.setPreferredSize(new Dimension(380, 0));
        panelRight.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelTransaction = new JPanel(new GridLayout(6, 2, 5, 5));
        panelTransaction.setBorder(BorderFactory.createTitledBorder("Create Transaction"));
        panelTransaction.setBackground(new Color(230, 230, 250));

        panelTransaction.add(new JLabel("Sender:"));
        txtSender = new JTextField(10);
        panelTransaction.add(txtSender);

        panelTransaction.add(new JLabel("Receiver:"));
        txtReceiver = new JTextField(10);
        panelTransaction.add(txtReceiver);

        panelTransaction.add(new JLabel("Amount:"));
        txtAmount = new JTextField(10);
        panelTransaction.add(txtAmount);

        panelTransaction.add(new JLabel(""));

        btnSend = new JButton("Send Transaction");
        btnSend.setBackground(new Color(100, 180, 100));
        btnSend.setForeground(Color.WHITE);
        btnSend.setFont(new Font("Arial", Font.BOLD, 12));
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendTransaction();
            }
        });
        panelTransaction.add(btnSend);

        btnMine = new JButton("Mine Block");
        btnMine.setBackground(new Color(200, 150, 50));
        btnMine.setForeground(Color.WHITE);
        btnMine.setFont(new Font("Arial", Font.BOLD, 12));
        btnMine.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mineBlock();
            }
        });
        panelTransaction.add(btnMine);

        // Retrieve Button
        btnRetrieve = new JButton("Retrieve Tx");
        btnRetrieve.setBackground(new Color(100, 100, 200));
        btnRetrieve.setForeground(Color.WHITE);
        btnRetrieve.setFont(new Font("Arial", Font.BOLD, 12));
        btnRetrieve.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                retrieveTransaction();
            }
        });
        panelTransaction.add(new JLabel("Retrieve:"));
        panelTransaction.add(btnRetrieve);

        panelRight.add(panelTransaction);
        panelRight.add(Box.createVerticalStrut(10));

        JPanel panelMempool = new JPanel(new BorderLayout());
        panelMempool.setBorder(BorderFactory.createTitledBorder("Mempool (Pending Transactions)"));
        panelMempool.setBackground(new Color(255, 250, 230));
        panelMempool.setPreferredSize(new Dimension(0, 180));

        listModel = new DefaultListModel<String>();
        listMempool = new JList<String>(listModel);
        listMempool.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scrollMempool = new JScrollPane(listMempool);
        panelMempool.add(scrollMempool, BorderLayout.CENTER);
        panelRight.add(panelMempool);
        panelRight.add(Box.createVerticalStrut(10));

        JPanel panelBalance = new JPanel(new BorderLayout());
        panelBalance.setBorder(BorderFactory.createTitledBorder("Wallet Balances"));
        panelBalance.setBackground(new Color(230, 255, 230));
        panelBalance.setPreferredSize(new Dimension(0, 200));

        String[] columns = {"Wallet", "Balance"};
        tableModel = new DefaultTableModel(columns, 0);
        tableBalances = new JTable(tableModel);
        tableBalances.setFont(new Font("Arial", Font.PLAIN, 12));
        tableBalances.setRowHeight(25);
        JScrollPane scrollBalance = new JScrollPane(tableBalances);
        panelBalance.add(scrollBalance, BorderLayout.CENTER);
        panelRight.add(panelBalance);

        add(panelRight, BorderLayout.EAST);

        JPanel panelBottom = new JPanel(new BorderLayout());
        panelBottom.setBackground(new Color(40, 40, 60));
        panelBottom.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        lblStatus = new JLabel("Ready - Create a transaction or mine a block");
        lblStatus.setForeground(Color.GREEN);
        lblStatus.setFont(new Font("Arial", Font.BOLD, 13));
        panelBottom.add(lblStatus, BorderLayout.WEST);

        JLabel lblInfo = new JLabel("Total Blocks: 1");
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        panelBottom.add(lblInfo, BorderLayout.EAST);

        add(panelBottom, BorderLayout.SOUTH);
    }

    void sendTransaction() {
        String sender = txtSender.getText().trim();
        String receiver = txtReceiver.getText().trim();
        String amountText = txtAmount.getText().trim();

        if (sender.equals("") || receiver.equals("") || amountText.equals("")) {
            showStatus("Error: All fields are required!", Color.RED);
            return;
        }

        if (sender.equalsIgnoreCase(receiver)) {
            showStatus("Error: Sender and receiver cannot be same!", Color.RED);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException ex) {
            showStatus("Error: Amount must be a valid number!", Color.RED);
            return;
        }

        if (amount <= 0) {
            showStatus("Error: Amount must be greater than 0!", Color.RED);
            return;
        }

        if (!balances.containsKey(sender)) {
            showStatus("Error: Sender wallet not found!", Color.RED);
            return;
        }

        if (!balances.containsKey(receiver)) {
            showStatus("Error: Receiver wallet not found!", Color.RED);
            return;
        }

        double senderBalance = balances.get(sender);
        if (senderBalance < amount) {
            showStatus("Error: Insufficient balance! " + sender + " has only " + senderBalance, Color.RED);
            return;
        }

        Transaction newTransaction = new Transaction(sender, receiver, amount);
        mempool.add(newTransaction);
        allTransactions.add(newTransaction); // Add to all transactions list
        listModel.addElement(newTransaction.toString() + " [" + newTransaction.txId + "]");

        txtSender.setText("");
        txtReceiver.setText("");
        txtAmount.setText("");

        showStatus("Transaction added to mempool! Pending: " + mempool.size(), new Color(0, 150, 0));
        updateTransactionHistory();
    }

    void mineBlock() {
        if (mempool.isEmpty()) {
            showStatus("Error: No transactions in mempool to mine!", Color.RED);
            return;
        }

        showStatus("Mining block...", new Color(200, 150, 0));

        ArrayList<Transaction> blockTransactions = new ArrayList<Transaction>();

        int count = 0;
        while (!mempool.isEmpty() && count < 3) {
            Transaction tx = mempool.poll();
            blockTransactions.add(tx);
            count++;
        }

        Block lastBlock = blockchain.get(blockchain.size() - 1);
        String previousHash = lastBlock.currentHash;

        int newBlockNumber = blockchain.size();
        Block newBlock = new Block(newBlockNumber, previousHash, blockTransactions);

        blockchain.add(newBlock);

        for (int i = 0; i < blockTransactions.size(); i++) {
            Transaction tx = blockTransactions.get(i);

            double senderBal = balances.get(tx.sender);
            balances.put(tx.sender, senderBal - tx.amount);

            double receiverBal = balances.get(tx.receiver);
            balances.put(tx.receiver, receiverBal + tx.amount);
        }

        updateMempoolList();
        updateBalanceTable();
        showBlockchain();
        updateTransactionHistory();

        showStatus("Block #" + newBlockNumber + " mined successfully!", new Color(0, 150, 0));
    }

    // NEW: Retrieve transaction by ID or search
    void retrieveTransaction() {
        String searchTerm = JOptionPane.showInputDialog(this,
                "Enter Transaction ID, Sender name, or Receiver name to search:",
                "Retrieve Transaction", JOptionPane.QUESTION_MESSAGE);

        if (searchTerm == null || searchTerm.trim().equals("")) {
            return;
        }

        searchTerm = searchTerm.trim();
        StringBuilder results = new StringBuilder();
        boolean found = false;

        results.append("===== SEARCH RESULTS =====\n\n");
        results.append("Search Term: " + searchTerm + "\n\n");

        for (Transaction tx : allTransactions) {
            if (tx.txId.equalsIgnoreCase(searchTerm) ||
                    tx.sender.equalsIgnoreCase(searchTerm) ||
                    tx.receiver.equalsIgnoreCase(searchTerm)) {

                found = true;
                results.append("----------------------------------------\n");
                results.append(tx.getDetails());
                results.append("\n----------------------------------------\n\n");
            }
        }

        if (!found) {
            results.append("No transactions found matching: " + searchTerm);
        }

        // Show results in a dialog
        JTextArea textArea = new JTextArea(results.toString());
        textArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        textArea.setEditable(false);
        textArea.setBackground(new Color(245, 245, 255));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        JOptionPane.showMessageDialog(this, scrollPane,
                "Transaction Retrieval Results", JOptionPane.INFORMATION_MESSAGE);
    }

    // NEW: Update transaction history display
    void updateTransactionHistory() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("      ALL TRANSACTIONS RECORD\n");
        sb.append("========================================\n\n");

        if (allTransactions.isEmpty()) {
            sb.append("No transactions yet.\n");
        } else {
            for (int i = allTransactions.size() - 1; i >= 0; i--) {
                Transaction tx = allTransactions.get(i);
                sb.append("----------------------------------------\n");
                sb.append("Tx ID:    " + tx.txId + "\n");
                sb.append("From:     " + tx.sender + "\n");
                sb.append("To:       " + tx.receiver + "\n");
                sb.append("Amount:   " + tx.amount + "\n");
                sb.append("Time:     " + tx.timestamp + "\n");

                // Check if mined or pending
                boolean isMined = false;
                for (Block b : blockchain) {
                    if (b.transactions.contains(tx)) {
                        isMined = true;
                        sb.append("Status:   MINED (Block #" + b.blockNumber + ")\n");
                        break;
                    }
                }
                if (!isMined) {
                    sb.append("Status:   PENDING (In Mempool)\n");
                }
                sb.append("----------------------------------------\n\n");
            }
        }

        sb.append("\nTotal Transactions: " + allTransactions.size() + "\n");
        sb.append("========================================\n");

        txtTransactionHistory.setText(sb.toString());
        txtTransactionHistory.setCaretPosition(0);
    }

    void updateMempoolList() {
        listModel.clear();
        for (Transaction tx : mempool) {
            listModel.addElement(tx.toString() + " [" + tx.txId + "]");
        }
    }

    void updateBalanceTable() {
        tableModel.setRowCount(0);
        for (String wallet : balances.keySet()) {
            double bal = balances.get(wallet);
            tableModel.addRow(new Object[]{wallet, bal});
        }
    }

    void showBlockchain() {
        StringBuilder sb = new StringBuilder();

        sb.append("========================================\n");
        sb.append("         BLOCKCHAIN RECORD\n");
        sb.append("========================================\n\n");

        for (int i = 0; i < blockchain.size(); i++) {
            Block b = blockchain.get(i);

            sb.append("----------------------------------------\n");
            sb.append("BLOCK #" + b.blockNumber + "\n");
            sb.append("----------------------------------------\n");
            sb.append("Hash:        " + b.currentHash + "\n");
            sb.append("Prev Hash:   " + b.previousHash + "\n");
            sb.append("Timestamp:   " + b.timestamp + "\n");
            sb.append("Transactions:" + b.transactions.size() + "\n\n");

            for (int j = 0; j < b.transactions.size(); j++) {
                Transaction tx = b.transactions.get(j);
                sb.append("  [" + (j+1) + "] " + tx.toString() + " [" + tx.txId + "]\n");
            }

            sb.append("\n");
        }

        sb.append("========================================\n");
        sb.append("Total Blocks: " + blockchain.size() + "\n");
        sb.append("========================================\n");

        txtBlockchain.setText(sb.toString());
        txtBlockchain.setCaretPosition(0);
    }

    void showStatus(String message, Color color) {
        lblStatus.setText(message);
        lblStatus.setForeground(color);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                BlockchainTransactionSimulator app = new BlockchainTransactionSimulator();

                app.addWallet("Ali", 8300.0);
                app.addWallet("Jahanzaib", 1120.0);
                app.addWallet("Shahzaib", 5200.0);
                app.addWallet("Hassan", 13000.0);
                app.addWallet("Ahmad", 1000);
                app.addWallet("Imran", 9800);
                app.addWallet("Zainab", 5400);

                app.setVisible(true);
            }
        });
    }
}