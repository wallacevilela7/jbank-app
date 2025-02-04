package tech.wvs.jbankapp.service;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wvs.jbankapp.controller.dto.TransferMoneyDto;
import tech.wvs.jbankapp.entities.Transfer;
import tech.wvs.jbankapp.entities.Wallet;
import tech.wvs.jbankapp.exception.TransferException;
import tech.wvs.jbankapp.exception.WalletNotFoundException;
import tech.wvs.jbankapp.repository.TransferRepository;
import tech.wvs.jbankapp.repository.WalletRepository;

import java.time.LocalDateTime;

@Service
public class TransferService {
    private final TransferRepository transferRepository;
    private final WalletRepository walletRepository;

    public TransferService(TransferRepository transferRepository, WalletRepository walletRepository) {
        this.transferRepository = transferRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional
    public void transferMoney(TransferMoneyDto data) {
        var receiver = walletRepository.findById(data.receiverId())
                .orElseThrow(() -> new WalletNotFoundException("Receiver does not exist"));

        var sender = walletRepository.findById(data.senderId())
                .orElseThrow(() -> new WalletNotFoundException("Sender does not exist"));

        if (sender.getBalance().compareTo(data.value()) == -1) {
            throw new TransferException("Insufficient balance. Your current balance is $" + sender.getBalance());
        }

        persistTransfer(data, receiver, sender);
        updateWallets(data, sender, receiver);
    }

    private void updateWallets(TransferMoneyDto data, Wallet sender, Wallet receiver) {
        sender.setBalance(sender.getBalance().subtract(data.value()));
        receiver.setBalance(receiver.getBalance().add(data.value()));

        walletRepository.save(sender);
        walletRepository.save(receiver);
    }

    private void persistTransfer(TransferMoneyDto data, Wallet receiver, Wallet sender) {
        var transfer = new Transfer();

        transfer.setReceiver(receiver);
        transfer.setSender(sender);
        transfer.setTransferValue(data.value());
        transfer.setTransferDateTime(LocalDateTime.now());

        transferRepository.save(transfer);
    }
}
