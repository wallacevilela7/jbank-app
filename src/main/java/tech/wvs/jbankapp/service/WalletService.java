package tech.wvs.jbankapp.service;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.wvs.jbankapp.controller.dto.*;
import tech.wvs.jbankapp.controller.enums.StatementOperation;
import tech.wvs.jbankapp.entities.Deposit;
import tech.wvs.jbankapp.entities.Wallet;
import tech.wvs.jbankapp.exception.DeleteWalletException;
import tech.wvs.jbankapp.exception.StatementException;
import tech.wvs.jbankapp.exception.WalletDataAlreadyExistsException;
import tech.wvs.jbankapp.exception.WalletNotFoundException;
import tech.wvs.jbankapp.repository.DepositRepository;
import tech.wvs.jbankapp.repository.WalletRepository;
import tech.wvs.jbankapp.repository.dto.StatementView;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final DepositRepository depositRepository;

    public WalletService(WalletRepository walletRepository, DepositRepository depositRepository) {
        this.walletRepository = walletRepository;
        this.depositRepository = depositRepository;
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

    public boolean deleteWallet(UUID walletId) {
        var wallet = walletRepository.findById(walletId);

        if (wallet.isPresent()) {

            if (wallet.get().getBalance().compareTo(BigDecimal.ZERO) != 0) {
                throw new DeleteWalletException("The balance is not zero, the current amount is $ " + wallet.get().getBalance());
            }

            walletRepository.deleteById(walletId);
        }

        return wallet.isPresent();
    }

    @Transactional
    public void createDeposit(UUID walletId,
                              CreateDepositDto data,
                              String ipAddress) {

        var wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("There is no wallet with this id"));

        var deposit = new Deposit();

        deposit.setDepositValue(data.value());
        deposit.setDepositDateTime(LocalDateTime.now());
        deposit.setIpAddress(ipAddress);
        deposit.setWallet(wallet);

        depositRepository.save(deposit);

        wallet.setBalance(wallet.getBalance().add(data.value()));

        walletRepository.save(wallet);
    }

    public StatementDto getStatements(UUID walletId, Integer page, Integer pageSize) {
        var wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("There is not wallet with this id"));

        //var pageRequest = PageRequest.of(page, pageSize, Sort.Direction.DESC, "statement_date_time");
        var pageRequest = PageRequest.of(page, pageSize);

        var statements = walletRepository.findStatements(walletId.toString(), pageRequest)
                .map(view -> mapToDto(walletId, view));

        return new StatementDto(
                new WalletDto(
                        wallet.getWalletId(),
                        wallet.getCpf(),
                        wallet.getName(),
                        wallet.getEmail(),
                        wallet.getBalance()
                ),
                statements.getContent(),
                new PaginationDto(
                        statements.getNumber(),
                        statements.getSize(),
                        statements.getTotalElements(),
                        statements.getTotalPages()
                )
        );
    }

    private StatementItemDto mapToDto(UUID walletId, StatementView view) {
        if (view.getType().equalsIgnoreCase("deposit")) {
            return mapToDeposit(view);
        }

        if (view.getType().equalsIgnoreCase("transfer")
                && view.getWalletSender().equalsIgnoreCase(walletId.toString())) {
            return mapToStatementItemWhenSender(walletId, view);
        }

        if (view.getType().equalsIgnoreCase("transfer")
                && view.getWalletReceiver().equalsIgnoreCase(walletId.toString())) {
            return mapToStatementItemWhenReceiver(walletId, view);
        }

        throw new StatementException("Invalid type " + view.getType());
    }

    private StatementItemDto mapToStatementItemWhenReceiver(UUID walletId, StatementView view) {
        return new StatementItemDto(
                view.getStatementId(),
                view.getType(),
                "money received from " + view.getWalletSender(),
                view.getStatementValue(),
                view.getStatementDateTime(),
                StatementOperation.DEBIT
        );
    }

    private StatementItemDto mapToStatementItemWhenSender(UUID walletId, StatementView view) {
        return new StatementItemDto(
                view.getStatementId(),
                view.getType(),
                "money sent to " + view.getWalletReceiver(),
                view.getStatementValue(),
                view.getStatementDateTime(),
                StatementOperation.DEBIT
        );
    }

    private static StatementItemDto mapToDeposit(StatementView view) {
        return new StatementItemDto(
                view.getStatementId(),
                view.getType(),
                "money deposit",
                view.getStatementValue(),
                view.getStatementDateTime(),
                StatementOperation.CREDIT
        );
    }
}
