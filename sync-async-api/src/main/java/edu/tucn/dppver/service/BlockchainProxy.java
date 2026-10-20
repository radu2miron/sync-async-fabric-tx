package edu.tucn.dppver.service;

import edu.tucn.dppver.exceptions.BlockchainRelatedException;
import edu.tucn.dppver.model.TransactionRequest;
import edu.tucn.dppver.model.TxType;
import io.grpc.Channel;
import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.TlsChannelCredentials;
import jakarta.annotation.PostConstruct;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.identity.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:radu.miro@aut.utcluj.ro">Radu Miron</a>
 */
@Component
public final class BlockchainProxy {
    @Value("${blockchain.msp-id}")
    private String mspIdDefaultVal;
    private String mspId;

    @Value("${blockchain.channel-name}")
    private String channelNameDefaultVal;
    private String channelName;

    @Value("${blockchain.chaincode-name}")
    private String chaincodeNameDefaultVal;
    private String chaincodeName;

    // Path to crypto materials.
    @Value("${blockchain.crypto-path}")
    private String cryptoPathVal;
    private Path cryptoPath;

    // Path to user certificate.
    @Value("${blockchain.cert-path}")
    private String certPathVal;
    private Path certPath;

    // Path to user private key directory.
    @Value("${blockchain.key-dir-path}")
    private String keyDirPathVal;
    private Path keyDirPath;

    // Path to peer tls certificate.
    @Value("${blockchain.tls-cert-path}")
    private String tlsCertPathVal;
    private Path tlsCertPath;

    // Gateway peer end point.
    @Value("${blockchain.peer-endpoint}")
    private String peerEndpoint;

    @Value("${blockchain.override_authority}")
    private String overrideAuthority;

    @PostConstruct
    public void init() {
        this.mspId = System.getenv().getOrDefault("MSP_ID", mspIdDefaultVal);
        this.channelName = System.getenv().getOrDefault("CHANNEL_NAME", channelNameDefaultVal);
        this.chaincodeName = System.getenv().getOrDefault("CHAINCODE_NAME", chaincodeNameDefaultVal);
        this.cryptoPath = Paths.get(cryptoPathVal);
        this.certPath = cryptoPath.resolve(Paths.get(certPathVal));
        this.keyDirPath = cryptoPath.resolve(Paths.get(keyDirPathVal));
        this.tlsCertPath = cryptoPath.resolve(Paths.get(tlsCertPathVal));
    }

    public Contract getContract(Gateway gateway) {
        var network = gateway.getNetwork(channelName);
        return network.getContract(chaincodeName);
    }

    public String execute(TransactionRequest transactionRequest) {
        try {
            ManagedChannel channel = getNewChannel();

            try (var gateway = getGateway(channel)) {
                var resultAsByteArray = TxType.READ.equals(transactionRequest.getTxType()) ?
                        getContract(gateway).evaluateTransaction(transactionRequest.getFunction(), transactionRequest.getPayload()) :
                        getContract(gateway).submitTransaction(transactionRequest.getFunction(), transactionRequest.getPayload());

                return new String(resultAsByteArray, StandardCharsets.UTF_8);
            } finally {
                if (channel != null) {
                    channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
                }
            }
        } catch (Exception e) {
            throw new BlockchainRelatedException(e.getMessage());
        }
    }

    public Gateway getGateway(Channel channel) throws CertificateException, IOException, InvalidKeyException {
        var builder = Gateway.newInstance().identity(newIdentity()).signer(newSigner()).connection(channel)
                // Default timeouts for different gRPC calls
                .evaluateOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
                .endorseOptions(options -> options.withDeadlineAfter(15, TimeUnit.SECONDS))
                .submitOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
                .commitStatusOptions(options -> options.withDeadlineAfter(1, TimeUnit.MINUTES));
        return builder.connect();
    }

    public ManagedChannel getNewChannel() throws IOException {
        var credentials = TlsChannelCredentials.newBuilder()
                .trustManager(tlsCertPath.toFile())
                .build();
        return Grpc.newChannelBuilder(peerEndpoint, credentials)
                .overrideAuthority(overrideAuthority)
                .build();
    }

    private Identity newIdentity() throws IOException, CertificateException {
        var certReader = Files.newBufferedReader(certPath);
        var certificate = Identities.readX509Certificate(certReader);

        return new X509Identity(mspId, certificate);
    }

    private Signer newSigner() throws IOException, InvalidKeyException {
        var keyReader = Files.newBufferedReader(getPrivateKeyPath());
        var privateKey = Identities.readPrivateKey(keyReader);

        return Signers.newPrivateKeySigner(privateKey);
    }

    private Path getPrivateKeyPath() throws IOException {
        try (var keyFiles = Files.list(keyDirPath)) {
            return keyFiles.findFirst().orElseThrow();
        }
    }
}
