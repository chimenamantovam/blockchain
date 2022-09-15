package br.blockchain.model;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Date;

import br.blockchain.utils.StringUtil;



public class Transaction {

	private String fromAddress;
	private String toAddress;
	private BigDecimal amount;
	private Date timestamp;
	private byte[] signature;
	private PublicKey pubKey;
	private byte[] sig;

	/**
	 * 
	 * @param fromAddress
	 * @param toAddress
	 * @param amount
	 */
	public Transaction(String fromAddress, String toAddress, BigDecimal amount) {
		this.fromAddress = fromAddress;
		this.toAddress = toAddress;
		this.amount = amount;
		this.timestamp = new Date();
	}

	/**
	 * Create a SHA-256 hash of the transaction.
	 * 
	 * @return String
	 */
	public String calculateHash() {
		return StringUtil.applySha256(getFromAddress() + getFromAddress() + getAmount() + getTimestamp());
	}

	/**
	 * Signs a transaction.
	 * 
	 * @param privKey
	 * @param pubKey
	 * @param sig
	 */
	public void signTransaction(PrivateKey privKey, PublicKey pubKey, byte[] sig) {
		this.pubKey = pubKey;
		this.sig = sig;
		Signature sg;
		try {
			sg = Signature.getInstance("SHA256withECDSA", "SunEC");
			sg.initVerify(pubKey);
			sg.update(this.fromAddress.getBytes("UTF-8"));
			if (!sg.verify(sig)) {
				throw new Error("You cannot sign transaction for other wallets.");
			}
			String hashTx = this.calculateHash();
			Signature s = Signature.getInstance("SHA256withECDSA", "SunEC");
			s.initSign(privKey);
			s.update(hashTx.getBytes("UTF-8"));
			this.signature = s.sign();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Verify whether the signature is valid.
	 * 
	 * @return boolean
	 */
	public boolean isValid() {
		boolean resp = false;
		if (getFromAddress() == null)
			return true;
		if (this.signature.length == 0) {
			throw new Error("No signature in this transaction.");
		}
		Signature sg;
		try {
			sg = Signature.getInstance("SHA256withECDSA", "SunEC");
			sg.initVerify(this.pubKey);
			sg.update(this.toString().getBytes("UTF-8"));
			resp = !sg.verify(this.sig);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return resp;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public Date getTimestamp() {
		return timestamp;
	}
}
