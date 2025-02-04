package tech.wvs.jbankapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.wvs.jbankapp.entities.Deposit;

import java.util.UUID;

public interface DepositRepository extends JpaRepository<Deposit, UUID> {
}
