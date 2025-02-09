package perfis;
import biblioteca.Livro;
import biblioteca.LivroEmprestado;
import javax.swing.*;
import java.awt.*;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Random;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static biblioteca.Biblioteca.*;
import static biblioteca.LogLivro.atualizarOuAdicionarLog;

public class Administrador extends Usuario implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public Administrador(String nome, String senha, String email, boolean tipo,boolean log, String id) {
        super(nome, senha, email, tipo, false, id);
    }

    public static void cadastraLivro() {
        Random random = new Random();
        Livro livro = new Livro();

        try {
            // Define o ISBN do livro
            livro.setISBN(random.nextInt(10000000));

            // Solicita o título do livro
            livro.setTitulo(JOptionPane.showInputDialog("Digite o título do livro: "));

            // Solicita o nome do autor e valida
            String autor;
            do {
                autor = JOptionPane.showInputDialog("Digite o nome do autor: ");
                if (autor != null && !autor.isEmpty() && autor.chars().noneMatch(Character::isDigit)) {
                    livro.setAutor(autor);
                    break;
                }
                JOptionPane.showMessageDialog(null, "Nome do autor inválido! O nome não pode conter números.");
            } while (true);

            livro.setEditora(JOptionPane.showInputDialog("Digite o nome da editora: "));

            int ano;
            do {
                String inputAno = JOptionPane.showInputDialog("Digite o ano de publicação: ");
                try {
                    ano = Integer.parseInt(inputAno);
                    if (ano >= 1 && ano <= LocalDate.now().getYear()) {
                        livro.setAnoDePublicacao(ano);
                        break;
                    } else {
                        JOptionPane.showMessageDialog(null, "Ano de publicação inválido! Deve estar entre 1 e " + LocalDate.now().getYear() + ".");
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Ano de publicação inválido! Deve ser um número.");
                }
            } while (true);

            // Solicita a categoria do livro usando um painel
            String categoria = selecionarCategoria();
            if (categoria != null) {
                livro.setCategoria(categoria);
            } else {
                JOptionPane.showMessageDialog(null, "Nenhuma categoria selecionada.");
                return;
            }

            // Solicita a quantidade de exemplares e valida
            int quantidadeExemplares;
            do {
                String inputQuantidade = JOptionPane.showInputDialog("Digite a quantidade de exemplares disponíveis: ");
                try {
                    quantidadeExemplares = Integer.parseInt(inputQuantidade);
                    if (quantidadeExemplares >= 1) {
                        livro.setNumeroDeExemplares(quantidadeExemplares);
                        break;
                    } else {
                        JOptionPane.showMessageDialog(null, "Quantidade de exemplares inválida! Deve ser maior que 0.");
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Quantidade de exemplares inválida! Deve ser um número.");
                }
            } while (true);

            livro.setNew(true);
            livro.setDataAdicao(LocalDate.now());

            setLivros(livro);
            salvarLivros();
            JOptionPane.showMessageDialog(null, "Cadastro efetuado com sucesso.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar livro: " + e.getMessage());
        }
    }

    private static String selecionarCategoria() {
        String[] categorias = {"Ficção", "Não-Ficção", "Mistério", "Sci-Fi", "Biografia", "Fantasia"};

        // Cria o painel com um JComboBox para selecionar a categoria
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Selecione a categoria do livro:");
        painel.add(label);

        JComboBox<String> comboBox = new JComboBox<>(categorias);
        painel.add(comboBox);

        int resultado = JOptionPane.showConfirmDialog(null, painel, "Categoria", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            return (String) comboBox.getSelectedItem();
        } else {
            return null;
        }
    }

    public static void alteraLivro() {
        carregarLivros();
        String inputISBN = JOptionPane.showInputDialog("Digite o ISBN do livro que deseja modificar:");
        int idLivroDesejado;

        try {
            idLivroDesejado = Integer.parseInt(inputISBN);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "O ISBN deve ser um número.");
            return;
        }

        Livro livroEncontrado = getLivros().stream()
                .filter(livro -> livro.getISBN() == idLivroDesejado)
                .findFirst()
                .orElse(null);

        if (livroEncontrado != null) {
            // Modificação dos detalhes do livro usando painéis para entradas de dados
            String titulo = JOptionPane.showInputDialog("Digite o novo título do livro:", livroEncontrado.getTitulo());
            String autor = obterNomeAutor(livroEncontrado.getAutor());
            String editora = JOptionPane.showInputDialog("Digite a nova editora do livro:", livroEncontrado.getEditora());

            int ano = obterAnoPublicacao(livroEncontrado.getAnoDePublicacao());
            String categoria = selecionarCategoria(livroEncontrado.getCategoria());
            int quantidadeExemplares = obterQuantidadeExemplares(livroEncontrado.getNumeroDeExemplares());

            // Atualiza os dados do livro
            livroEncontrado.setTitulo(titulo);
            livroEncontrado.setAutor(autor);
            livroEncontrado.setEditora(editora);
            livroEncontrado.setAnoDePublicacao(ano);
            livroEncontrado.setCategoria(categoria);
            livroEncontrado.setNumeroDeExemplares(quantidadeExemplares);

            salvarLivros();

            JOptionPane.showMessageDialog(null, "Dados do livro alterados com sucesso.");
        } else {
            JOptionPane.showMessageDialog(null, "Livro não encontrado.");
        }
    }

    private static String obterNomeAutor(String autorAtual) {
        String autor;
        do {
            autor = JOptionPane.showInputDialog("Digite o novo autor do livro:", autorAtual);
            if (autor != null && !autor.isEmpty() && autor.chars().noneMatch(Character::isDigit)) {
                return autor;
            }
            JOptionPane.showMessageDialog(null, "Nome do autor inválido! O nome não pode conter números.");
        } while (true);
    }

    private static int obterAnoPublicacao(int anoAtual) {
        int ano;
        do {
            String inputAno = JOptionPane.showInputDialog("Digite o novo ano de publicação:", anoAtual);
            try {
                ano = Integer.parseInt(inputAno);
                if (ano >= 1 && ano <= LocalDate.now().getYear()) {
                    return ano;
                } else {
                    JOptionPane.showMessageDialog(null, "Ano de publicação inválido! Deve estar entre 1 e " + LocalDate.now().getYear() + ".");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Ano de publicação inválido! Deve ser um número.");
            }
        } while (true);
    }

    public static void removerLivro() {
        carregarLivros();
        JPanel painel = new JPanel(new BorderLayout());
        JList<Livro> listaLivros = new JList<>(getLivros().toArray(new Livro[0]));
        JScrollPane scrollPane = new JScrollPane(listaLivros);
        painel.add(scrollPane, BorderLayout.CENTER);

        JButton botaoRemover = new JButton("Remover Livro Selecionado");
        painel.add(botaoRemover, BorderLayout.SOUTH);

        int resultado = JOptionPane.showConfirmDialog(null, painel, "Selecione o Livro para Remover", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            Livro livroSelecionado = listaLivros.getSelectedValue();
            if (livroSelecionado != null) {
                int isbn = livroSelecionado.getISBN();
                boolean livroRemovido = getLivros().removeIf(l -> l.getISBN() == isbn);
                if (livroRemovido) {
                    boolean removidoDoSet = getLivros().remove(Integer.valueOf(isbn));
                    if (removidoDoSet) {
                        salvarLivros();
                        JOptionPane.showMessageDialog(null, "Livro com ISBN " + isbn + " removido com sucesso.");
                    } else {
                        JOptionPane.showMessageDialog(null, "ISBN " + isbn + " não encontrado no conjunto.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Livro com ISBN " + isbn + " não encontrado na lista.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Nenhum livro selecionado.");
            }
        }
    }





    private static String selecionarCategoria(String categoriaAtual) {
        // Define a lista de categorias
        String[] categorias = {"Ficção", "Não-Ficção", "Mistério", "Sci-Fi", "Biografia", "Fantasia"};

        // Cria o painel com um JComboBox para selecionar a categoria
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Selecione a nova categoria do livro:");
        painel.add(label);

        JComboBox<String> comboBox = new JComboBox<>(categorias);
        comboBox.setSelectedItem(categoriaAtual);
        painel.add(comboBox);

        int resultado = JOptionPane.showConfirmDialog(null, painel, "Categoria", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            return (String) comboBox.getSelectedItem();
        } else {
            return null;
        }
    }

    private static int obterQuantidadeExemplares(int quantidadeAtual) {
        int quantidadeExemplares;
        do {
            String inputQuantidade = JOptionPane.showInputDialog("Digite a nova quantidade de exemplares:", quantidadeAtual);
            try {
                quantidadeExemplares = Integer.parseInt(inputQuantidade);
                if (quantidadeExemplares >= 1) {
                    return quantidadeExemplares;
                } else {
                    JOptionPane.showMessageDialog(null, "Quantidade de exemplares inválida! Deve ser maior que 0.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Quantidade de exemplares inválida! Deve ser um número.");
            }
        } while (true);
    }


    public static void emprestarLivro() {
        carregarLivros();
        carregarPerfisLeitor();

        if (getLivros().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum livro disponível para empréstimo.");
            return;
        }

        StringBuilder listaLeitores = new StringBuilder();
        for (int i = 0; i < getLeitores().size(); i++) {
            Leitor leitor = getLeitores().get(i);
            if (leitor.isSolicitaEmprestimo()) {
                listaLeitores.append("ID: ").append(i).append("\n")
                        .append("Nome: ").append(leitor.getNome()).append("\n")
                        .append("Email: ").append(leitor.getEmail()).append("\n")
                        .append("Número de Livros Emprestados: ").append(leitor.getRegistroLivrosEmprestados()).append("\n\n");
            }
        }

        if (listaLeitores.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum leitor com solicitação de empréstimo.");
            return;
        }

        JOptionPane.showMessageDialog(null, listaLeitores.toString(), "Leitores com Solicitação de Empréstimo", JOptionPane.INFORMATION_MESSAGE);

        String inputIdLeitor = JOptionPane.showInputDialog("Digite o ID do leitor para confirmar o empréstimo:");
        if (inputIdLeitor == null || inputIdLeitor.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "ID do leitor não fornecido.");
            return;
        }

        int idLeitorDesejado;
        try {
            idLeitorDesejado = Integer.parseInt(inputIdLeitor);
            if (idLeitorDesejado < 0 || idLeitorDesejado >= getLeitores().size()) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID do leitor inválido.");
            return;
        }

        Leitor leitor = getLeitores().get(idLeitorDesejado);

        if (leitor.isPenalty()) {
            JOptionPane.showMessageDialog(null, "O leitor está penalizado e não pode emprestar livros.");


            atualizarOuAdicionarLog(leitor, leitor.getLivroSolicitado(), false, true);

            salvarPerfisLeitor();
            return;
        }

        Livro livroSolicitado = leitor.getLivroSolicitado();
        if (livroSolicitado == null) {
            JOptionPane.showMessageDialog(null, "Nenhum livro solicitado pelo leitor.");

            // Atualiza o log de empréstimo para o leitor com `isAccepted = false`
            atualizarOuAdicionarLog(leitor, livroSolicitado, false, true);

            salvarPerfisLeitor();
            return;
        }

        boolean livroEncontrado = false;
        for (Livro livro : getLivros()) {
            if (livro.getISBN() == livroSolicitado.getISBN()) {
                livroEncontrado = true;
                if (livro.getNumeroDeExemplares() > 0) {
                    livro.setNumeroDeExemplares(livro.getNumeroDeExemplares() - 1);
                    LocalDate dataEmprestimo = LocalDate.now();
                    LocalDate dataVencimento = dataEmprestimo.minusMonths(1);
                    LivroEmprestado livroEmprestado = new LivroEmprestado(livro, dataEmprestimo, dataVencimento);

                    // Inicializa a lista de livros emprestados se for null
                    if (leitor.getLivrosEmprestados() == null) {
                        leitor.setLivrosEmprestados(new ArrayList<>());
                    }

                    leitor.getLivrosEmprestados().add(livroEmprestado);
                    leitor.setRegistroLivrosEmprestados(leitor.getRegistroLivrosEmprestados() + 1);

                    // Reseta a solicitação de empréstimo
                    leitor.setLivroSolicitado(null);
                    leitor.setSolicitaEmprestimo(false);

                    // Atualiza o log de empréstimo para o leitor com `isAccepted = true`
                    atualizarOuAdicionarLog(leitor, livroSolicitado, true, true);

                    // Salva as alterações
                    salvarLivros();
                    salvarPerfisLeitor();

                    JOptionPane.showMessageDialog(null, "Livro emprestado com sucesso. Prazo de devolução: 1 mês.");
                } else {
                    JOptionPane.showMessageDialog(null, "Não há exemplares disponíveis para o livro solicitado.");

                    // Atualiza o log de empréstimo para o leitor com `isAccepted = false`
                    atualizarOuAdicionarLog(leitor, livroSolicitado, false, true);

                    salvarPerfisLeitor();
                }
                break;
            }
        }

        if (!livroEncontrado) {
            JOptionPane.showMessageDialog(null, "Livro não encontrado.");
            atualizarOuAdicionarLog(leitor, livroSolicitado, false, true);
            salvarPerfisLeitor();
        }
    }

    public static void estatisticasBiblioteca() {
        carregarLivros();
        carregarPerfisLeitor();
        carregarPerfisAdmin();
        ArrayList<Livro> livros = getLivros() != null ? getLivros() : new ArrayList<>();
        ArrayList<Leitor> leitores = getLeitores() != null ? getLeitores() : new ArrayList<>();
        ArrayList<Administrador> administradores = getAdministradores() != null ? getAdministradores() : new ArrayList<>();

        int totalLivros = livros.size();
        int totalLeitores = leitores.size();
        int totalAdministradores = administradores.size();
        int totalLivrosEmprestados = 0;
        AtomicInteger totalLivrosComPendencias = new AtomicInteger();
        int totalLivrosAdicionadosRecentemente = 0;

        LocalDate umaSemanaAtras = LocalDate.now().minusWeeks(1);

        totalLivrosEmprestados = leitores.stream()
                .filter(leitor -> leitor.getLivrosEmprestados() != null)
                .flatMap(leitor -> leitor.getLivrosEmprestados().stream())
                .peek(livroEmprestado -> {
                    if (livroEmprestado.getDataVencimento().isBefore(LocalDate.now())) {
                        totalLivrosComPendencias.getAndIncrement();
                    }
                })
                .collect(Collectors.counting()).intValue();

        // Contabiliza livros adicionados recentemente usando streams
        totalLivrosAdicionadosRecentemente = (int) livros.stream()
                .filter(livro -> livro.getDataAdicao().isAfter(umaSemanaAtras))
                .count();

        // Cria a janela para exibir as estatísticas
        JFrame frame = new JFrame("Estatísticas da Biblioteca");
        JTextArea textArea = new JTextArea(20, 50);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Monta o conteúdo do texto
        StringBuilder sb = new StringBuilder();
        sb.append("Estatísticas da Biblioteca:\n")
                .append("Número Total de Livros: ").append(totalLivros).append("\n")
                .append("Número Total de Leitores: ").append(totalLeitores).append("\n")
                .append("Número Total de Administradores: ").append(totalAdministradores).append("\n")
                .append("Número Total de Livros Emprestados: ").append(totalLivrosEmprestados).append("\n")
                .append("Número de Livros com Pendências: ").append(totalLivrosComPendencias.get()).append("\n")
                .append("Número de Livros Adicionados Recentemente (última semana): ").append(totalLivrosAdicionadosRecentemente).append("\n");

        // Exibe o texto na janela
        textArea.setText(sb.toString());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }



    public static void gerirUsuarios() {
        String[] opcoes = {"Gerenciar Leitores", "Gerenciar Administradores", "Voltar"};
        int escolha = JOptionPane.showOptionDialog(null,
                "Escolha a opção de gerenciamento:",
                "Gerenciar Usuários",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opcoes,
                opcoes[0]);

        switch (escolha) {
            case 0:
                gerenciarLeitores();
                break;
            case 1:
                gerenciarAdministradores();
                break;
            case 2:
                return;
            default:
                JOptionPane.showMessageDialog(null, "Opção inválida.");
                break;
        }
    }

    private static void gerenciarLeitores() {
        if (getLeitores().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum leitor cadastrado.");
            return;
        }

        String[] opcoesLeitores = getLeitores().stream()
                .map(leitor -> leitor.getNome() + " - " + leitor.getId())
                .toArray(String[]::new);

        String leitorEscolhido = (String) JOptionPane.showInputDialog(
                null,
                "Selecione o leitor para gerenciar:",
                "Gerenciar Leitor",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoesLeitores,
                opcoesLeitores[0]
        );

        if (leitorEscolhido != null) {
            String idLeitorEscolhido = leitorEscolhido.split(" - ")[1];
            Leitor leitor = getLeitores().stream()
                    .filter(l -> l.getId().equals(idLeitorEscolhido))
                    .findFirst()
                    .orElse(null);

            if (leitor != null) {
                alterarInformacoesLeitor(leitor);
            } else {
                JOptionPane.showMessageDialog(null, "Leitor não encontrado.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nenhum leitor selecionado.");
        }
    }

    private static void gerenciarAdministradores() {
        if (getAdministradores().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum administrador cadastrado.");
            return;
        }

        String[] opcoesAdmins = getAdministradores().stream()
                .map(admin -> admin.getNome() + " - " + admin.getId())
                .toArray(String[]::new);

        String adminEscolhido = (String) JOptionPane.showInputDialog(
                null,
                "Selecione o administrador para gerenciar:",
                "Gerenciar Administrador",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoesAdmins,
                opcoesAdmins[0]
        );

        if (adminEscolhido != null) {
            String idAdminEscolhido = adminEscolhido.split(" - ")[1];
            Administrador admin = getAdministradores().stream()
                    .filter(a -> a.getId().equals(idAdminEscolhido))
                    .findFirst()
                    .orElse(null);

            if (admin != null) {
                alterarInformacoesAdmin(admin);
            } else {
                JOptionPane.showMessageDialog(null, "Administrador não encontrado.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nenhum administrador selecionado.");
        }
    }

    private static void alterarInformacoesLeitor(Leitor leitor) {
        JPanel painel = new JPanel();
        painel.setLayout(new GridLayout(5, 2));

        JTextField nomeField = new JTextField(leitor.getNome());
        JPasswordField senhaField = new JPasswordField(leitor.getSenha());
        JTextField emailField = new JTextField(leitor.getEmail());
        JCheckBox penalidadeCheckBox = new JCheckBox("Marcar como inadimplente", leitor.isShouldNotify());

        painel.add(new JLabel("Nome:"));
        painel.add(nomeField);
        painel.add(new JLabel("Senha:"));
        painel.add(senhaField);
        painel.add(new JLabel("Email:"));
        painel.add(emailField);
        painel.add(new JLabel("Penalidade:"));
        painel.add(penalidadeCheckBox);

        int resultado = JOptionPane.showConfirmDialog(null, painel, "Alterar Informações do Leitor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            leitor.setNome(nomeField.getText());
            leitor.setSenha(new String(senhaField.getPassword()));
            leitor.setEmail(emailField.getText());
            leitor.setShouldNotify(penalidadeCheckBox.isSelected());

            salvarPerfisLeitor();
            JOptionPane.showMessageDialog(null, "Informações do leitor alteradas com sucesso.");
        }
    }

    private static void alterarInformacoesAdmin(Administrador admin) {
        JPanel painel = new JPanel();
        painel.setLayout(new GridLayout(3, 2));

        JTextField nomeField = new JTextField(admin.getNome());
        JPasswordField senhaField = new JPasswordField(admin.getSenha());
        JTextField emailField = new JTextField(admin.getEmail());

        painel.add(new JLabel("Nome:"));
        painel.add(nomeField);
        painel.add(new JLabel("Senha:"));
        painel.add(senhaField);
        painel.add(new JLabel("Email:"));
        painel.add(emailField);

        int resultado = JOptionPane.showConfirmDialog(null, painel, "Alterar Informações do Administrador", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            admin.setNome(nomeField.getText());
            admin.setSenha(new String(senhaField.getPassword()));
            admin.setEmail(emailField.getText());

            salvarPerfisAdmin();
            JOptionPane.showMessageDialog(null, "Informações do administrador alteradas com sucesso.");
        }
    }

}
