package biblioteca;

import perfis.Leitor;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class LogLivro implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public Livro livro;
    public boolean isafterDate;
    public boolean isDevolved;
    public boolean wasAccepted;

    public boolean isWasAccepted() {
        return wasAccepted;
    }

    public void setWasAccepted(boolean wasAccepted) {
        this.wasAccepted = wasAccepted;
    }

    public boolean isDevolved() {
        return isDevolved;
    }

    public void setDevolved(boolean devolved) {
        isDevolved = devolved;
    }

    public boolean isIsafterDate() {
        return isafterDate;
    }

    public void setIsafterDate(boolean isafterDate) {
        this.isafterDate = isafterDate;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }

    public static void atualizarOuAdicionarLog(Leitor leitor, Livro livroSolicitado, boolean isAccepted, boolean isafterDate) {
        if (livroSolicitado == null) {
            return;
        }

        LogLivro logAtualizado = null;
        if (leitor.getLogLivros() != null) {
            for (LogLivro log : leitor.getLogLivros()) {
                if (log.livro.getISBN() == livroSolicitado.getISBN()) {
                    logAtualizado = log;
                    break;
                }
            }
        }

        if (logAtualizado == null) {
            logAtualizado = new LogLivro();
            logAtualizado.livro = new Livro(livroSolicitado); // Cria uma cópia do livro solicitado
            if (leitor.getLogLivros() == null) {
                leitor.setLogLivros(new ArrayList<>());
            }
            leitor.getLogLivros().add(logAtualizado);
        }

        logAtualizado.wasAccepted = isAccepted;
        logAtualizado.isDevolved = false;
        logAtualizado.isafterDate = isafterDate;
    }
}
