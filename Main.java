import java.security.*;
import javax.xml.bind.DatatypeConverter;

public class Main {
    public static void main(String[] args) {
        // Step 1: Create UTXO Pool
        UTXOPool utxoPool = new UTXOPool();

        // Step 2: Create and add some UTXOs to the UTXO Pool
        byte[] tx1Hash = "transaction1".getBytes();
        byte[] tx2Hash = "transaction2".getBytes();
        int outputIndex1 = 0;
        int outputIndex2 = 1;
        Transaction.Output txOut1 = new Transaction().new Output(10.0, getAddress(1)); // Replace null with a public key/address
        Transaction.Output txOut2 = new Transaction().new Output(5.0, getAddress(2));  // Replace null with a public key/address
        UTXO utxo1 = new UTXO(tx1Hash, outputIndex1);
        UTXO utxo2 = new UTXO(tx2Hash, outputIndex2);
        utxoPool.addUTXO(utxo1, txOut1);
        utxoPool.addUTXO(utxo2, txOut2);

        // Step 3: Create a Transaction
        Transaction tx = new Transaction();
        tx.addInput(tx1Hash, outputIndex1);
        tx.addOutput(7.0, getAddress(3)); // Replace null with a public key/address
        tx.addOutput(3.0, getAddress(4)); // Replace null with a public key/address

        // Step 4: Sign the Transaction
        KeyPair keyPair = getAddress(1); // Get the key pair corresponding to getAddress(1)
        byte[] signature = signData(tx.getRawDataToSign(0), keyPair.getPrivate());
        tx.addSignature(signature, 0); // Add the signature to the transaction

        // Finalize the transaction to calculate the transaction hash
        tx.finalize();

        // Step 5: Verify the Transaction's Validity
        boolean isValid = new TxHandler(utxoPool).isValidTx(tx);
        System.out.println("Is transaction valid? " + isValid);

        // Print transaction details for debugging
        System.out.println("Transaction Hash: " + bytesToHex(tx.getHash()));
        System.out.println("Raw Data To Sign: " + bytesToHex(tx.getRawDataToSign(0)));
        System.out.println("Signature: " + bytesToHex(signature));

        // Step 6: Handle Transactions in an Epoch
        Transaction[] possibleTxs = {tx};
        Transaction[] acceptedTxs = new TxHandler(utxoPool).handleTxs(possibleTxs);

        // Step 7: Print the Accepted Transactions
        System.out.println("Accepted Transactions:");
        for (Transaction acceptedTx : acceptedTxs) {
            System.out.println("Transaction Hash: " + bytesToHex(acceptedTx.getHash()));
        }
    }

    private static PublicKey getAddress(int addressNumber) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            return keyPair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // A placeholder method for generating a signature for the transaction data
    private static byte[] signData(byte[] data, PrivateKey privateKey) {
        // Replace this with your actual signature generation logic
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Helper method to convert a byte array to a hexadecimal string
    private static String bytesToHex(byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes);
    }
}