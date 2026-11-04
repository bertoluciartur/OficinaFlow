package oficinaflow.service;

import oficinaflow.model.OrdemServico;

@org.springframework.stereotype.Service
public class ValidadorOrdemServico {

    public void validar(OrdemServico os) {
        if (os.getCliente() == null) {
            throw new IllegalArgumentException("A ordem de serviço precisa de um cliente.");
        }
        if (!os.getCliente().isAtivo()) {
            throw new IllegalArgumentException("O cliente está inativo e não pode receber novas ordens de serviço.");
        }
        if (os.getVeiculo() == null) {
            throw new IllegalArgumentException("A ordem de serviço precisa de um veículo.");
        }
        if (os.getVeiculo().getProprietario().getId() != os.getCliente().getId()) {
            throw new IllegalArgumentException("O veículo informado não pertence ao cliente.");
        }
        if (os.getMecanicos().isEmpty()) {
            throw new IllegalArgumentException("Informe ao menos um mecânico responsável.");
        }
    }
}
