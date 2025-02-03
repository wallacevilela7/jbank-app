package tech.wvs.jbankapp.exception;

public class WalletDataAlreadyExistsException extends JBankException{
    public WalletDataAlreadyExistsException(String message) {
        super(message);
    }
}
