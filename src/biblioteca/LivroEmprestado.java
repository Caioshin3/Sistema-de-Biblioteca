package biblioteca;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class LivroEmprestado implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Livro livro;
    private LocalDate dataEmprestimo;
    private LocalDate dataVencimento;

    public LivroEmprestado(Livro livro, LocalDate dataEmprestimo, LocalDate dataVencimento){
        this.livro = livro;
        this.dataEmprestimo = dataEmprestimo;
        this.dataVencimento = dataVencimento;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public void setDataEmprestimo(LocalDate dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }
}
