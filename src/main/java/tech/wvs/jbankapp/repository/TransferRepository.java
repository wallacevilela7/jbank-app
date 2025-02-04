package tech.wvs.jbankapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.wvs.jbankapp.entities.Transfer;

import java.util.UUID;

public interface TransferRepository extends JpaRepository<Transfer, UUID> {
}
