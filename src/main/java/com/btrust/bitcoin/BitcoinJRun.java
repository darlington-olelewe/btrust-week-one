package com.btrust.bitcoin;

import org.bitcoinj.core.*;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;

public class BitcoinJRun {

    public static void main(String[] args) {
        createP2PKHWallet();
    }

    public static void createP2PKHWallet() {
        try {
            // Use the test network. For mainnet, use NetworkParameters.fromID(NetworkParameters.ID_MAINNET).
            NetworkParameters networkParameters = TestNet3Params.get();
            List<String> mnemonicCode = MnemonicCode.INSTANCE.toMnemonic(SecureRandom.getSeed(16 * 8));

            // Create a deterministic seed from the mnemonic
            DeterministicSeed seed = new DeterministicSeed(mnemonicCode, null, "", System.currentTimeMillis());

            // Create a wallet from the seed
            Wallet wallet = Wallet.fromSeed(networkParameters, seed);


            // Get the first key in the wallet (you can generate more keys as needed)
            ECKey key = wallet.freshReceiveKey();

            // Create a P2PKH address using the public key
            Address address = LegacyAddress.fromKey(networkParameters, key);

            // Get the private key in Wallet Import Format (WIF)
            String privateKeyWIF = key.getPrivateKeyEncoded(networkParameters).toBase58();

            System.out.println("| Public Address | " + address + " |");
            System.out.println("| Private Key    | " + privateKeyWIF + " |");

            // Save wallet information to a JSON file
            saveWalletToJson(address.toString(), privateKeyWIF);

            System.out.println("Wallet created and saved to wallet.json");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MnemonicException.MnemonicLengthException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveWalletToJson(String address, String privateKey) throws IOException {
        WalletData walletData = new WalletData(address, privateKey);
        File file = new File("wallet.json");

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(walletData.toJson());
        }
    }

    static class WalletData {
        private final String address;
        private final String privateKey;

        public WalletData(String address, String privateKey) {
            this.address = address;
            this.privateKey = privateKey;
        }

        public String toJson() {
            return String.format("{\n  \"address\": \"%s\",\n  \"privateKey\": \"%s\"\n}", address, privateKey);
        }
    }
}

