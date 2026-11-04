package oficinaflow.web;

import java.sql.SQLException;
import java.time.format.DateTimeParseException;

/** Mensagens de erro claras para o usuário (RNF10). */
final class Mensagens {

    private Mensagens() { }

    static String de(Exception e) {
        if (e instanceof NumberFormatException) {
            return "Valor numérico inválido.";
        }
        if (e instanceof DateTimeParseException) {
            return "Data inválida (use AAAA-MM-DD).";
        }
        if (e instanceof NullPointerException) {
            return "Dados incompletos: preencha os campos do item.";
        }
        if (e instanceof SQLException s) {
            String estado = s.getSQLState() == null ? "" : s.getSQLState();
            if (estado.equals("23505")) {
                return "Já existe um registro com esse CPF/CNPJ, placa ou código.";
            }
            if (estado.equals("23503")) {
                return "Operação não permitida: o registro está vinculado a outros dados (ex.: ordens de serviço). "
                    + "Para clientes, altere a situação para Inativo.";
            }
            return "Erro de banco de dados: " + s.getMessage();
        }
        return e.getMessage();
    }
}
