package br.blockchain;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.util.Date;

import br.blockchain.model.Blockchain;
import br.blockchain.model.Transaction;


public class Executar {

	public static void main(String[] args) throws Exception {

		KeyPairGenerator g = KeyPairGenerator.getInstance("EC", "SunEC");
		ECGenParameterSpec ecsp = new ECGenParameterSpec("secp256k1");
		g.initialize(ecsp);
		KeyPair kp = g.genKeyPair();

		PrivateKey privKey = kp.getPrivate();
		PublicKey pubKey = kp.getPublic();

		String fromAddress = "0x8e8837a1ebf3cd04813773ce6e50f02756d74a6f";
		String toAddress = "0xca8d4799d2128656a6ce4b164950116e6f6e7113";
		Blockchain blockchain = new Blockchain();

		blockchain.minePendingTransactions(fromAddress);

		Transaction tx1 = new Transaction(fromAddress, toAddress, new BigDecimal(40));

		Signature s = Signature.getInstance("SHA256withECDSA", "SunEC");
		s.initSign(privKey);
		s.update(fromAddress.getBytes("UTF-8"));
		byte[] sig = s.sign();
		tx1.signTransaction(privKey, pubKey, sig);
		blockchain.addTransaction(tx1);

		System.out.println("Starting the miner...");
		blockchain.minePendingTransactions(fromAddress);
		System.out.println("Balance of address is " + blockchain.getBalanceOfAddress(fromAddress));
		System.out.println("Is chain Valid? " + (blockchain.isChainValid() ? "Yes" : "No").toString());
		System.out.println(new Date());

	}
}
