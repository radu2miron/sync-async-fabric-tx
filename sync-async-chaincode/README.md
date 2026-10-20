# Freight as a Service (FaaS) - Hyperledger Fabric Chaincode

This repository contains the chaincode for the SyncAsync application, which is built on top of Hyperledger Fabric. 
The chaincode is responsible for managing LifecycleEvents.

## Steps to build, deploy and run the chaincode
### 1. Build chaincode
1.1. Build sync-async-chaincode: 
```mvn clean install```

1.2. Copy ```target/sync-async-chaincode-0.0.1.tar.gz``` to ```../fabric/test-network/sync-async-chaincode-0.0.1.tar.gz```

### 2. Open terminal and run the following commands to set up the environment variables:
```
export CONTAINER_CLI_COMPOSE="docker compose"
cd ../fabric/test-network
export PATH=${PWD}/../bin:$PATH
export FABRIC_CFG_PATH=$PWD/../config/
peer version
```

### 3. Bring network up and create channel
```
./network.sh up -s couchdb
./network.sh createChannel -c sync-async-channel
```

### 4. Install chaincode on peers
4.1. Install chaincode on Org1 peer
```
export CORE_PEER_TLS_ENABLED=true
export CORE_PEER_LOCALMSPID="Org1MSP"
export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt
export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
export CORE_PEER_ADDRESS=localhost:7051

peer lifecycle chaincode install sync-async-chaincode-0.0.1.tar.gz
```

4.2. Install chaincode on Org2 peer
```
export CORE_PEER_LOCALMSPID="Org2MSP"
export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt
export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp
export CORE_PEER_ADDRESS=localhost:9051

peer lifecycle chaincode install sync-async-chaincode-0.0.1.tar.gz
```

### 5. Approve chaincode definition
5.1. Setup chaincode environment variables
```
peer lifecycle chaincode queryinstalled
export CC_PACKAGE_ID=<REPLACE WITH PACKAGE ID, E.G. sync-async-chaincode-0.0.1:69de748301770f6ef64b42aa6bb6cb291df20aa39542c3ef94008615704007f3>
```

5.2. Approve chaincode for Org2 - env. variables already setup for this org at previous step
```
peer lifecycle chaincode approveformyorg -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --channelID sync-async-channel --name sync-async-chaincode --version 1.0 --package-id $CC_PACKAGE_ID --sequence 1 --tls --cafile "${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem"
```

5.3. Approve chaincode for Org1
```
export CORE_PEER_LOCALMSPID="Org1MSP"
export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt
export CORE_PEER_ADDRESS=localhost:7051

peer lifecycle chaincode approveformyorg -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --channelID sync-async-channel --name sync-async-chaincode --version 1.0 --package-id $CC_PACKAGE_ID --sequence 1 --tls --cafile "${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem"
```

### 6. Commit chaincode
```
peer lifecycle chaincode checkcommitreadiness --channelID sync-async-channel --name sync-async-chaincode --version 1.0 --sequence 1 --tls --cafile "${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem" --output json
peer lifecycle chaincode commit -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --channelID sync-async-channel --name sync-async-chaincode --version 1.0 --sequence 1 --tls --cafile "${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem" --peerAddresses localhost:7051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt" --peerAddresses localhost:9051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt"
peer lifecycle chaincode querycommitted --channelID sync-async-channel --name sync-async-chaincode
```

### 7. Invoke/test chaincode
```
peer chaincode invoke -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile "${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem" -C sync-async-channel -n sync-async-chaincode --peerAddresses localhost:7051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt" --peerAddresses localhost:9051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt" -c '{"function":"createAsset","Args":["{\"id\":\"id1\",\"name\":\"asset1\",\"owner\":\"Diana\",\"status\":\"NEW\"}"]}'
peer chaincode invoke -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile "${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem" -C sync-async-channel -n sync-async-chaincode --peerAddresses localhost:7051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt" --peerAddresses localhost:9051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt" -c '{"function":"updateAsset","Args":["{\"id\":\"id1\",\"name\":\"asset1\",\"owner\":\"MARA\",\"status\":\"UPDATED\"}"]}'
peer chaincode invoke -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile "${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem" -C sync-async-channel -n sync-async-chaincode --peerAddresses localhost:7051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt" --peerAddresses localhost:9051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt" -c '{"function":"readAsset","Args":["id1"]}'
```

### 8. Stop/Stop and clean up
To only stop the network (and keep data), run:
```
./network.sh stop
```

To stop the network and clean up the volumes (i.e. erase data), run:
```
./network.sh down
```
