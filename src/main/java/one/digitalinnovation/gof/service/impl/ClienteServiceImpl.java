package one.digitalinnovation.gof.service.impl;

import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.ClienteRepository;
import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.model.EnderecoRepository;
import one.digitalinnovation.gof.service.ClienteService;
import one.digitalinnovation.gof.service.ViaCepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Implementação da <b>Strategy</b> {@link ClienteService}, a qual pode ser
 * injetada pelo Spring (via {@link Autowired}). Com isso, como essa classe
 * é um {@link Service}, ela será tratada como um <b>Singleton</b>.
 * 
 * @author Anderson
 */

@Service
public class ClienteServiceImpl implements ClienteService {
    private static final Logger logger = LoggerFactory.getLogger(ClienteServiceImpl.class);

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private ViaCepService viaCepService;

    @Override
    public Iterable<Cliente> buscarTodos() {
        logger.info("Buscando todos os clientes...");
        return clienteRepository.findAll();
    }

    @Override
    public Cliente buscarPorId(Long id) {
        logger.info("Buscando cliente pelo ID: {}", id);
        Optional<Cliente> cliente = clienteRepository.findById(id);
        if (cliente.isPresent()) {
            logger.info("Cliente encontrado: {}", cliente.get());
        } else {
            logger.warn("Cliente com ID {} não encontrado.", id);
        }
        return cliente.orElse(null);
    }

    @Override
    public Cliente inserir(Cliente cliente) {
        logger.info("Inserindo novo cliente: {}", cliente);
        salvarClienteComCep(cliente);
        Cliente salvo = clienteRepository.save(cliente);
        logger.info("Cliente inserido com sucesso: {}", salvo);
        return salvo;
    }

    @Override
    public void atualizar(Long id, Cliente cliente) {
        logger.info("Atualizando cliente com ID: {}", id);
        Optional<Cliente> clienteBd = clienteRepository.findById(id);
        if (clienteBd.isPresent()) {
            logger.info("Cliente encontrado para atualização: {}", clienteBd.get());
            salvarClienteComCep(cliente);
            cliente.setId(id); // Certifique-se de manter o ID do cliente
            Cliente atualizado = clienteRepository.save(cliente);
            logger.info("Cliente atualizado com sucesso: {}", atualizado);
        } else {
            logger.warn("Cliente com ID {} não encontrado para atualização.", id);
        }
    }

    @Override
    public void deletar(Long id) {
        logger.info("Deletando cliente com ID: {}", id);
        Optional<Cliente> cliente = clienteRepository.findById(id);
        if (cliente.isPresent()) {
            clienteRepository.deleteById(id);
            logger.info("Cliente com ID {} deletado com sucesso.", id);
        } else {
            logger.warn("Cliente com ID {} não encontrado para exclusão.", id);
        }
    }

    private void salvarClienteComCep(Cliente cliente) {
        String cep = cliente.getEndereco().getCep();
        logger.info("Salvando cliente com CEP: {}", cep);

        // Verificar se o Endereco do Cliente já existe (pelo CEP).
        Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
            logger.info("Endereço com CEP {} não encontrado no banco. Consultando API ViaCEP...", cep);
            Endereco novoEndereco = viaCepService.consultarCep(cep);
            if(novoEndereco.getCep() == null){
                throw new RuntimeException("CEP inválido ou não encontrado");
            }
            logger.info("Endereço retornado pela API ViaCEP: {}", novoEndereco);
            enderecoRepository.save(novoEndereco);
            logger.info("Endereço salvo no banco: {}", novoEndereco);
            return novoEndereco;
        });

        cliente.setEndereco(endereco);
        logger.info("Cliente associado ao endereço: {}", cliente);
    }
}