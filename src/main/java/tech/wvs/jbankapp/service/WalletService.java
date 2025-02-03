package tech.wvs.jbankapp.service;

import org.springframework.stereotype.Service;
import tech.wvs.jbankapp.controller.dto.CreateWalletDto;
import tech.wvs.jbankapp.entities.Wallet;
import tech.wvs.jbankapp.exception.WalletDataAlreadyExistsException;
import tech.wvs.jbankapp.repository.WalletRepository;

import java.math.BigDecimal;

@Service
public class WalletService {
    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet createWallet(CreateWalletDto data) {
        //valida se já existe uma wallet com os dados
        var walletDb = walletRepository.findByCpfOrEmail(data.cpf(), data.email());

        if (walletDb.isPresent()) {
            throw new WalletDataAlreadyExistsException("CPF or email already exists");
        }

        var wallet = new Wallet();
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setName(data.name());
        wallet.setCpf(data.cpf());
        wallet.setEmail(data.email());

        return walletRepository.save(wallet);
    }
}
