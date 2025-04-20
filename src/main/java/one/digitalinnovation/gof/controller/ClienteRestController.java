package one.digitalinnovation.gof.controller;

import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Esse {@link RestController} representa nossa <b>Facade</b>, pois abstrai
 * toda a complexidade de integrações (Banco de Dados H2 e API do ViaCEP) em
 * uma interface simples e coesa (API REST).
 * 
 * @author Anderson
 */
@RestController
@RequestMapping("clientes")
@Validated
public class ClienteRestController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<Iterable<Cliente>> buscarTodos() {
        return ResponseEntity.ok(clienteService.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        Cliente cliente = clienteService.buscarPorId(id);
        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cliente);
    }

    @PostMapping
    public ResponseEntity<Cliente> inserir(@Valid @RequestBody Cliente cliente) {
        Cliente salvo = clienteService.inserir(cliente);
        return ResponseEntity.ok(salvo);
    }

    @PutMapping("/{id}")
public ResponseEntity<Cliente> atualizar(@PathVariable Long id, @Valid @RequestBody Cliente cliente) {
    Cliente existente = clienteService.buscarPorId(id);
    if (existente == null) {
        return ResponseEntity.notFound().build();
    }
    clienteService.atualizar(id, cliente);
    Cliente atualizado = clienteService.buscarPorId(id);
    return ResponseEntity.ok(atualizado);
}


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        Cliente cliente = clienteService.buscarPorId(id);
        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}