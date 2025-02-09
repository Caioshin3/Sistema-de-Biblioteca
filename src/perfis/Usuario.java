package perfis;
import biblioteca.Livro;
import javax.swing.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static biblioteca.Biblioteca.carregarLivros;
import static biblioteca.Biblioteca.getLivros;

public class Usuario implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String nome;
    private String senha;
    private String email;
    private boolean isAdmin;
    private boolean isLogged;
    private boolean autoLog;
    private String Id;

    public Usuario(){
        this("","","",false,false,null);
    }

    public Usuario(String nome, String senha, String email, boolean tipo, boolean autoLog,String id){
        this.nome = nome;
        this.senha = senha;
        this.email = email;
        isAdmin = tipo;
        this.autoLog = autoLog;
        this.Id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return nome.equals(usuario.nome) && senha.equals(usuario.senha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, senha);
    }

    public String getNome() {
        return nome;
    }

    public String getSenha(){
        return senha;
    }

    public String getEmail() {
        return email;
    }

    public boolean getPermissao(){
        return isAdmin;
    }

    public void setLogged(boolean logged) {
        isLogged = logged;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setAutoLog(boolean autoLog) {
        this.autoLog = autoLog;
    }

    public String getId() {
        return Id;
    }

    public boolean isAutoLog() {
        return autoLog;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setId(String id) {
        Id = id;
    }

    public static void exibirLivros() {
        carregarLivros();

        if (getLivros().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum livro encontrado.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Livro livro : getLivros()) {
            sb.append("ISBN: ").append(livro.getISBN()).append("\n")
                    .append("Título: ").append(livro.getTitulo()).append("\n")
                    .append("Autor: ").append(livro.getAutor()).append("\n")
                    .append("Editora: ").append(livro.getEditora()).append("\n")
                    .append("Ano de Publicação: ").append(livro.getAnoDePublicacao()).append("\n")
                    .append("Gênero: ").append(livro.getCategoria()).append("\n")
                    .append("Número de Exemplares: ").append(livro.getNumeroDeExemplares()).append("\n\n");
        }

        JOptionPane.showMessageDialog(null, sb.toString(), "Lista de Livros", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void buscaLivro() {
        carregarLivros();

        // Solicita ao usuário o critério de busca
        String[] criterios = {"ISBN", "Título", "Autor", "Editora", "Categoria"};
        String criterioEscolhido = (String) JOptionPane.showInputDialog(
                null,
                "Escolha o critério de busca:",
                "Buscar Livro",
                JOptionPane.QUESTION_MESSAGE,
                null,
                criterios,
                criterios[0]
        );

        if (criterioEscolhido == null) {
            JOptionPane.showMessageDialog(null, "Critério de busca não selecionado.");
            return;
        }

        String inputValor = null;

        if (criterioEscolhido.equals("Categoria")) {
            String[] categorias = {"Ficção", "Não-Ficção", "Ciência", "História", "Biografia"};
            List<String> selecionadas = new ArrayList<>();
            JList<String> categoriaList = new JList<>(categorias);
            categoriaList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            JOptionPane.showMessageDialog(null, new JScrollPane(categoriaList), "Selecione uma ou mais categorias", JOptionPane.QUESTION_MESSAGE);

            // Obtendo as categorias selecionadas
            for (String categoria : categoriaList.getSelectedValuesList()) {
                selecionadas.add(categoria);
            }

            if (selecionadas.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nenhuma categoria selecionada.");
                return;
            }

            inputValor = String.join(", ", selecionadas);
        } else {
            inputValor = JOptionPane.showInputDialog("Digite o valor do " + criterioEscolhido + " que deseja buscar:");

            if (inputValor == null || inputValor.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Valor de busca não fornecido.");
                return;
            }
        }

        Livro livroEncontrado = null;

        // Busca o livro de acordo com o critério escolhido
        for (Livro livro : getLivros()) {
            switch (criterioEscolhido) {
                case "ISBN":
                    try {
                        int isbn = Integer.parseInt(inputValor);
                        if (livro.getISBN() == isbn) {
                            livroEncontrado = livro;
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "O ISBN deve ser um número.");
                        return;
                    }
                    break;
                case "Título":
                    if (livro.getTitulo().equalsIgnoreCase(inputValor)) {
                        livroEncontrado = livro;
                    }
                    break;
                case "Autor":
                    if (livro.getAutor().equalsIgnoreCase(inputValor)) {
                        livroEncontrado = livro;
                    }
                    break;
                case "Editora":
                    if (livro.getEditora().equalsIgnoreCase(inputValor)) {
                        livroEncontrado = livro;
                    }
                    break;
                case "Categoria":
                    // Verifica se o livro possui uma das categorias selecionadas
                    String[] categoriasSelecionadas = inputValor.split(", ");
                    for (String categoriaSelecionada : categoriasSelecionadas) {
                        if (livro.getCategoria().equalsIgnoreCase(categoriaSelecionada)) {
                            livroEncontrado = livro;
                            break;
                        }
                    }
                    break;
            }

            // Se encontrar o livro, interrompe a busca
            if (livroEncontrado != null) {
                break;
            }
        }

        // Exibe o resultado da busca
        if (livroEncontrado != null) {
            JOptionPane.showMessageDialog(null, "Livro encontrado:\n" +
                    "Título: " + livroEncontrado.getTitulo() + "\n" +
                    "Autor: " + livroEncontrado.getAutor() + "\n" +
                    "Editora: " + livroEncontrado.getEditora() + "\n" +
                    "Ano de Publicação: " + livroEncontrado.getAnoDePublicacao() + "\n" +
                    "Categoria: " + livroEncontrado.getCategoria() + "\n" +
                    "Número de Exemplares: " + livroEncontrado.getNumeroDeExemplares());
        } else {
            JOptionPane.showMessageDialog(null, "Livro não encontrado.");
        }
    }
}


