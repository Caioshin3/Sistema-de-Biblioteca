package perfis;
import biblioteca.Livro;
import biblioteca.LivroEmprestado;
import biblioteca.LogLivro;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import static biblioteca.Biblioteca.*;


public class Leitor extends Usuario implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    boolean isPenalty;
    boolean shouldNotify;
    boolean solicitaEmprestimo;
    Livro livroSolicitado;
    ArrayList<LivroEmprestado> livrosEmprestados;
    static ArrayList<LogLivro> logLivros;
    private int maxEmprestimos;
    private int registroLivrosEmprestados;

    public Leitor() {
        super("", "", "", false, true, "");
        this.registroLivrosEmprestados = 0;
        this.livrosEmprestados = new ArrayList<>();
        logLivros = new ArrayList<>();
    }

    public boolean isShouldNotify() {
        return shouldNotify;
    }

    public void setShouldNotify(boolean shouldNotify) {
        this.shouldNotify = shouldNotify;
    }

    public void setLivrosEmprestados(ArrayList<LivroEmprestado> livrosEmprestados) {
        this.livrosEmprestados = livrosEmprestados;
    }

    public void setSolicitaEmprestimo(boolean solicitaEmprestimo) {
        this.solicitaEmprestimo = solicitaEmprestimo;
    }

    public ArrayList<LivroEmprestado> getLivrosEmprestados() {
        return livrosEmprestados;
    }

    public boolean isSolicitaEmprestimo() {
        return solicitaEmprestimo;
    }

    public boolean isPenalty() {
        return isPenalty;
    }

    public Leitor(String nome, String senha, String email, boolean tipo, boolean log, int registroLivrosEmprestados, String id) {
        super(nome, senha, email, tipo, log, id);
        this.registroLivrosEmprestados = registroLivrosEmprestados;
        this.livrosEmprestados = new ArrayList<>();
        logLivros = new ArrayList<>();
    }

    public int getRegistroLivrosEmprestados() {
        return registroLivrosEmprestados;
    }

    public int getMaxEmprestimos() {
        return maxEmprestimos;
    }

    public void setMaxEmprestimos(int maxEmprestimos) {
        this.maxEmprestimos = maxEmprestimos;
    }

    public void setRegistroLivrosEmprestados(int registroLivrosEmprestados) {
        this.registroLivrosEmprestados = registroLivrosEmprestados;
    }

    public Livro getLivroSolicitado() {
        return livroSolicitado;
    }

    public ArrayList <LogLivro> getLogLivros() {
        if(logLivros == null){
            logLivros = new ArrayList<>();
        }
        return logLivros;
    }

    public static void solicitarEmprestimo(Usuario usuario) {
        if (!(usuario instanceof Leitor)) {
            JOptionPane.showMessageDialog(null, "Usuário inválido.");
            return;
        }

        Leitor leitorAtual = (Leitor) usuario;

        if (leitorAtual.isPenalty()) {
            JOptionPane.showMessageDialog(null, "Você está penalizado e não pode solicitar empréstimos.");
            return;
        }

        if (leitorAtual.isSolicitaEmprestimo()) {
            JOptionPane.showMessageDialog(null, "Você já solicitou um empréstimo.");
            return;
        }

        if (leitorAtual.getLivrosEmprestados().size() >= leitorAtual.getMaxEmprestimos() + 2) {
            JOptionPane.showMessageDialog(null, "Você atingiu o limite máximo de empréstimos.");
            return;
        }

        carregarLivros();

        if (getLivros().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum livro disponível para empréstimo.");
            return;
        }

        StringBuilder listaLivros = new StringBuilder();
        for (int i = 0; i < getLivros().size(); i++) {
            Livro livro = getLivros().get(i);
            listaLivros.append(i).append(" - ISBN: ").append(livro.getISBN()).append("\n")
                    .append("Título: ").append(livro.getTitulo()).append("\n")
                    .append("Autor: ").append(livro.getAutor()).append("\n")
                    .append("Editora: ").append(livro.getEditora()).append("\n")
                    .append("Ano de Publicação: ").append(livro.getAnoDePublicacao()).append("\n")
                    .append("Categoria: ").append(livro.getCategoria()).append("\n")
                    .append("Número de Exemplares: ").append(livro.getNumeroDeExemplares()).append("\n\n");
        }

        String inputIdLivro = JOptionPane.showInputDialog("Selecione o número do livro que deseja solicitar empréstimo:\n" + listaLivros.toString());
        if (inputIdLivro == null || inputIdLivro.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Número do livro não fornecido.");
            return;
        }

        int idLivroDesejado;
        try {
            idLivroDesejado = Integer.parseInt(inputIdLivro);
            if (idLivroDesejado < 0 || idLivroDesejado >= getLivros().size()) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Número do livro inválido.");
            return;
        }

        Livro livro = getLivros().get(idLivroDesejado);
        if (livro.getNumeroDeExemplares() > 0) {
            leitorAtual.setSolicitaEmprestimo(true);
            leitorAtual.setLivroSolicitado(livro);

            LogLivro log = new LogLivro();
            log.setLivro(livro);
            log.setIsafterDate(false);
            log.setDevolved(false);
            log.setWasAccepted(false);

            leitorAtual.getLogLivros().add(log);
            livro.setNumeroDeSolicitacoes(livro.getNumeroDeSolicitacoes() + 1);

            salvarPerfisLeitor();

            JOptionPane.showMessageDialog(null, "Solicitação de empréstimo registrada com sucesso. Aguarde a confirmação.");
        } else {
            JOptionPane.showMessageDialog(null, "Não há exemplares disponíveis para o livro selecionado.");
        }
    }

    public void setPenalty(boolean penalty) {
        isPenalty = penalty;
    }

    public static void devolverLivro(Usuario usuario) {
        if (!(usuario instanceof Leitor)) {
            JOptionPane.showMessageDialog(null, "Usuário inválido.");
            return;
        }

        Leitor leitorAtual = (Leitor) usuario;

        if (leitorAtual.getLivrosEmprestados().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Você não tem livros emprestados.");
            return;
        }

        StringBuilder listaLivrosEmprestados = new StringBuilder();
        for (int i = 0; i < leitorAtual.getLivrosEmprestados().size(); i++) {
            LivroEmprestado livroEmprestado = leitorAtual.getLivrosEmprestados().get(i);
            Livro livro = livroEmprestado.getLivro();
            listaLivrosEmprestados.append(i).append(" - ISBN: ").append(livro.getISBN()).append("\n")
                    .append("Título: ").append(livro.getTitulo()).append("\n")
                    .append("Autor: ").append(livro.getAutor()).append("\n")
                    .append("Editora: ").append(livro.getEditora()).append("\n")
                    .append("Ano de Publicação: ").append(livro.getAnoDePublicacao()).append("\n")
                    .append("Categoria: ").append(livro.getCategoria()).append("\n")
                    .append("Data de Empréstimo: ").append(livroEmprestado.getDataEmprestimo()).append("\n")
                    .append("Data de Vencimento: ").append(livroEmprestado.getDataVencimento()).append("\n\n");
        }

        String inputIdLivro = JOptionPane.showInputDialog("Digite o número do livro que deseja devolver:\n" + listaLivrosEmprestados.toString());
        if (inputIdLivro == null || inputIdLivro.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Número do livro não fornecido.");
            return;
        }

        int idLivroDesejado;
        try {
            idLivroDesejado = Integer.parseInt(inputIdLivro);
            if (idLivroDesejado < 0 || idLivroDesejado >= leitorAtual.getLivrosEmprestados().size()) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Número do livro inválido.");
            return;
        }

        LivroEmprestado livroEmprestadoDevolvido = leitorAtual.getLivrosEmprestados().remove(idLivroDesejado);
        Livro livroDevolvido = livroEmprestadoDevolvido.getLivro();

        LocalDate dataAtual = LocalDate.now();
        boolean isAtrasado = dataAtual.isAfter(livroEmprestadoDevolvido.getDataVencimento());

        LogLivro log = new LogLivro();
        log.setLivro(livroDevolvido);
        log.setIsafterDate(isAtrasado);
        log.setDevolved(true);
        log.setWasAccepted(true);

        leitorAtual.getLogLivros().add(log);

        boolean livroAtualizado = false;
        for (Livro livro : getLivros()) {
            if (livro.getISBN() == (livroDevolvido.getISBN())) {
                livro.setNumeroDeExemplares(livro.getNumeroDeExemplares() + 1);
                livroAtualizado = true;
                break;
            }
        }

        if (!livroAtualizado) {
            getLivros().add(livroDevolvido);
        }

        leitorAtual.setSolicitaEmprestimo(false);
        salvarLivros();
        salvarPerfisLeitor();

        JOptionPane.showMessageDialog(null, "Livro devolvido com sucesso.");
    }

    public static void verLogs() {
        if (logLivros == null) {
            logLivros = new ArrayList<>();
        }

        if (logLivros.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Não há logs disponíveis.");
            return;
        }

        StringBuilder logs = new StringBuilder();
        for (LogLivro log : logLivros) {
            Livro livro = log.livro;
            logs.append("ISBN: ").append(livro.getISBN()).append("\n")
                    .append("Título: ").append(livro.getTitulo()).append("\n")
                    .append("Autor: ").append(livro.getAutor()).append("\n")
                    .append("Editora: ").append(livro.getEditora()).append("\n")
                    .append("Ano de Publicação: ").append(livro.getAnoDePublicacao()).append("\n")
                    .append("Categoria: ").append(livro.getCategoria()).append("\n");

            if (log.isDevolved()) {
                if (log.isafterDate) {
                    logs.append("O livro foi devolvido com atraso.\n");
                } else {
                    logs.append("O livro foi devolvido no prazo.\n");
                }
            } else {
                if (log.wasAccepted) {
                    if (log.isafterDate) {
                        logs.append("O livro foi emprestado, mas está atrasado.\n");
                    } else {
                        logs.append("Empréstimo aceito e livro está em dia.\n");
                    }
                } else {
                    logs.append("O empréstimo não foi aceito.\n");
                }
            }

            logs.append("========================================\n");
        }

        JOptionPane.showMessageDialog(null, logs.toString(), "Logs do Leitor", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void logEmprestimos(Usuario leitorAtual) {
        if (leitorAtual == null) {
            JOptionPane.showMessageDialog(null, "Leitor não autenticado.");
            return;
        }

        // Cria uma janela para exibir o log
        JFrame frame = new JFrame("Livros Emprestados");
        JTextArea textArea = new JTextArea(20, 50);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        StringBuilder sb = new StringBuilder();

        if (((Leitor)leitorAtual).getLivrosEmprestados() != null && !((Leitor)leitorAtual).getLivrosEmprestados().isEmpty()) {
            sb.append("Leitor: ").append(leitorAtual.getNome()).append("\n");
            for (LivroEmprestado livroEmprestado : ((Leitor)leitorAtual).getLivrosEmprestados()) {
                Livro livro = livroEmprestado.getLivro();
                sb.append(" - ISBN: ").append(livro.getISBN()).append("\n")
                        .append("   Título: ").append(livro.getTitulo()).append("\n")
                        .append("   Autor: ").append(livro.getAutor()).append("\n")
                        .append("   Editora: ").append(livro.getEditora()).append("\n")
                        .append("   Ano de Publicação: ").append(livro.getAnoDePublicacao()).append("\n")
                        .append("   Categoria: ").append(livro.getCategoria()).append("\n")
                        .append("   Data de Empréstimo: ").append(livroEmprestado.getDataEmprestimo()).append("\n")
                        .append("   Data de Vencimento: ").append(livroEmprestado.getDataVencimento()).append("\n\n");
            }
            sb.append("-------\n");
        } else {
            sb.append("Nenhum livro emprestado no momento.\n");
        }

        // Exibe o log na janela
        textArea.setText(sb.toString());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void exibirLivrosMaisSolicitados() {
        // Obtém a lista de livros
        ArrayList<Livro> livros = getLivros();

        // Ordena os livros com base no número de solicitações
        livros.sort((l1, l2) -> Integer.compare(l2.getNumeroDeSolicitacoes(), l1.getNumeroDeSolicitacoes()));

        // Cria uma janela para exibir o log
        JFrame frame = new JFrame("Livros com Maior Número de Solicitações");
        JTextArea textArea = new JTextArea(20, 50);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        StringBuilder sb = new StringBuilder();
        sb.append("Top 10 Livros com Maior Número de Solicitações:\n");

        // Limita a 10 livros mais solicitados
        int maxLivros = Math.min(10, livros.size());
        for (int i = 0; i < maxLivros; i++) {
            Livro livro = livros.get(i);
            sb.append("Título: ").append(livro.getTitulo()).append("\n")
                    .append("Categoria: ").append(livro.getCategoria()).append("\n")
                    .append("Número de Solicitações: ").append(livro.getNumeroDeSolicitacoes()).append("\n\n");
        }

        if (livros.isEmpty()) {
            sb.append("Nenhum livro solicitado até o momento.");
        }

        // Exibe o log na janela
        textArea.setText(sb.toString());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    public void setLogLivros(ArrayList<LogLivro> logLivros) {
        this.logLivros = logLivros;
    }

    public void setLivroSolicitado(Livro livroSolicitado) {
        this.livroSolicitado = livroSolicitado;
    }
}
