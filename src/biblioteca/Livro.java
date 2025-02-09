package biblioteca;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class Livro implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;
    private String titulo;
    private String autor;
    private String editora;
    private int anoDePublicacao;
    private int ISBN;
    private String categoria;
    private int numeroDeExemplares = 0;
    private int numeroDeSolicitacoes = 0;
    private boolean isNew;
    private LocalDate dataAdicao;


    public Livro(){
    }

    public Livro(Livro livro) {
        this.titulo = livro.titulo;
        this.autor = livro.autor;
        this.editora = livro.editora;
        this.anoDePublicacao = livro.anoDePublicacao;
        this.ISBN = livro.ISBN;
        this.categoria = livro.categoria;
        this.numeroDeExemplares = livro.numeroDeExemplares;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getEditora() {
        return editora;
    }

    public void setEditora(String editora) {
        this.editora = editora;
    }

    public int getAnoDePublicacao() {
        return anoDePublicacao;
    }

    public void setAnoDePublicacao(int anoDePublicacao) {
        this.anoDePublicacao = anoDePublicacao;
    }

    public int getISBN() {
        return ISBN;
    }

    public void setISBN(int ISBN) {
        this.ISBN = ISBN;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getNumeroDeExemplares() {
        return numeroDeExemplares;
    }

    public void setNumeroDeExemplares(int numeroDeExemplares) {
        this.numeroDeExemplares = numeroDeExemplares;
    }

    public int getNumeroDeSolicitacoes() {
        return numeroDeSolicitacoes;
    }

    public void setNumeroDeSolicitacoes(int numeroDeSolicitacoes) {
        this.numeroDeSolicitacoes = numeroDeSolicitacoes;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public LocalDate getDataAdicao() {
        return dataAdicao;
    }

    public void setDataAdicao(LocalDate dataAdicao) {
        this.dataAdicao = dataAdicao;
    }
}

