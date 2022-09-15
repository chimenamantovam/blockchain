package br.blockchain.model;

import java.util.ArrayList;
import java.util.Date;

import com.google.gson.Gson;

import br.blockchain.utils.StringUtil;


public class Block {

	private Date timestamp;
	private ArrayList<Transaction> transactions;
	private String previousHash;
	private Integer nonce;
	private String hash;

	/**
	 * 
	 * @param timestamp
	 * @param transactions
	 * @param previousHash
	 */
	public Block(Date timestamp, ArrayList<Transaction> transactions, String previousHash) {
		this.timestamp = timestamp;
		this.transactions = transactions;
		this.previousHash = previousHash;
		this.nonce = 0;
		this.hash = this.calculateHash();
	}

	/**
	 * Create a SHA-256 hash of the block.
	 * 
	 * @return String
	 */
	public String calculateHash() {
		return StringUtil
				.applySha256(this.previousHash + this.timestamp + new Gson().toJson(this.transactions) + this.nonce);
	}

	/**
	 * The miner process on the block.
	 * 
	 * @param difficulty
	 */
	public void mineBlock(int difficulty) {
		String target = new String(new char[difficulty]).replace('\0', '0');
		while (!hash.substring(0, difficulty).equals(target)) {
			this.nonce++;
			this.hash = calculateHash();
		}
	}

	/**
	 * Validate all the transactions inside the block.
	 * 
	 * @return boolean
	 */
	public boolean hasValidTransactions() {
		for (Transaction tx : this.transactions) {
			if (!tx.isValid()) {
				return false;
			}
		}
		return true;
	}

	protected String getHash() {
		return hash;
	}

	protected ArrayList<Transaction> getTransactions() {
		return transactions;
	}

	protected String getPreviousHash() {
		return previousHash;
	}

}
