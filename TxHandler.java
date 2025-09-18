public class TxHandler {

	/* Creates a public ledger whose current UTXOPool (collection of unspent 
	 * transaction outputs) is utxoPool. This should make a defensive copy of 
	 * utxoPool by using the UTXOPool(UTXOPool uPool) constructor.
	 */
	private UTXOPool utxoPool;
	public TxHandler(UTXOPool utxoPool) {
		this.utxoPool = new UTXOPool(utxoPool);
	}

	/* Returns true if 
	 * (1) all outputs claimed by tx are in the current UTXO pool, 
	 * (2) the signatures on each input of tx are valid, 
	 * (3) no UTXO is claimed multiple times by tx, 
	 * (4) all of tx’s output values are non-negative, and
	 * (5) the sum of tx’s input values is greater than or equal to the sum of   
	        its output values;
	   and false otherwise.
	 */

	public boolean isValidTx(Transaction tx) {
		UTXOPool claimedUTXO = new UTXOPool();
		double inputsum = 0;
		double outputsum = 0;
		for(int i =0;i < tx.numInputs();i++){
			Transaction.Input in = tx.getInput(i);
			Coin utxo = new Coin(in.prevTxhash,out.in.outputIndex);
			if(!utxoPool.contains(utxo)){
				return false;
			}
			Transaction.Output prevOutput = utxoPool.getTxOutput(utxo);
			if(!Crypto.verifySignature(prevOutput.address, tx.getRawDataToSign(i),in.signature)){
				return false;
			}
			if(claimedUTXO.contains(utxo)){
				return false;
			}
			claimedUTXO.addCoin(utxo,prevOutput);
			inputSum += prevOutput.value;
		}
		for(Transaction.Output out : tx.getOutputs()) {
			if(out.value < 0) {
				return false;
			}
			outputSum += out.value;
		}
		if(inputSum < outputSum){
			return false;
		}

		return true;
	}

	/* Handles each epoch by receiving an unordered array of proposed 
	 * transactions, checking each transaction for correctness, 
	 * returning a mutually valid array of accepted transactions, 
	 * and updating the current UTXO pool as appropriate.
	 */
	public Transaction[] handleTxs(Transaction[] possibleTxs) {
		List<Transactions> acceptedTxs = new ArrayList<>();
		boolean progress == true;
		while(progress){
			progress = false;
			for (Transaction tx: possibleTxs) {
				if (acceptedTxss.contains(tx)) continue;
				if (isValidTx(tx)){
					acceptedTxs.add(tx);
					progress = true;
					for (Transaction.Input in : tx.getInputs()) {
						Coin utxo = new Coin(in.prevTxHash, in.outputIndex);
						utxoPool.removeCoin(utxo);
					}
					for(int i = 0; i < tx.numOutputs(); i++){
						Coin utxo = new Coin(tx.getHash(),i);
						utxoPool.addCoin(utxo, tx.getOutput(i));
					}
				}
			}
		}
		return acceptedTxs.toArray(new Transactions[0]);
	}

} 
