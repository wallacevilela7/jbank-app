package tech.wvs.jbankapp.controller;

import org.springframework.beans.factory.parsing.Problem;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.wvs.jbankapp.controller.dto.CreateWalletDto;
import tech.wvs.jbankapp.exception.WalletDataAlreadyExistsException;
import tech.wvs.jbankapp.service.WalletService;

import java.net.URI;

@RestController
@RequestMapping(path = "/wallets")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<Void> createWallet(@RequestBody CreateWalletDto data) {
        var wallet = walletService.createWallet(data);
        return ResponseEntity.created(URI.create("/wallets/" + wallet.getWalletId().toString())).build();
    }

    @ExceptionHandler(WalletDataAlreadyExistsException.class)
    public ProblemDetail handleWalletDataAlreadyExistsException(WalletDataAlreadyExistsException e) {
        var pd  = ProblemDetail.forStatus(422);

        pd.setTitle("Wallet data already exists");
        pd.setDetail(e.getMessage());

        return pd;
    }
}
