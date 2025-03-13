package com.ciarancumiskey.mockitobank.controllers;

import com.ciarancumiskey.mockitobank.exceptions.InvalidArgumentsException;
import com.ciarancumiskey.mockitobank.exceptions.NotFoundException;
import com.ciarancumiskey.mockitobank.models.TransactionRequest;
import com.ciarancumiskey.mockitobank.utils.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Transaction operations")
@RequestMapping(path = Constants.TRANSACTIONS_PATH)
public interface ITransactionController {

    @Operation(summary = "Transfer money")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Money successfully transferred",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Payee/payer not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal service error",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ResponseEntity.class)))
            })
    @PostMapping(value = Constants.TRANSFER_PATH, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    String transferMoney(@Valid @RequestBody final TransactionRequest transaction) throws NotFoundException, InvalidArgumentsException;
}
