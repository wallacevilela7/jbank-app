package tech.wvs.jbankapp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.wvs.jbankapp.controller.dto.CreateDepositDto;
import tech.wvs.jbankapp.controller.dto.CreateWalletDto;
import tech.wvs.jbankapp.controller.dto.StatementDto;
import tech.wvs.jbankapp.service.WalletService;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(path = "/wallets")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }
    @PostMapping
    public ResponseEntity<Void> createWallet(@RequestBody @Valid CreateWalletDto data) {
        var wallet = walletService.createWallet(data);
        return ResponseEntity.created(URI.create("/wallets/" + wallet.getWalletId().toString())).build();
    }

    @PostMapping(path = "/{walletId}/deposits")
    public ResponseEntity<Void> createDeposit(@PathVariable("walletId") UUID walletId,
                                              @RequestBody @Valid CreateDepositDto data,
                                              HttpServletRequest servletRequest) {

        walletService.createDeposit(
                walletId,
                data,
                servletRequest.getAttribute("x-user-ip").toString()
        );

        return ResponseEntity.ok().build();
    }


    @DeleteMapping(path = "/{walletId}")
    public ResponseEntity<Void> deleteWallet(@PathVariable UUID walletId) {
        var deleted = walletService.deleteWallet(walletId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/{walletId}/statements")
    public ResponseEntity<StatementDto> getStatements(@PathVariable("walletId") UUID walletId,
                                                      @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize){
        var statement = walletService.getStatements(walletId, page, pageSize);

        return ResponseEntity.ok(statement);
    }

}
