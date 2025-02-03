package tech.wvs.jbankapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.wvs.jbankapp.entities.Wallet;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    Optional<Wallet> findByCpfOrEmail(String cpf, String email);
}
