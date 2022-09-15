package br.blockchain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class Blockchain {

	private ArrayList<Block> chain = new ArrayList<Block>();
	private int difficulty;
	private ArrayList<Transaction> pendingTransactions;
	private BigDecimal miningReward;

	public Blockchain() {
		this.chain.add(createGenesisBlock());
		this.difficulty = 2;
		this.pendingTransactions = new ArrayList<Transaction>();
		this.miningReward = new BigDecimal(100);
	}

	/**
	 * Create the Genisis Block
	 * 
	 * @return Block
	 */
	public Block createGenesisBlock() {
		return new Block(new Date(), new ArrayList<Transaction>(), "0");
	}

	/**
	 * Return the latest block of the chain.
	 * 
	 * @return Block
	 */
	public Block getLatestBlock() {
		return chain.get(chain.size() - 1);
	}

	/**
	 * Takes all the pending transactions, puts them in a Block, and starts the
	 * mining process.
	 * 
	 * @param miningRewardAddress
	 */
	public void minePendingTransactions(String miningRewardAddress) {
		Transaction rewardTx = new Transaction(null, miningRewardAddress, this.miningReward);
		this.pendingTransactions.add(rewardTx);
		Block block = new Block(new Date(), this.pendingTransactions, this.getLatestBlock().getHash());
		block.mineBlock(this.difficulty);
		this.chain.add(block);
		this.pendingTransactions = new ArrayList<Transaction>();
	}

	/**
	 * Add a new transaction to the pending transactions list.
	 * 
	 * @param transaction
	 */
	public void addTransaction(Transaction transaction) {
		if (transaction.getFromAddress() == null || transaction.getToAddress() == null) {
			throw new Error("Transaction mus include from and to address.");
		}
		if (!transaction.isValid()) {
			throw new Error("Cannot add invalid transaction to chain.");
		}
		if (transaction.getAmount().compareTo(new BigDecimal(1)) == -1) {
			throw new Error("Transaction amount should be higher than 0.");
		}

		BigDecimal walletBalance = this.getBalanceOfAddress(transaction.getFromAddress());
		if (walletBalance.compareTo(transaction.getAmount()) == -1) {
			throw new Error("Not enough balance.");
		}

		ArrayList<Transaction> pendingTxForWallet = new ArrayList<Transaction>();
		for (Transaction trans : this.pendingTransactions) {
			if (trans.getFromAddress().equals(transaction.getFromAddress())) {
				pendingTxForWallet.add(trans);
			}
		}

		BigDecimal totalPendingAmount = new BigDecimal(0);
		if (pendingTxForWallet.size() > 0) {
			for (Transaction tx : pendingTxForWallet) {
				totalPendingAmount = totalPendingAmount.add(tx.getAmount());
			}
			BigDecimal totalAmount = totalPendingAmount.add(transaction.getAmount());
			if (walletBalance.compareTo(totalAmount) == -1) {
				throw new Error("Pending transactions for this wallet are higher than the balance.");
			}
		}

		this.pendingTransactions.add(transaction);
	}

	/**
	 * Return the balance of a given wallet address
	 * 
	 * @param address
	 * @return BigDecimal
	 */
	public BigDecimal getBalanceOfAddress(String address) {
		BigDecimal balance = new BigDecimal(0);
		for (int i = 0; i < chain.size(); i++) {
			Block block = chain.get(i);
			for (int j = 0; j < block.getTransactions().size(); j++) {
				Transaction trans = block.getTransactions().get(j);
				if (trans.getFromAddress() != null && trans.getFromAddress().equals(address)) {
					balance = balance.subtract(trans.getAmount());
				}
				if (trans.getToAddress() != null && trans.getToAddress().equals(address)) {
					balance = balance.add(trans.getAmount());
				}
			}
		}
		return balance;
	}

	/**
	 * Return all transactions from the given wallet address.
	 * 
	 * @param address
	 * @return ArrayList<Transaction>
	 */
	public ArrayList<Transaction> getAllTransactionsForWallet(String address) {
		ArrayList<Transaction> txs = new ArrayList<Transaction>();
		for (int i = 0; i <= chain.size(); i++) {
			Block block = chain.get(i);
			for (int j = 0; j <= block.getTransactions().size(); j++) {
				Transaction tx = block.getTransactions().get(j);
				if (tx.getFromAddress().equals(address) || tx.getToAddress().equals(address)) {
					txs.add(tx);
				}
			}
		}
		return txs;
	}

	/**
	 * Verify if the chain is valid.
	 * 
	 * @return boolean
	 */
	public boolean isChainValid() {
		for (int i = 1; i < this.chain.size(); i++) {
			Block currentBlock = this.chain.get(i);
			Block previousBlock = this.chain.get(i - 1);

			if (!currentBlock.hasValidTransactions()) {
				return false;
			}

			if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
				return false;
			}
			if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
				return false;
			}
		}
		return true;
	}
}
